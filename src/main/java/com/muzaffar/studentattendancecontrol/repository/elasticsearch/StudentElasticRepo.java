package com.muzaffar.studentattendancecontrol.repository.elasticsearch;

import com.muzaffar.studentattendancecontrol.entity.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StudentElasticRepo extends ElasticsearchRepository<Student, String> {
    List<Student> findByLastName(String lastName);
    List<Student> findAllByLastNameAndFirstName(String lastName, String firstName);
    List<Student> findAllByGroup_Name(String group_name);
}
