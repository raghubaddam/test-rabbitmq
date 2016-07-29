package com.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class TestRabbitmqApplication {


	final static String queueName = "spring-boot";

	final static String routingKey = "routingKey";

	public static final String SPRING_BOOT_EXCHANGE = "spring-boot-exchange";

	final static String deadLetterQueue = "spring-boot-dead";

	final static String routingKeyDead = "routingKeyDead";

	@Value("${rabbitMQAccessURI}${rabbitMQAccessVirtualHost}")
	private String uri;


	@Bean
	public Queue queue() {
		Map<String, Object> args = new HashMap<String, Object>();
		// The default exchange
		args.put("x-dead-letter-exchange", SPRING_BOOT_EXCHANGE);
		// Route to the incoming queue when the TTL occurs
		args.put("x-dead-letter-routing-key", routingKeyDead);
		// TTL 5 seconds
		args.put("x-message-ttl", 5000);
		return new Queue(queueName, false, false, false, args);
	}

	/*@Bean
	public Queue queue() {

		return new Queue(queueName, false, false, false);
	}*/



	@Bean
	TopicExchange exchange() {
		return new TopicExchange(SPRING_BOOT_EXCHANGE);
	}


	@Bean
	Binding binding(@Qualifier("queue") Queue queue, TopicExchange exchange) {

		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	Binding bindingDeadLetter(@Qualifier("queueDead") Queue queueDead, TopicExchange exchange) {

		return BindingBuilder.bind(queueDead).to(exchange).with(routingKeyDead);
	}

	@Bean
	Queue queueDead() {
		return new Queue(deadLetterQueue, false);
	}


	@Bean
	public CachingConnectionFactory cachingConnectionFactory() throws URISyntaxException {
		ConnectionFactory factory = new ConnectionFactory();

		try {
			factory.setUri(uri);

		} catch (KeyManagementException | NoSuchAlgorithmException  e) {
			throw new BeanCreationException("Failed to create Rabbit MQ connection factory", e);
		}

        return new CachingConnectionFactory(factory);
	}

	@Bean
	public RabbitTemplate rabbitTemplate() throws URISyntaxException {
		RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory());
		template.setChannelTransacted(true);
		template.setMandatory(true);
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		template.setMessageConverter(converter);
		return template;
	}
	/*@Bean
	SimpleMessageListenerContainer container(MessageListenerAdapter listenerAdapter) throws URISyntaxException {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(cachingConnectionFactory());
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	Receiver receiver() {
		return new Receiver();
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}*/

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(TestRabbitmqApplication.class, args);
	}
//
//	@Override
//	public void run(String... args) throws Exception {
//		System.out.println("Sending message...");
//		//rabbitTemplate().convertAndSend(SPRING_BOOT_EXCHANGE, routingKey, "Hello from RabbitMQ!");
//	}


}