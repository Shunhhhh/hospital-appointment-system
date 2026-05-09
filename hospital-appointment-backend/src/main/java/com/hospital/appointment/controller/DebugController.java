package com.hospital.appointment.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/test-password")
    public String testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$OyXnTF90DHtmPUFvCdh9eO20hsXDu6z0H3tneWs.xGHK8ScwU4jfy";
        boolean matches = encoder.matches("password", hash);
        String newHash = encoder.encode("password");
        return "Hash matches 'password': " + matches + "<br/>New hash for 'password': " + newHash;
    }
}
