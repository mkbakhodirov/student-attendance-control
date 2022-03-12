package com.muzaffar.studentattendancecontrol.service.base;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public interface BaseService<T, R> {
    String DOWNLOAD_PDF = "file/downloadPdf/";

    String add(T t);
    List<R> getList();
    List<R> getList(String id);
    R get(String id);
    void delete(String id);
    R update(String id, T t);
    File getFile();
    List<R> uploadExcel(MultipartFile file);
    default void download(HttpServletResponse response, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                file.getName() + "\"");
        response.setContentType("application/force-download");
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }


}
