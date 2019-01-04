package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class SolrDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage= (ObjectMessage) message; //获取消息
            Long[] ids = (Long[]) objectMessage.getObject(); //转化为数组
            System.out.println("获取的ids:"+ids);
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
