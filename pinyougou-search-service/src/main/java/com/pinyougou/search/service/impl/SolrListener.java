package com.pinyougou.search.service.impl;


import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class SolrListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage= (TextMessage) message;
            String text = textMessage.getText();  //获取到json字符创
            System.out.println("监听到消息:"+text);
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class); //转化为对象
            itemSearchService.importItem(itemList);
            System.out.println("导入索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
