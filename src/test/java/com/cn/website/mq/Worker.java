package com.cn.website.mq;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 消息自动重传
 * @author Administrator
 *
 */
public class Worker {
	private final static String TASK_QUEUE_NAME = "task_queue";

	private static void doWork(String task) throws InterruptedException {
		for (char ch : task.toCharArray()) {
			if (ch == '.')
				Thread.sleep(1000);
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		//创建信道
		final Channel channel = connection.createChannel();
		channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

		//设置任务满载状况 一次只能一个任务
		int prefetchCount = 1;
		
		channel.basicQos(prefetchCount);

		System.out.println(new Date().getTime() + ": [*] Waiting for messages. To exit press CTRL+C");

		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				System.out.println(" [x] Received '" + message + "'");
				try {
					doWork(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		 //这里auto=false表示打开应答机制，关闭后消息 自动重传
		 boolean autoAck = false;
		 channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer);
	}
}
