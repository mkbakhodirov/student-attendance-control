package com.muzaffar.studentattendancecontrol.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.muzaffar.studentattendancecontrol.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Student extends BaseEntity {
    private String lastName;
    private String firstName;
    private String patronymic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @OneToOne(cascade = CascadeType.ALL)
    private Attachment attachment;
    @ManyToOne
    private Group group;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student", cascade = CascadeType.ALL)
    private List<Attendance> attendances;
}
