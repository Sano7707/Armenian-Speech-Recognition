package com.example.speechrecognition.service;

import com.example.speechrecognition.model.RegistrationRequest;
import com.example.speechrecognition.model.User;

import java.util.List;
public interface UserService  {


    List<User> getAllUsers();
    User getChats(String userId);

    User getUserById(String userId);

    User addChatToUser(String userId, String chatTitle);

    User deleteChat(String userId, String chatTitle);

    User saveUser(User user);

    void registerUser(RegistrationRequest registrationRequest);

    String login(String email,String password);

    String getUserName(String userId);

    void verifyEmail(String token);

}
