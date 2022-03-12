package com.muzaffar.studentattendancecontrol.repository.jpa;

import com.muzaffar.studentattendancecontrol.entity.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
}
