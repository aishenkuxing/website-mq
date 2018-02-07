package com.cn.website.mq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

	private final static String QUEUE_NAME = "hello";

	private static String getMessage(String[] strings) {
		if (strings.length < 1)
			return "Hello World!";
		return joinStrings(strings, " ");
	}

	private static String joinStrings(String[] strings, String delimiter) {
		int length = strings.length;
		if (length == 0)
			return "";
		StringBuilder words = new StringBuilder(strings[0]);
		for (int i = 1; i < length; i++) {
			words.append(delimiter).append(strings[i]);
		}
		return words.toString();
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		/**  
         * 创建连接连接到RabbitMQ  
         */  
		ConnectionFactory factory = new ConnectionFactory();
		
		 // 设置MabbitMQ所在主机ip或者主机名  
		factory.setHost("localhost");
		
		 // 创建一个连接  
		Connection connection = factory.newConnection();
		
		// 创建一个频道    
		Channel channel = connection.createChannel();

		String message = getMessage(args);

		//持久消息队列
		boolean durable = true;
		
		 // 指定一个队列    
		channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

		// 往队列中发出一条消息   
		channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		
		System.out.println(" [x] 发送 '" + message + "'");
		   
		// 关闭频道和连接    
	    channel.close();
	   
	    connection.close();
	}

}
