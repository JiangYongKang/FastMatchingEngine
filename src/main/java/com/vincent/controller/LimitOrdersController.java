package com.vincent.controller;

import com.vincent.model.CommonResponse;
import com.vincent.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/limit/orders")
public class LimitOrdersController {

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size,
            @RequestParam("ticket") String ticket) {
        return ResponseEntity.ok(new CommonResponse());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order) {
        CommonResponse commonResponse = new CommonResponse();
        return ResponseEntity.ok(commonResponse);
    }

    @PutMapping
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(new CommonResponse());
    }

}
