package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.request.GroupRequestDTO;
import com.muzaffar.studentattendancecontrol.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody GroupRequestDTO groupRequestDTO) {
        Integer groupId = groupService.add(groupRequestDTO);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(groupId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Group> get(@PathVariable Integer id) {
        return ResponseEntity.ok(groupService.get(id));
    }

    @GetMapping("/byFaculty/{facultyId}")
    public ResponseEntity<?> getList(@PathVariable Integer facultyId) {
        return ResponseEntity.ok(groupService.getList(facultyId));
    }

    @GetMapping
    public ResponseEntity<List<Group>> getList() {
        return ResponseEntity.ok(groupService.getList());
    }

    @PutMapping("{id}")
    public ResponseEntity<Group> update(@PathVariable Integer id, @RequestBody GroupRequestDTO groupRequestDTO) {
        return ResponseEntity.ok(groupService.update(id, groupRequestDTO));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        groupService.delete(id);
    }

    @GetMapping("download")
    public void download(HttpServletResponse response) {
        File file = groupService.getFile();
        try {
            groupService.download(response, file);
        } catch (IOException ioException) {
            response.setStatus(500);
        }
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(MultipartFile file) {
        List<Group> groups = groupService.uploadExcel(file);
        if (groups == null)
            return ResponseEntity.badRequest().body("Send Excel file");
        if (groups.isEmpty()) {
            throw new UniqueException("Group names repeated");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(groups);
    }

}
