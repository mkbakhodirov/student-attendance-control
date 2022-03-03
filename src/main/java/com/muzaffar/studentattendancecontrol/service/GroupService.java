package com.muzaffar.studentattendancecontrol.service;

import com.muzaffar.studentattendancecontrol.entity.Faculty;
import com.muzaffar.studentattendancecontrol.entity.Group;
import com.muzaffar.studentattendancecontrol.exception.NotFoundException;
import com.muzaffar.studentattendancecontrol.exception.UniqueException;
import com.muzaffar.studentattendancecontrol.model.request.GroupRequestDTO;
import com.muzaffar.studentattendancecontrol.repository.GroupRepository;
import com.muzaffar.studentattendancecontrol.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService implements BaseService<GroupRequestDTO, Group> {

    private final GroupRepository groupRepository;
    private final FacultyService facultyService;

    @Override
    public Integer add(GroupRequestDTO groupRequestDTO) {
        String name = groupRequestDTO.getName();
        boolean exists = groupRepository.existsByName(name);
        if (exists)
            throw new UniqueException("Group with name + " + name + " already exists");
        Faculty faculty = facultyService.get(groupRequestDTO.getFacultyId());
        Group group = new Group(name, faculty);
        return groupRepository.save(group).getId();
    }

    @Override
    public List<Group> getList() {
        return groupRepository.findAll();
    }

    @Override
    public List<Group> getList(Integer facultyId) {
        Faculty faculty = facultyService.get(facultyId);
        return faculty.getGroups();
    }

    @Override
    public Group get(Integer id) {
        Optional<Group> optional = groupRepository.findById(id);
        if (optional.isPresent())
            return optional.get();
        throw new NotFoundException("Group is not found");
    }

    @Override
    public Group update(Integer id, GroupRequestDTO groupRequestDTO) {
        Optional<Group> optional = groupRepository.findById(id);
        if (optional.isPresent()) {
            Faculty faculty = facultyService.get(groupRequestDTO.getFacultyId());
            String name = groupRequestDTO.getName();
            Group group = optional.get();
            boolean exists = groupRepository.existsByName(name);
            if (exists)
                if (group.getFaculty().getId().equals(faculty.getId()))
                    throw new UniqueException("Group with name " + name + " already exists");
            group.setName(name);
            group.setFaculty(faculty);
            group.setUpdateTime(LocalDateTime.now());
            return groupRepository.save(group);
        }
        throw new NotFoundException("Group is not found");
    }

    @Override
    public void delete(Integer id) {
        boolean exists = groupRepository.existsById(id);
        if (!exists)
            throw new NotFoundException("Group is not found");
        groupRepository.deleteById(id);
    }
}
