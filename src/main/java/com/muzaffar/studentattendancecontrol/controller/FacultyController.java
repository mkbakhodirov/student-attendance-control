package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.request.FacultyRequestDTO;
import com.muzaffar.studentattendancecontrol.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody FacultyRequestDTO facultyRequestDTO) {
        Integer facultyId = facultyService.add(facultyRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(facultyId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> get(@PathVariable Integer id) {
        return ResponseEntity.ok(facultyService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getList() {
        return ResponseEntity.ok(facultyService.getList());
    }

    @PutMapping("{id}")
    public ResponseEntity<Faculty> update(@PathVariable Integer id, @RequestBody FacultyRequestDTO facultyRequestDTO) {
        return ResponseEntity.ok(facultyService.update(id, facultyRequestDTO));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        facultyService.delete(id);
    }

    @GetMapping("download")
    public void download(HttpServletResponse response) {
        File file = facultyService.getFile();
        try {
            facultyService.download(response, file);
        } catch (IOException ioException) {
            response.setStatus(500);
        }
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(MultipartFile file) {
        List<Faculty> faculties = facultyService.uploadExcel(file);
        if (faculties == null)
            return ResponseEntity.badRequest().body("Send Excel file");
        if (faculties.isEmpty())
            throw new UniqueException("Faculties names repeated");
        return ResponseEntity.status(HttpStatus.CREATED).body(faculties);
    }

}
