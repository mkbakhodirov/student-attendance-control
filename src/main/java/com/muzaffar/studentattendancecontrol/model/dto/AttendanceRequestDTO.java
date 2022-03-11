package com.muzaffar.studentattendancecontrol.model.dto;

import lombok.Data;

@Data
public class AttendanceRequestDTO {
    private Integer studentId;
    private String arrivalTime;
    private String departureTime;
}
