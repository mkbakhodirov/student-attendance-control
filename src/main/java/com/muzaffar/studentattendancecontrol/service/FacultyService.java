package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.request.FacultyRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.FacultyRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        throw new NotFoundException("Faculty is not found");
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
}
