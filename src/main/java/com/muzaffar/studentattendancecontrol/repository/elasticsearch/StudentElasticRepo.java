package com.muzaffar.studentattendancecontrol.repository.elasticsearch;

import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.model.StudentModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StudentElasticRepo extends ElasticsearchRepository<StudentModel, String> {
    List<StudentModel> findByLastName(String lastName);
    List<StudentModel> findAllByLastNameAndFirstName(String lastName, String firstName);
    List<StudentModel> findAllByGroupName(String groupName);
}
