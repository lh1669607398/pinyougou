package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000) //配置延时
public class ItemSearchServiceimpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

       /* Map<String, Object> map = new HashMap();
        //添加查询条件
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> items = page.getContent();
        for (TbItem item : items) {
            System.out.println(item.getTitle());
        }
        map.put("rows", page.getContent());
        return map;*/
        //根据关键字查询
        Map<String, Object> map = new HashMap();
        map.putAll(searchList(searchMap));
        //根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //查询出品牌和规格列表
        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            map.putAll(searchBrandAndSpecList(category));
        } else {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }
        return map;
    }

    //显示高亮
    public Map<String, Object> searchList(Map searchMap) {
        //空格处理
        String keywordsStr = (String) searchMap.get("keywords");
        String keywords = keywordsStr.replace(" ", "");

        Map<String, Object> map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //1.1按关键字查询
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //1.2过滤商品分类
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3过滤品牌
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4规格过滤
        if ((searchMap.get("spec")) != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (Object key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec" + key).is(searchMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.5价格区间过滤
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");  //0-500
            if (!price[0].equals('0')) {  //如果价格不等于0
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals('*')) { //如果价格不等于*
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.6分页
        //提取页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        //展示条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 40;
        }

        query.setOffset((pageNo - 1) * pageSize);//从第几条开始查询
        query.setRows(pageSize);  //每页记录数
        //1.7排序
        String sortField = (String) searchMap.get("sortField");//排序字段
        String sortValue = (String) searchMap.get("sort");//获取排序方式  ASC  DESC
        //判断
        if (sortField!=null&&!sortValue.equals("")){
            if (("ASC").equals(sortValue)){
                Sort sort= new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (("DESC").equals(sortValue)){
                Sort sort= new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }

        }

        //***********获取高亮结果集******************
        //高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList) {
            //获取高亮列表
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlightList) {
                List<String> snipplets = highlight.getSnipplets();
                System.out.println(snipplets);
            }
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());  //返回的总页数
        map.put("total", page.getTotalElements());    //返回的总记录数
        return map;

    }

    //商品分类分组
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        //根据关键字查询
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组项
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分页组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分页结果入口集
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历入口集合
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());//将分组结果集的名称封装到list集合中
        }

        return list;
    }

    /**
     * 查询品牌和规格列表
     *
     * @param category
     * @return
     */

    public Map<String, Object> searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //获取模板id
        long typedId = (long) redisTemplate.boundHashOps("tbItemCat").get(category);
        if (typedId + "" != null) {
            //获取品牌
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typedId);
            //获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typedId);
            //添加进map集合并返回
            map.put("brandList", brandList);
            map.put("specList", specList);
        }

        return map;

    }

    /**
     * 批量导入商品的sku
     * @param list
     */
    @Override
    public void importItem(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsList) {
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }


}
