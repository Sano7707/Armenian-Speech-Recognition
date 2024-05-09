package com.example.speechrecognition.controller;

import com.example.speechrecognition.model.User;
import com.example.speechrecognition.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/{userId}/chats")
    public ResponseEntity<User> addNewChatToUser(@PathVariable String userId, @RequestParam String chatTitle) {
        return new ResponseEntity<>(userService.addChatToUser(userId, chatTitle),HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/chats")
    public ResponseEntity<User> deleteChat(@PathVariable String userId,@RequestParam String chatTitle) {
        return new ResponseEntity<>(userService.deleteChat(userId,chatTitle),HttpStatus.OK);
    }

    @GetMapping("/{userId}/chats")
    public ResponseEntity<User> getChats(@PathVariable String userId) {
        return new ResponseEntity<>(userService.getChats(userId),HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUserName(@PathVariable(value = "userId") String userId) {
        return new ResponseEntity<>(userService.getUserName(userId),HttpStatus.OK);
    }

}

