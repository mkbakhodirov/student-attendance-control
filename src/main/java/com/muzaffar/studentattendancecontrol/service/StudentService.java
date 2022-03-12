package com.muzaffar.studentattendancecontrol.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.muzaffar.studentattendancecontrol.entity.Attachment;
import com.muzaffar.studentattendancecontrol.entity.Attendance;
import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.entity.Student;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.model.dto.StudentRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.elasticsearch.StudentElasticRepo;
import com.muzaffar.studentattendancecontrol.repository.jpa.StudentRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StudentService implements BaseService<StudentRequestDTO, Student> {

    private final StudentRepository studentRepository;
    private final StudentElasticRepo studentElasticRepo;
    private final GroupService groupService;
    private final AttachmentService attachmentService;
    private final ModelMapper modelMapper;

    @Override
    public String add(StudentRequestDTO studentRequestDTO) {
        Group group = groupService.get(studentRequestDTO.getGroupId());
        Attachment attachment = attachmentService.get(studentRequestDTO.getAttachmentId());
        Student student = new Student();
        String firstName = studentRequestDTO.getFirstName();
        String lastName = studentRequestDTO.getLastName();
        String patronymic = studentRequestDTO.getPatronymic();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setPatronymic(patronymic);
        student.setBirthDate(LocalDate.parse(studentRequestDTO.getBirthDate()));
        student.setGroup(group);
        student.setAttachment(attachment);
        return studentRepository.save(student).getId();
    }

    @Override
    public List<Student> getList() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getList(String groupId) {
        Group group = groupService.get(groupId);
        return group.getStudents();
    }

    @Override
    public Student get(String id) {
        Optional<Student> optional = studentRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Student ID = " + id + " is not found");
    }

    @Override
    public Student update(String id, StudentRequestDTO studentRequestDTO) {
        Student student = get(id);
        Attachment attachment = attachmentService.get(studentRequestDTO.getAttachmentId());
        Group group = groupService.get(studentRequestDTO.getGroupId());
        String firstName = studentRequestDTO.getFirstName();
        String lastName = studentRequestDTO.getLastName();
        String patronymic = studentRequestDTO.getPatronymic();
        LocalDate birthDate = LocalDate.parse(studentRequestDTO.getBirthDate());
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setPatronymic(patronymic);
        student.setBirthDate(birthDate);
        student.setGroup(group);
        student.setAttachment(attachment);
        student.setUpdateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public void delete(String id) {
        boolean exists = studentRepository.existsById(id);
        if (!exists)
            throw new NotFoundException("Student is not found");
        studentRepository.deleteById(id);
    }

    @Override
    public File getFile() {
        try {
            File file = new File("file/base/students.xlsx");
            InputStream inputStream = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            List<Student> students = getList();
            int index = 2;
            for (int i = index; i < index + students.size(); i++) {
                Row row = sheet.getRow(i);
                for (int j = 1; j < 8; j++) {
                    Student student = students.get(i - 2);
                    switch (j) {
                        case 1 -> row.getCell(j).setCellValue(student.getId());
                        case 2 -> row.getCell(j).setCellValue(student.getLastName());
                        case 3 -> row.getCell(j).setCellValue(student.getFirstName());
                        case 4 -> row.getCell(j).setCellValue(student.getPatronymic());
                        case 5 -> row.getCell(j).setCellValue(String.valueOf(student.getBirthDate()));
                        case 6 -> row.getCell(j).setCellValue(student.getGroup().getName());
                        case 7 -> row.getCell(j).setCellValue(student.getGroup().getFaculty().getName());
                    }
                }
            }
            inputStream.close();
            File file1 = new File("file/downloadExcel/students.xlsx");
            boolean isSuccess = file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file1);
            wb.write(outputStream);
            outputStream.close();
            wb.close();
            return file1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> uploadExcel(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        try {
            InputStream inputStream1 = file.getInputStream();
            Workbook wb = new XSSFWorkbook(inputStream1);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Student student = new Student();
                int j = 0;
                for (Cell cell : iterator.next()) {
                    if (cell.getCellType().equals(CellType.BLANK))
                        return studentRepository.saveAll(students);
                    switch (j) {
                        case 0 -> student.setLastName(cell.getStringCellValue());
                        case 1 -> student.setFirstName(cell.getStringCellValue());
                        case 2 -> student.setPatronymic(cell.getStringCellValue());
                        case 3 -> {
                            Date dateCellValue = cell.getDateCellValue();
                            LocalDate birthDate = dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            student.setBirthDate(birthDate);
                        }
                        case 4 -> {
                            String groupId = cell.getStringCellValue();
                            Group group = groupService.get(groupId);
                            student.setGroup(group);
                        }
                    }
                    j++;
                }
                students.add(student);
            }
            return studentRepository.saveAll(students);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    public File getPdf(String id) throws DocumentException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Document document = new Document();
        File file = new File(DOWNLOAD_PDF + "student.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Student student = get(id);
        String name = student.getAttachment().getName();

        Image image = Image.getInstance(AttachmentService.FILE_PACKAGE + name);
        image.scaleAbsolute(75f, 75f);
        image.setAlignment(Element.ALIGN_CENTER);
        document.add(image);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);

        PdfPTable table = new PdfPTable(5);
        table.setSpacingBefore(15f);
        table.setSpacingAfter(15f);
        Stream.of("ID", "Full Name", "Birth Date", "Group", "Faculty")
                        .forEach(columnTitle -> {
                            Chunk chunk = new Chunk(columnTitle, font);
                            PdfPCell header = new PdfPCell(new Phrase(chunk));
                            header.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table.addCell(header);
                        });
        String fullName = String.format("%s %s %s", student.getLastName(), student.getFirstName(), student.getPatronymic());
        table.addCell(new PdfPCell(new Phrase(student.getId())));
        table.addCell(new PdfPCell(new Phrase(fullName)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(student.getBirthDate()))));
        table.addCell(new PdfPCell(new Phrase(student.getGroup().getName())));
        table.addCell(new PdfPCell(new Phrase(student.getGroup().getFaculty().getName())));
        document.add(table);

        PdfPTable table1 = new PdfPTable(3);
        Stream.of("ID", "Arrival Time", "Departure Time")
                .forEach(columnTitle -> {
                    Chunk chunk = new Chunk(columnTitle, font);
                    PdfPCell header = new PdfPCell(new Phrase(chunk));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table1.addCell(header);
                });
        for (Attendance attendance : student.getAttendances()) {
            table1.addCell(new PdfPCell(new Phrase(attendance.getId())));
            table1.addCell(new PdfPCell(new Phrase(attendance.getArrivalTime().format(formatter))));
            table1.addCell(new PdfPCell(new Phrase(attendance.getDepartureTime().format(formatter))));
        }
        document.add(table1);

        document.close();
        return file;
    }

    public List<Student> getListByLastName(String lastName) {
        System.out.println(1);
        List<Student> list = studentElasticRepo.findByLastName(lastName);
        System.out.println(list);
        return list;
    }

    public List<Student> getList(String lastName, String firstName) {
        return studentElasticRepo.findAllByLastNameAndFirstName(lastName, firstName);
    }

    public List<Student> getListByGroupName(String groupName) {
        return studentElasticRepo.findAllByGroup_Name(groupName);
    }
}
