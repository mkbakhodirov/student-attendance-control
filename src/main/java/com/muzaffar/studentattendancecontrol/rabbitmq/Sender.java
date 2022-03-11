package com.muzaffar.studentattendancecontrol.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Sender {

    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public void send(String json) {
//        rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, "foo.bar.baz", json);
        rabbitTemplate.convertAndSend(queue.getName(), json);
    }
}
