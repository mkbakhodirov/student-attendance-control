package com.muzaffar.studentattendancecontrol.repository.jpa;

import com.muzaffar.studentattendancecontrol.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
    boolean existsByName(String name);
}
