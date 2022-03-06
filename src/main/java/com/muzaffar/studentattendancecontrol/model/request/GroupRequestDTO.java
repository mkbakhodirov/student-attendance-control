package com.muzaffar.studentattendancecontrol.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GroupRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private Integer facultyId;
}
