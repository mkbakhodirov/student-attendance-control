package com.muzaffar.studentattendancecontrol.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceForwardDto {
    private String id;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private String studentId;
}
