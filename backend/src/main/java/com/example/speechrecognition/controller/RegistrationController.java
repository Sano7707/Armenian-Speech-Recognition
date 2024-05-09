package com.example.speechrecognition.controller;

import com.example.speechrecognition.model.RegistrationRequest;
import com.example.speechrecognition.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.speechrecognition.constants.Constants.EMAIL_VERIFICATION_HTML;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody RegistrationRequest registrationRequest){
         userService.registerUser(registrationRequest);
         return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email,
                                        @RequestParam String password) {
        try {
            String userId = userService.login(email, password);
            return ResponseEntity.ok(userId);
        } catch (IllegalStateException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("User with this email is not registered")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            } else if (errorMessage.contains("The password is not correct")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
            } else if (errorMessage.contains("The user is not verified, check your email for verification")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        }
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return new ResponseEntity<>(EMAIL_VERIFICATION_HTML,HttpStatus.OK);
    }
}
