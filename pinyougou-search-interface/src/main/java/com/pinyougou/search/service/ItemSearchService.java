package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索
     * @param
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 批量导入商品的sku
     * @param list
     */
    public void importItem(List list);

    /**
     * 删除商品
     * */
    public void deleteByGoodsIds(List goodsIdList);

}
