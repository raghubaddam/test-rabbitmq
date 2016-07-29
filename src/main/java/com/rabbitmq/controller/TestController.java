package com.rabbitmq.controller;

import com.rabbitmq.domain.Person;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    final static String routingKey = "routingKey";

    final String SPRING_BOOT_EXCHANGE = "spring-boot-exchange";

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Person> postMessage(@RequestParam Person person){

        rabbitTemplate.convertAndSend(SPRING_BOOT_EXCHANGE, routingKey, person);
        System.out.print("Message sent !!!");

        return new ResponseEntity<Person>(HttpStatus.OK);

    }

    @RequestMapping(value = "/message/dead-letter-queue", method = RequestMethod.POST)
    public ResponseEntity<String> putMessageToDeadLetterQueue(@RequestParam String message){

        rabbitTemplate.convertAndSend(SPRING_BOOT_EXCHANGE, routingKey, message);
        System.out.print("Message sent !!!");
        return new ResponseEntity<String>(HttpStatus.OK);

    }
}
