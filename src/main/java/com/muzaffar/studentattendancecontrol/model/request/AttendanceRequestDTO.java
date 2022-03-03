package com.muzaffar.studentattendancecontrol.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRequestDTO {
    private Integer studentId;
    private String arrivalTime;
    private String departureTime;
}
