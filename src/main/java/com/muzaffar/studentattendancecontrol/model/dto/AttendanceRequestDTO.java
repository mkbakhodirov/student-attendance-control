package com.muzaffar.studentattendancecontrol.model.dto;

import lombok.Data;

@Data
public class AttendanceRequestDTO {
    private String studentId;
    private String arrivalTime;
    private String departureTime;
}
