package com.example.speechrecognition.controller;

import com.example.speechrecognition.service.SubscriberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@AllArgsConstructor
public class SubscriptionController {

    private SubscriberService subscriberService;

    @PostMapping("/subscribe")
    public ResponseEntity<HttpStatus> subscribe(@RequestParam String email){
        HttpStatus status = subscriberService.subscribe(email).equalsIgnoreCase("Success") ? HttpStatus.OK : HttpStatus.CONFLICT;
        return new ResponseEntity<>(status);
    }
}
