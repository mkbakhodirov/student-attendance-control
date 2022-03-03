package com.muzaffar.studentattendancecontrol.repository;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findFirstByStudentIdAndDepartureTimeOrderByArrivalTimeDesc(Integer studentId, LocalDateTime departureTime);
}
