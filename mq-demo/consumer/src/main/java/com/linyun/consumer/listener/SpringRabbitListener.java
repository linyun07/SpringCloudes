package com.linyun.consumer.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @author zhangqianwei
 * @date 2023/6/2 13:19
 */

@Component
public class SpringRabbitListener {
    @RabbitListener(queues = "simple.queue")
    public void listenerSimpleQueue(String msg) throws InterruptedException {
        System.out.println("消费者1  接收 simple.queue到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenerWorkQueue(String msg) throws InterruptedException {
        System.err.println("消费者2  接收simple.queue到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);
    }

    /**
     * 发布订阅模式
     */

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "fanout.queue1"),
            exchange = @Exchange(name = "linyun.fanout", type = ExchangeTypes.FANOUT)
    ))//广播方式
    public void listenerFanoutQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1  fanout.queue1到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "fanout.queue2"),
            exchange = @Exchange(name = "linyun.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void listenerFanoutQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2  fanout.queue2到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "linyun.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
    ))//路由方式
    public void listenerDirectQueue(String msg) throws InterruptedException {
        System.out.println("消费者1  接收direct.queue1到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "linyun.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}
    ))
    public void listenerDirectQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2  接收direct.queue2到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);
    }


    //话题方式
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "linyun.topic", type = ExchangeTypes.TOPIC),
            value = @Queue("topic.queue1"),
            key = "chain.#"
    ))
    public void listenerTopicQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1  topic.queue1到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "linyun.topic", type = ExchangeTypes.TOPIC),
            value = @Queue("topic.queue2"),
            key = "#.news"
    ))
    public void listenerTopicQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2  topic.queue2到的消息是：" + msg + "--> " + LocalTime.now());
        Thread.sleep(200);
    }
}
