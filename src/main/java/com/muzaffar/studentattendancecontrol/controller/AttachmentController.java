package com.muzaffar.studentattendancecontrol.controller;

import com.muzaffar.studentattendancecontrol.entity.Attachment;
import com.muzaffar.studentattendancecontrol.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("upload")
    public ResponseEntity<?> add(MultipartHttpServletRequest request) {
        Integer attachmentId = attachmentService.add(request);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(attachmentId).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("download/{id}")
    public void download(@PathVariable Integer id, HttpServletResponse response) {
        try {
            attachmentService.download(response, id);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Attachment> get(@PathVariable Integer id) {
        return ResponseEntity.ok(attachmentService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<Attachment>> getList() {
        return ResponseEntity.ok(attachmentService.getList());
    }

    @PutMapping("{id}")
    public ResponseEntity<Attachment> update(@PathVariable Integer id, MultipartHttpServletRequest request) {
        return ResponseEntity.ok(attachmentService.update(id, request));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        attachmentService.delete(id);
    }
}
