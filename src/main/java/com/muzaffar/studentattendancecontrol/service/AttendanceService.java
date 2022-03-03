package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.request.AttendanceRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.AttendanceRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService implements BaseService<AttendanceRequestDTO, Attendance> {

    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;

    @Override
    public Integer add(AttendanceRequestDTO attendanceRequestDTO) {
        Integer studentId = attendanceRequestDTO.getStudentId();
        Student student = studentService.get(studentId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime arrivalTime = LocalDateTime.parse(attendanceRequestDTO.getArrivalTime(), formatter);
        LocalDateTime departureTime = LocalDateTime.parse(attendanceRequestDTO.getDepartureTime(), formatter);
        Attendance attendance = new Attendance(arrivalTime, departureTime, student);
        return attendanceRepository.save(attendance).getId();
    }

    public Integer arrive(Integer studentId) {
        Student student = studentService.get(studentId);
        Attendance attendance = new Attendance();
        attendance.setArrivalTime(LocalDateTime.now());
        attendance.setStudent(student);
        return attendanceRepository.save(attendance).getId();
    }

    public Integer departure(Integer studentId) {
        Student student = studentService.get(studentId);
        Optional<Attendance> optional = attendanceRepository.findFirstByStudentIdAndDepartureTimeOrderByArrivalTimeDesc(studentId, null);
        if (optional.isEmpty()) {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDepartureTime(LocalDateTime.now());
            return attendanceRepository.save(attendance).getId();
        }
        Attendance attendance = optional.get();
        attendance.setUpdateTime(LocalDateTime.now());
        attendance.setDepartureTime(LocalDateTime.now());
        return attendanceRepository.save(attendance).getId();
    }

    @Override
    public List<Attendance> getList() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getList(Integer studentId) {
        Student student = studentService.get(studentId);
        return student.getAttendances();
    }

    @Override
    public Attendance get(Integer id) {
        Optional<Attendance> optional = attendanceRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Attendance is not found");
    }

    @Override
    public Attendance update(Integer id, AttendanceRequestDTO attendanceRequestDTO) {
        Attendance attendance = get(id);
        Student student = studentService.get(attendanceRequestDTO.getStudentId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime arrivalTime = LocalDateTime.parse(attendanceRequestDTO.getArrivalTime(), formatter);
        LocalDateTime departureTime = LocalDateTime.parse(attendanceRequestDTO.getDepartureTime(), formatter);
        attendance.setStudent(student);
        attendance.setArrivalTime(arrivalTime);
        attendance.setDepartureTime(departureTime);
        attendance.setUpdateTime(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public void delete(Integer id) {
        boolean exists = attendanceRepository.existsById(id);
        if (!exists)
            throw new NotFoundException("Attendance is not found");
        attendanceRepository.deleteById(id);
    }

}
