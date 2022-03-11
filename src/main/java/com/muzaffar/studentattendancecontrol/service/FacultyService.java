package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.dto.FacultyRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.FacultyRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FacultyService implements BaseService<FacultyRequestDTO, Faculty> {

    private final FacultyRepository facultyRepository;

    @Override
    public Integer add(FacultyRequestDTO facultyRequestDTO) {
        String name = facultyRequestDTO.getName();
        boolean exists = facultyRepository.existsByName(name);
        if (exists)
            throw new UniqueException("Faculty with name " + name + " already exists");
        Faculty faculty = new Faculty(name);
        return facultyRepository.save(faculty).getId();
    }

    @Override
    public List<Faculty> getList() {
       return facultyRepository.findAll();
    }

    @Override
    public Faculty get(Integer id) {
        Optional<Faculty> optional = facultyRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Faculty ID = " + id + " is not found");
    }

    @Override
    public Faculty update(Integer id, FacultyRequestDTO facultyRequestDTO) {
        Optional<Faculty> optional = facultyRepository.findById(id);
        if (optional.isPresent()) {
            Faculty faculty = optional.get();
            String name = facultyRequestDTO.getName();
            boolean exists = facultyRepository.existsByName(name);
            if (exists)
                throw new UniqueException("Faculty with name " + name + " already exists");
            faculty.setName(name);
            faculty.setUpdateTime(LocalDateTime.now());
            return facultyRepository.save(faculty);
        }
        throw new NotFoundException("Faculty is not found");
    }

    @Override
    public void delete(Integer id) {
        boolean exists = facultyRepository.existsById(id);
        if (!exists)
            throw new NotFoundException("Faculty is not found");
        facultyRepository.deleteById(id);
    }

    @Override
    public List<Faculty> getList(Integer id) {
        return null;
    }

    @Override
    public File getFile() {
        try {
            File file = new File("file/base/faculties.xlsx");
            InputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            List<Faculty> faculties = getList();
            int index = 2;
            for (int i = index; i < index + faculties.size(); i++) {
                Row row = sheet.getRow(i);
                for (int j = 1; j < 3; j++) {
                    Faculty faculty = faculties.get(i - 2);
                    switch (j) {
                        case 1 -> row.getCell(j).setCellValue(String.valueOf(faculty.getId()));
                        case 2 -> row.getCell(j).setCellValue(faculty.getName());
                    }
                }
            }
            fis.close();
            File file1 = new File("file/downloadExcel/faculties.xlsx");
            boolean isSuccess = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file1);
            wb.write(fos);
            fos.close();
            wb.close();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Faculty> uploadExcel(MultipartFile file) {
        List<Faculty> faculties = new ArrayList<>();
        try {
            InputStream inputStream1 = file.getInputStream();
            Workbook wb = new XSSFWorkbook(inputStream1);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Faculty faculty = new Faculty();
                for (Cell cell : iterator.next()) {
                    if (cell.getCellType().equals(CellType.BLANK))
                        return facultyRepository.saveAll(faculties);
                    String name = cell.getStringCellValue();
                    boolean exists = facultyRepository.existsByName(name);
                    if (exists)
                        throw new UniqueException("Faculty with name " + name + " already exists");
                    faculty.setName(name);
                }
                faculties.add(faculty);
            }
            return facultyRepository.saveAll(faculties);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
