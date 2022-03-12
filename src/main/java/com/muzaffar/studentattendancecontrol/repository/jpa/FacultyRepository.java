package com.muzaffar.studentattendancecontrol.repository.jpa;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, String> {
    boolean existsByName(String name);
}
