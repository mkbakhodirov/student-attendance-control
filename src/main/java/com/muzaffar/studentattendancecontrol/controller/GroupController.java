package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.model.request.GroupRequestDTO;
import com.muzaffar.studentattendancecontrol.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
}
