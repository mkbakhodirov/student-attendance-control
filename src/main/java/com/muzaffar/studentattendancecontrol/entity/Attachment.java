package com.muzaffar.studentattendancecontrol.entity;

import com.muzaffar.studentattendancecontrol.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Attachment extends BaseEntity {
    private String originalFileName;
    private String contentType;
    private long size;
    private String name;
}
