package com.cn.website.mq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;

/**
 * 定义消息队列的发送器
 * @author Administrator
 *
 */
public class SendMq {
	
   private final static String QUEUE_NAME = "hello";
	
   public static void main(String[] args) throws IOException, TimeoutException {
	   //创建链接工厂
	   ConnectionFactory factory = new ConnectionFactory();
	   
	   factory.setHost("127.0.0.1");
	   
	   Connection connection = factory.newConnection();
	   
	   Channel channel = connection.createChannel();
	   
	   channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	   
	   String message = "Hello World!";
	   
	   channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	   
	   System.out.println(" [x] Sent '" + message + "'");
	   
	   channel.close();
	   
	   connection.close();
   }
}
