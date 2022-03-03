package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.model.request.GroupRequestDTO;
import com.muzaffar.studentattendancecontrol.model.request.StudentRequestDTO;
import com.muzaffar.studentattendancecontrol.service.AttachmentService;
import com.muzaffar.studentattendancecontrol.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/students")
public class StudentController {

    private final StudentService studentService;
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<?> add(@ModelAttribute StudentRequestDTO studentRequestDTO) {
        MultipartFile file = studentRequestDTO.getFile();
        Integer attachmentId = attachmentService.add(file);
        if (attachmentId == null)
            return ResponseEntity.internalServerError().body("File cannot be saved");
        studentRequestDTO.setAttachmentId(attachmentId);
        Integer studentId = studentService.add(studentRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(studentId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> get(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<Student>> getList() {
        return ResponseEntity.ok(studentService.getList());
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> update(@PathVariable Integer id, @RequestBody StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.ok(studentService.update(id, studentRequestDTO));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        studentService.delete(id);
    }
}
