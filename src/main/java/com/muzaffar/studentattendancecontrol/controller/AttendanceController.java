package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.model.dto.AttendanceRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.AttendanceRepository;
import com.muzaffar.studentattendancecontrol.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AttendanceRequestDTO attendanceRequestDTO) throws Exception {
        Integer attendanceId = attendanceService.add(attendanceRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(attendanceId).toUri();
        attendanceService.sendToRabbit(attendanceRequestDTO, attendanceId);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("arrive")
    public ResponseEntity<?> arrive(@RequestParam("studentId") Integer studentId) {
        Integer attendanceId = attendanceService.arrive(studentId);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(attendanceId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("departure")
    public ResponseEntity<?> departure(@RequestParam("studentId") Integer studentId) {
        Integer attendanceId = attendanceService.departure(studentId);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(attendanceId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Attendance> get(@PathVariable Integer id) {
        return ResponseEntity.ok(attendanceService.get(id));
    }

    @GetMapping("/byStudent/{studentId}")
    public ResponseEntity<?> getList(@PathVariable Integer studentId) {
        return ResponseEntity.ok(attendanceService.getList(studentId));
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getList() {
        return ResponseEntity.ok(attendanceService.getList());
    }

    @PutMapping("{id}")
    public ResponseEntity<Attendance> update(@PathVariable Integer id, @RequestBody AttendanceRequestDTO attendanceRequestDTO) {
        return ResponseEntity.ok(attendanceService.update(id, attendanceRequestDTO));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        attendanceService.delete(id);
    }

    @GetMapping("download")
    public void download(HttpServletResponse response) {
        File file = attendanceService.getFile();
        try {
            attendanceService.download(response, file);
        } catch (IOException ioException) {
            response.setStatus(500);
        }
    }

    @GetMapping("download/byStudent/{studentId}")
    public void downloadByStudent(@PathVariable Integer studentId, HttpServletResponse response) {
        File file = attendanceService.getFile(studentId);
        try {
            attendanceService.download(response, file);
        } catch (IOException ioException) {
            response.setStatus(500);
        }
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(MultipartFile file) {
        List<Attendance> attendances = attendanceService.uploadExcel(file);
        if (attendances == null)
            return ResponseEntity.badRequest().body("Send Excel file");
        return ResponseEntity.status(HttpStatus.CREATED).body(attendances);
    }
}
