package com.muzaffar.studentattendancecontrol.repository;

import com.muzaffar.studentattendancecontrol.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    boolean existsByName(String name);
}
