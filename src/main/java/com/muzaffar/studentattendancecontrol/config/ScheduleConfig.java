package com.muzaffar.studentattendancecontrol.config;

import com.muzaffar.studentattendancecontrol.model.AttendanceForwardDto;
import com.muzaffar.studentattendancecontrol.repository.AttendanceRepository;
import com.muzaffar.studentattendancecontrol.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {
    private final AttendanceService attendanceService;
    private final RestTemplate restTemplate;
    private final AttendanceRepository attendanceRepository;

    @Scheduled(fixedDelay = 10000L)
    public void sendAttendances() throws URISyntaxException {
        String url = "http://localhost:8081/api/attendances";
        List<AttendanceForwardDto> list = attendanceService.send();
        if (list != null && !list.isEmpty()) {
            restTemplate.postForObject(new URI(url), list, ResponseEntity.class);
            for (AttendanceForwardDto attendanceForwardDto : list) {
                attendanceRepository.updateSent(attendanceForwardDto.getId());
            }
        }
    }
}
