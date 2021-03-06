package com.muzaffar.studentattendancecontrol.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.dto.AttendanceForwardDto;
import com.muzaffar.studentattendancecontrol.model.dto.AttendanceRequestDTO;
import com.muzaffar.studentattendancecontrol.rabbitmq.Sender;
import com.muzaffar.studentattendancecontrol.repository.jpa.AttendanceRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService implements BaseService<AttendanceRequestDTO, Attendance> {

    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final Sender sender;

    @Override
    public String add(AttendanceRequestDTO attendanceRequestDTO) {
        String studentId = attendanceRequestDTO.getStudentId();
        Student student = studentService.get(studentId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime arrivalTime = LocalDateTime.parse(attendanceRequestDTO.getArrivalTime(), formatter);
        LocalDateTime departureTime = LocalDateTime.parse(attendanceRequestDTO.getDepartureTime(), formatter);
        Attendance attendance = new Attendance(arrivalTime, departureTime, student);
        return attendanceRepository.save(attendance).getId();
    }

    public String arrive(String studentId) {
        Student student = studentService.get(studentId);
        Attendance attendance = new Attendance();
        attendance.setArrivalTime(LocalDateTime.now());
        attendance.setStudent(student);
        return attendanceRepository.save(attendance).getId();
    }

    public String departure(String studentId) {
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
    public List<Attendance> getList(String studentId) {
        Student student = studentService.get(studentId);
        return student.getAttendances();
    }

    @Override
    public Attendance get(String id) {
        Optional<Attendance> optional = attendanceRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Attendance ID = " + id + " is not found");
    }

    @Override
    public Attendance update(String id, AttendanceRequestDTO attendanceRequestDTO) {
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
    public void delete(String id) {
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
                        case 1 -> row.getCell(j).setCellValue(attendance.getId());
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
            File file1 = new File("file/downloadExcel/attendances.xlsx");
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

    public File getFile(String studentId) {
        try {
            File file = new File("file/base/student-attendances.xlsx");
            InputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            List<Attendance> attendances = getList(studentId);
            Student student = attendances.get(0).getStudent();
            Row row = sheet.getRow(2);
            for (int j = 1; j < 5; j++) {
                switch (j) {
                    case 1 -> row.getCell(j).setCellValue(student.getId());
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
                        case 1 -> row.getCell(j).setCellValue(attendance.getId());
                        case 2 -> row.getCell(j).setCellValue(attendance.getArrivalTime().format(formatter));
                        case 3 -> row.getCell(j).setCellValue(attendance.getDepartureTime().format(formatter));
                    }
                }
            }
            fis.close();
            File file1 = new File("file/downloadExcel/student-attendances.xlsx");
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

    @Override
    public List<Attendance> uploadExcel(MultipartFile file) {
        List<Attendance> attendances = new ArrayList<>();
        try {
            InputStream inputStream1 = file.getInputStream();
            Workbook wb = new XSSFWorkbook(inputStream1);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Attendance attendance = new Attendance();
                int j = 0;
                for (Cell cell : iterator.next()) {
                    if (cell.getCellType().equals(CellType.BLANK))
                        return attendanceRepository.saveAll(attendances);
                    switch (j) {
                        case 0 -> {
                            String studentId = cell.getStringCellValue();
                            Student student = studentService.get(studentId);
                            attendance.setStudent(student);
                        }
                        case 1 -> attendance.setArrivalTime(cell.getLocalDateTimeCellValue());
                        case 2 -> attendance.setDepartureTime(cell.getLocalDateTimeCellValue());
                    }
                    j++;
                }
                attendances.add(attendance);
            }
            return attendanceRepository.saveAll(attendances);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    public List<AttendanceForwardDto> send() {
        List<Attendance> attendances = attendanceRepository.findAllBySent(false);
        List<AttendanceForwardDto> list = new ArrayList<>();
        for (Attendance attendance : attendances) {
            AttendanceForwardDto attendanceForwardDto = modelMapper.map(attendance, AttendanceForwardDto.class);
            attendanceForwardDto.setStudentId(attendance.getStudent().getId());
            list.add(attendanceForwardDto);
        }
        return list;
    }

    public void sendToRabbit(AttendanceRequestDTO attendanceRequestDTO, String attendanceId) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(attendanceRequestDTO);
        sender.send(json);
        attendanceRepository.updateSent(attendanceId);
    }
}

