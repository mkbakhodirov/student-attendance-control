package com.muzaffar.studentattendancecontrol.controller;

import com.itextpdf.text.DocumentException;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.model.dto.StudentRequestDTO;
import com.muzaffar.studentattendancecontrol.service.AttachmentService;
import com.muzaffar.studentattendancecontrol.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
        String attachmentId = attachmentService.add(file);
        if (attachmentId == null)
            return ResponseEntity.internalServerError().body("File cannot be saved");
        studentRequestDTO.setAttachmentId(attachmentId);
        String studentId = studentService.add(studentRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(studentId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(studentService.get(id));
    }

    @GetMapping("/byGroup/{groupId}")
    public ResponseEntity<?> getList(@PathVariable String groupId) {
        return ResponseEntity.ok(studentService.getList(groupId));
    }

    @GetMapping
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok(studentService.getList());
    }

    @GetMapping("search/byFullName")
    public ResponseEntity<?> getList(@RequestParam("lastName") String lastName,
                                     @RequestParam("firstName") String firstName
    ) {
        return ResponseEntity.ok(studentService.getList(lastName, firstName));
    }

    @GetMapping("search/byLastName")
    public ResponseEntity<?> getListByLastName(@RequestParam("lastName") String lastName) {
        return ResponseEntity.ok(studentService.getListByLastName(lastName));
    }

    @GetMapping("search/byGroupName")
    public ResponseEntity<?> getListByGroupName(@RequestParam("groupName") String groupName) {
        return ResponseEntity.ok(studentService.getListByGroupName(groupName));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.ok(studentService.update(id, studentRequestDTO));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        studentService.delete(id);
    }

    @GetMapping("download/excel")
    public void download(HttpServletResponse response) {
        File file = studentService.getFile();
        try {
            studentService.download(response, file);
        } catch (IOException ioException) {
            response.setStatus(500);
        }
    }

    @PostMapping("upload/excel")
    public ResponseEntity<?> upload(MultipartFile file) {
        List<Student> students = studentService.uploadExcel(file);
        if (students == null)
            return ResponseEntity.badRequest().body("Send Excel file");
        return ResponseEntity.status(HttpStatus.CREATED).body(students);
    }

    @GetMapping("download/pdf/{id}")
    public void downloadPdf(@PathVariable String id, HttpServletResponse response) {
        try {
            File file = studentService.getPdf(id);
            studentService.download(response, file);
        } catch (IOException | DocumentException exception) {
            exception.printStackTrace();
            response.setStatus(500);
        }
    }
}
