package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class TopicMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public  void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            String text = (String) objectMessage.getObject();
            System.out.println("传递的字符串"+text);
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("网页生成结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
