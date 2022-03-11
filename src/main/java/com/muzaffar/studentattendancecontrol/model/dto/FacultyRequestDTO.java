package com.muzaffar.studentattendancecontrol.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FacultyRequestDTO {
    @NotBlank(message = "Name should not be empty")
    private String name;
}
