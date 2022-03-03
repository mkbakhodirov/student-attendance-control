package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Attachment;
import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.request.StudentRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.StudentRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        throw new NotFoundException("Student is not found");
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

}
