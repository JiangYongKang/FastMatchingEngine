package com.vincent.web.controller;

import com.vincent.web.service.MemberOrderServiceImpl;
import com.vincent.model.CommonResponse;
import com.vincent.model.OrderCommand;
import com.vincent.web.validator.OrderValidation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orders")
public class LimitOrdersController {

    @Resource
    private MemberOrderServiceImpl memberOrderServiceImpl;

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size,
            @RequestParam("ticket") String ticket) {
        return ResponseEntity.ok(new CommonResponse());
    }

    @PostMapping("/limit")
    public ResponseEntity<?> createLimitOrder(@RequestBody @OrderValidation(message = "invalid command") OrderCommand command) {
        command = memberOrderServiceImpl.createLimitOrder(command);
        return ResponseEntity.ok(command);
    }

    @PostMapping("/market")
    public ResponseEntity<?> createMarketOrder(@RequestBody @OrderValidation(message = "invalid command") OrderCommand command) {
        command = memberOrderServiceImpl.createMarketOrder(command);
        return ResponseEntity.ok(command);
    }


    @PutMapping
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(new CommonResponse());
    }

}
