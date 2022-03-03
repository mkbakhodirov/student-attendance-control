package com.muzaffar.studentattendancecontrol.repository;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    boolean existsByName(String name);
}
