package com.muzaffar.studentattendancecontrol.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceForwardDto {
    private Integer id;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private Integer studentId;
}
