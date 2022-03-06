package com.muzaffar.studentattendancecontrol.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class StudentRequestDTO {
    private MultipartFile file;
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    private String patronymic;
    private String birthDate;
    private Integer attachmentId;
    private Integer groupId;
}
