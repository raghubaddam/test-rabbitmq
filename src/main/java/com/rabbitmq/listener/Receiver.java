package com.rabbitmq.listener;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class Receiver implements MessageListener{


    public void onMessage(Message message) {
        System.out.println("Received <" + new String(message.getBody()) + ">");
    }


}
