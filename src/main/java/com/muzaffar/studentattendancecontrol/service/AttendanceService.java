package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.request.AttendanceRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.AttendanceRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    @Override
    public File getFile() {
        try {
            File file = new File("file/base/attendances.xlsx");
            InputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            List<Attendance> attendances = getList();
            int index = 2;
            for (int i = index; i < index + attendances.size(); i++) {
                Row row = sheet.getRow(i);
                for (int j = 1; j < 6; j++) {
                    Attendance attendance = attendances.get(i - 2);
                    Student student = attendance.getStudent();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    switch (j) {
                        case 1 -> row.getCell(j).setCellValue(String.valueOf(attendance.getId()));
                        case 2 -> row.getCell(j).setCellValue(
                                String.format("%s %s %s",
                                        student.getLastName(),
                                        student.getFirstName(),
                                        student.getPatronymic()
                                )
                        );
                        case 3 -> row.getCell(j).setCellValue(student.getGroup().getName());
                        case 4 -> row.getCell(j).setCellValue(attendance.getArrivalTime().format(formatter));
                        case 5 -> row.getCell(j).setCellValue(attendance.getDepartureTime().format(formatter));
                    }
                }
            }
            fis.close();
            File file1 = new File("file/attendances.xlsx");
            boolean isSuccess = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file1);
            wb.write(fos);
            fos.close();
            wb.close();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getFile(Integer studentId) {
        try {
            File file = new File("file/base/student-attendances.xlsx");
            InputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            List<Attendance> attendances = getList(studentId);
            System.out.println(attendances);
            Student student = attendances.get(0).getStudent();
            Row row = sheet.getRow(2);
            for (int j = 1; j < 5; j++) {
                switch (j) {
                    case 1 -> row.getCell(j).setCellValue(String.valueOf(student.getId()));
                    case 2 -> row.getCell(j).setCellValue(
                            String.format("%s %s %s",
                                    student.getLastName(),
                                    student.getFirstName(),
                                    student.getPatronymic()
                            )
                    );
                    case 3 -> row.getCell(j).setCellValue(student.getGroup().getName());
                    case 4 -> row.getCell(j).setCellValue(student.getGroup().getFaculty().getName());
                }
            }
            int index = 5;
            for (int i = index; i < index + attendances.size(); i++) {
                row = sheet.getRow(i);
                for (int j = 1; j < 4; j++) {
                    Attendance attendance = attendances.get(i - 5);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    switch (j) {
                        case 1 -> row.getCell(j).setCellValue(String.valueOf(attendance.getId()));
                        case 2 -> row.getCell(j).setCellValue(attendance.getArrivalTime().format(formatter));
                        case 3 -> row.getCell(j).setCellValue(attendance.getDepartureTime().format(formatter));
                    }
                }
            }
            fis.close();
            File file1 = new File("file/student-attendances.xlsx");
            boolean isSuccess = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file1);
            wb.write(fos);
            fos.close();
            wb.close();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

