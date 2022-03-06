package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Attachment;
import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.request.StudentRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.StudentRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService implements BaseService<StudentRequestDTO, Student> {

    private final StudentRepository studentRepository;
    private final GroupService groupService;
    private final AttachmentService attachmentService;
    private final ModelMapper modelMapper;

    @Override
    public Integer add(StudentRequestDTO studentRequestDTO) {
        Group group = groupService.get(studentRequestDTO.getGroupId());
        Attachment attachment = attachmentService.get(studentRequestDTO.getAttachmentId());
        Student student = new Student();
        String firstName = studentRequestDTO.getFirstName();
        String lastName = studentRequestDTO.getLastName();
        String patronymic = studentRequestDTO.getPatronymic();
        LocalDate birthDate = LocalDate.parse(studentRequestDTO.getBirthDate());
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setPatronymic(patronymic);
        student.setBirthDate(LocalDate.parse(studentRequestDTO.getBirthDate()));
        student.setGroup(group);
        student.setAttachment(attachment);
        return studentRepository.save(student).getId();
    }

    @Override
    public List<Student> getList() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getList(Integer groupId) {
        Group group = groupService.get(groupId);
        return group.getStudents();
    }

    @Override
    public Student get(Integer id) {
        Optional<Student> optional = studentRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Student ID = " + id + " is not found");
    }

    @Override
    public Student update(Integer id, StudentRequestDTO studentRequestDTO) {
        Student student = get(id);
        Attachment attachment = attachmentService.get(studentRequestDTO.getAttachmentId());
        Group group = groupService.get(studentRequestDTO.getGroupId());
        String firstName = studentRequestDTO.getFirstName();
        String lastName = studentRequestDTO.getLastName();
        String patronymic = studentRequestDTO.getPatronymic();
        LocalDate birthDate = LocalDate.parse(studentRequestDTO.getBirthDate());
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setPatronymic(patronymic);
        student.setBirthDate(birthDate);
        student.setGroup(group);
        student.setAttachment(attachment);
        student.setUpdateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public void delete(Integer id) {
        boolean exists = studentRepository.existsById(id);
        if (!exists)
            throw new NotFoundException("Student is not found");
        studentRepository.deleteById(id);
    }

    @Override
    public File getFile() {
        try {
            File file = new File("file/base/students.xlsx");
            InputStream inputStream = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            List<Student> students = getList();
            int index = 2;
            for (int i = index; i < index + students.size(); i++) {
                Row row = sheet.getRow(i);
                for (int j = 1; j < 8; j++) {
                    Student student = students.get(i - 2);
                    switch (j) {
                        case 1 -> row.getCell(j).setCellValue(String.valueOf(student.getId()));
                        case 2 -> row.getCell(j).setCellValue(student.getLastName());
                        case 3 -> row.getCell(j).setCellValue(student.getFirstName());
                        case 4 -> row.getCell(j).setCellValue(student.getPatronymic());
                        case 5 -> row.getCell(j).setCellValue(String.valueOf(student.getBirthDate()));
                        case 6 -> row.getCell(j).setCellValue(student.getGroup().getName());
                        case 7 -> row.getCell(j).setCellValue(student.getGroup().getFaculty().getName());
                    }
                }
            }
            inputStream.close();
            File file1 = new File("file/downloadExcel/students.xlsx");
            boolean isSuccess = file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file1);
            wb.write(outputStream);
            outputStream.close();
            wb.close();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> uploadExcel(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        try {
            InputStream inputStream1 = file.getInputStream();
            Workbook wb = new XSSFWorkbook(inputStream1);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Student student = new Student();
                int j = 0;
                for (Cell cell : iterator.next()) {
                    if (cell.getCellType().equals(CellType.BLANK))
                        return studentRepository.saveAll(students);
                    switch (j) {
                        case 0 -> student.setLastName(cell.getStringCellValue());
                        case 1 -> student.setFirstName(cell.getStringCellValue());
                        case 2 -> student.setPatronymic(cell.getStringCellValue());
                        case 3 -> {
                            Date dateCellValue = cell.getDateCellValue();
                            LocalDate birthDate = dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            student.setBirthDate(birthDate);
                        }
                        case 4 -> {
                            int groupId = (int) cell.getNumericCellValue();
                            Group group = groupService.get(groupId);
                            student.setGroup(group);
                        }
                    }
                    j++;
                }
                students.add(student);
            }
            return studentRepository.saveAll(students);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }
}
