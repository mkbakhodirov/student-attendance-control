package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Attachment;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.MissRequiredParam;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.exception.NotValidParamException;
import com.muzaffar.studentattendancecontrol.repository.AttachmentRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttachmentService implements BaseService<MultipartHttpServletRequest, Attachment> {

    private final AttachmentRepository attachmentRepository;
    public static final String FILE_PACKAGE = "file/attachment/";

    @Override
    public Integer add(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        return add(file);
    }

    public Integer add(MultipartFile file) {
        if (file == null)
            throw new MissRequiredParam("No file was sent");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null)
            throw new NotValidParamException("File does not have name");
        String contentType = file.getContentType();
        long size = file.getSize();
        String name = UUID.randomUUID() + "." +
                originalFilename.substring(originalFilename.indexOf("."));
        Path path = Paths.get(FILE_PACKAGE + name);
        try {
            Files.copy(file.getInputStream(), path);
            Attachment attachment = new Attachment(originalFilename, contentType, size, name);
            return attachmentRepository.save(attachment).getId();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Attachment> getList() {
        return attachmentRepository.findAll();
    }

    @Override
    public List<Attachment> getList(Integer studentId) {
        return null;
    }

    @Override
    public Attachment get(Integer id) {
        Optional<Attachment> optional = attachmentRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Attachment ID = " + id + " is not found");
    }

    @Override
    public Attachment update(Integer id, MultipartHttpServletRequest request) {
        Attachment attachment = get(id);
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if (file == null)
            throw new MissRequiredParam("No file was sent");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null)
            throw new NotValidParamException("File does not have name");
        String contentType = file.getContentType();
        long size = file.getSize();
        File file1 = new File(FILE_PACKAGE + attachment.getName());
        file1.deleteOnExit();
        String name = UUID.randomUUID() + "." +
                originalFilename.substring(originalFilename.indexOf("."));
        Path path = Paths.get(FILE_PACKAGE + name);
        try {
            Files.copy(file.getInputStream(), path);
            attachment.setName(name);
            attachment.setContentType(contentType);
            attachment.setOriginalFileName(originalFilename);
            attachment.setSize(size);
            attachment.setUpdateTime(LocalDateTime.now());
            return attachmentRepository.save(attachment);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        Attachment attachment = get(id);
        File file = new File(FILE_PACKAGE + attachment.getName());
        file.deleteOnExit();
        attachmentRepository.delete(attachment);
    }

    @Override
    public File getFile() {
        return null;
    }

    public void download(HttpServletResponse response, Integer id) throws IOException {
        Attachment attachment = get(id);
        InputStream inputStream = new FileInputStream(FILE_PACKAGE + attachment.getName());
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                attachment.getOriginalFileName() + "\"");
        response.setContentType("application/force-download");
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    @Override
    public List<Attachment> uploadExcel(MultipartFile file) {
        return null;
    }
}
