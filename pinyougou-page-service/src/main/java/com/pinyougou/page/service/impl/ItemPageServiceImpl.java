package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            //获取模板
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map map=new HashMap();
            //获取商品列表
            TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods",goods);
            //获取商品扩展表
            TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",goodsDesc);
            //创建一个输入对象
            Writer out= new FileWriter(pagedir+goodsId+".html");
            //输出
            template.process(map, out);
            //释放资源
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
