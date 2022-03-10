package com.muzaffar.studentattendancecontrol.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.muzaffar.studentattendancecontrol.entity.base.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Attendance extends BaseEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;
    @ManyToOne
    private Student student;
    private boolean sent;

    {
        sent = false;
    }

    public Attendance(LocalDateTime arrivalTime, LocalDateTime departureTime, Student student) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.student = student;
    }
}
