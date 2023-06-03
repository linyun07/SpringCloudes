package com.linyun.publisher.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PublisherTest {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test//发送topic消息队列
     public void topicQueueTest() throws InterruptedException {
        String exchangeName = "linyun.topic";
        String mes = "hello,spring amqp topic";
        rabbitTemplate.convertAndSend(exchangeName, "chain.weather", mes);
        log.info("发送成功");
    }

    @Test//发送direct消息队列
    public void directQueueTest() throws InterruptedException {
        String exchangeName = "linyun.direct";
        String mes = "hello,spring amqp direct";
        rabbitTemplate.convertAndSend(exchangeName, "red", mes);
        log.info("发送成功");
    }

    @Test//发送fanout消息队列
    public void fanoutQueueTest() throws InterruptedException {
        String exchangeName = "linyun.fanout";
        String mes = "hello,spring amqp fanout";
        rabbitTemplate.convertAndSend(exchangeName, "", mes);
        log.info("发送成功");
    }

    @Test//循环发送
    public void workQueueTest() throws InterruptedException {
        String queueName = "simple.queue";
        String mes = "hello,spring amqp Work__";
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend(queueName, mes + i);
            Thread.sleep(20);
        }

        log.info("发送成功");
    }

    @Test
    public void simpleQueueTest() {
        String queueName = "simple.queue";
        String mes = "hello,spring amqp";
        rabbitTemplate.convertAndSend(queueName, mes);
        log.info("发送成功");
    }

    @Test
    public void testSendMessage() throws IOException, TimeoutException {
        // 1.建立连接
        ConnectionFactory factory = new ConnectionFactory();
        // 1.1.设置连接参数，分别是：主机名、端口号、vhost、用户名、密码
        factory.setHost("47.120.37.50");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("linyun");
        factory.setPassword("123456");
        // 1.2.建立连接
        Connection connection = factory.newConnection();

        // 2.创建通道Channel
        Channel channel = connection.createChannel();

        // 3.创建队列
        String queueName = "simple.queue";
        channel.queueDeclare(queueName, false, false, false, null);

        // 4.发送消息
        String message = "hello, rabbitmq!";
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println("发送消息成功：【" + message + "】");

        // 5.关闭通道和连接
        channel.close();
        connection.close();

    }
}
