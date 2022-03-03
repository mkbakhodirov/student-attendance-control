package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.entity.Faculty;
import com.muzaffar.studentattendancecontrol.model.request.AttendanceRequestDTO;
import com.muzaffar.studentattendancecontrol.model.request.FacultyRequestDTO;
import com.muzaffar.studentattendancecontrol.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AttendanceRequestDTO attendanceRequestDTO) {
        Integer attendanceId = attendanceService.add(attendanceRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(attendanceId).toUri();
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
}
