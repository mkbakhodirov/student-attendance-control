package com.muzaffar.studentattendancecontrol.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzaffar.studentattendancecontrol.config.RabbitMQConfig;
import com.muzaffar.studentattendancecontrol.model.dto.AttendanceRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class Receiver {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {RabbitMQConfig.queueName})
    public void receiveMessage(String json) throws URISyntaxException, JsonProcessingException {
        AttendanceRequestDTO attendanceRequestDTO = objectMapper.readValue(json, AttendanceRequestDTO.class);
        String url = "http://localhost:8081/api/attendances";
        restTemplate.postForLocation(new URI(url), attendanceRequestDTO);
    }
}
