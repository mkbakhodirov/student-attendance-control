package com.muzaffar.studentattendancecontrol.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.muzaffar.studentattendancecontrol.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "student")
public class StudentModel {
    private String id;
    private String lastName;
    private String firstName;
    private String patronymic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String facultyName;
    private String groupName;
    private String attachmentName;

    public void convert(Student student) {
        StudentModel studentModel = new StudentModel();
        id = student.getId();
        lastName = student.getLastName();
        firstName = student.getFirstName();
        patronymic = student.getPatronymic();
        birthDate = student.getBirthDate();
        facultyName = student.getGroup().getFaculty().getName();
        groupName = student.getGroup().getName();
        attachmentName = student.getAttachment().getName();
    }
}
