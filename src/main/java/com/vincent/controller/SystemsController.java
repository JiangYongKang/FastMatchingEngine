package com.vincent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/systems")
public class SystemsController {

    @GetMapping("/open")
    public ResponseEntity<?> open() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/close")
    public ResponseEntity<?> close() {
        return ResponseEntity.ok("success");
    }

}
