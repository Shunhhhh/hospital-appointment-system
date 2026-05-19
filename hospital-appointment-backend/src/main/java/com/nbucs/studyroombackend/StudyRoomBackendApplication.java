package com.nbucs.studyroombackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.nbucs.studyroombackend",
    "com.hospital.appointment"
})
@EnableScheduling
@MapperScan({"com.nbucs.studyroombackend.mapper", "com.hospital.appointment.mapper"})
public class StudyRoomBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomBackendApplication.class, args);
    }
}
