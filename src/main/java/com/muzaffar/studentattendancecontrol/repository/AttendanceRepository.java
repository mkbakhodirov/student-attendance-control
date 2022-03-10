package com.muzaffar.studentattendancecontrol.repository;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findFirstByStudentIdAndDepartureTimeOrderByArrivalTimeDesc(Integer studentId, LocalDateTime departureTime);
    List<Attendance> findAllBySent(boolean sent);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update attendance set sent = true where id = ?1", nativeQuery = true)
    void updateSent(Integer id);
}
