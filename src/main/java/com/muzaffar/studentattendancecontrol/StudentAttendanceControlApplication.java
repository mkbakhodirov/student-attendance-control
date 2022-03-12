package com.muzaffar.studentattendancecontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.muzaffar.studentattendancecontrol.repository.jpa"})
public class StudentAttendanceControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentAttendanceControlApplication.class, args);
    }

}
