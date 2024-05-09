package com.example.speechrecognition.service;


import com.example.speechrecognition.model.Chat;
import com.example.speechrecognition.model.Message;
import com.example.speechrecognition.model.RegistrationRequest;
import com.example.speechrecognition.model.User;
import com.example.speechrecognition.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.speechrecognition.Util.Util.OUTPUT_DIRECTORY;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(String userId){
        return userRepository.findById(userId).orElse(new User());
    }

    public User getChats(String userId){
        return userRepository.findById(userId).orElse(new User());
    }

    public User addChatToUser(String userId, String chatTitle) {
        User user = getUserById(userId);
        user.getChats().add(new Chat(chatTitle,new ArrayList<>()));
        return userRepository.save(user);
    }

    public User deleteChat(String userId, String chatTitle){
        User user = getUserById(userId);
        List<Chat> chats = user.getChats();
        Optional<Chat> chatToDelete = chats.stream().filter(chat -> chat.getTitle().equalsIgnoreCase(chatTitle)).findFirst();

        chatToDelete.ifPresent(chat -> {
            List<String> chatRecordingNames = chat.getChatLog().stream()
                .filter(message -> message.getUser().equals("me"))
                .map(Message::getMessage)
                .toList();

            chatRecordingNames.forEach(fileName -> {
                File fileToBeDeleted = new File(OUTPUT_DIRECTORY + "/" + fileName);
                if (fileToBeDeleted.exists()) {
                    fileToBeDeleted.delete();
                }
            });
        });

        List<Chat> updatedChats = chats.stream()
            .filter(chat -> !chat.getTitle().equalsIgnoreCase(chatTitle))
            .collect(Collectors.toList());

        user.setChats(updatedChats);
        return userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }


    @Override
    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(() -> new IllegalStateException("User with this email is not registered"));

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            throw new IllegalStateException("The password is not correct");
        }

        if (!user.isVerified()) {
            throw new IllegalStateException("The user is not verified, check your email for verification");
        }

        return user.get_id();
    }


    @Override
    public String getUserName(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFirstName();
    }

    public void registerUser(RegistrationRequest registrationRequest) {
        boolean userExists = userRepository.findByEmail(registrationRequest.getEmail()).isPresent();

        if(userExists){
            throw new IllegalStateException(("Email already taken"));
        }

        String password = new BCryptPasswordEncoder().encode(registrationRequest.getPassword());
        String token = UUID.randomUUID().toString();
        User user = new User(
            registrationRequest.getFirstName(),
            registrationRequest.getLastName(),
            registrationRequest.getEmail(),
            password,
            new ArrayList<>(),
            false,
            token
        );

        String link = "http://20.52.101.91:8081/verify?token=" + token;
        userRepository.save(user);

        emailService.send(user.getEmail(), buildEmail(registrationRequest.getFirstName(),link),"Confirm your email");
    }


    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new IllegalStateException("Invalid token"));

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Email Confirmation</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Helvetica, Arial, sans-serif;\n" +
            "            font-size: 16px;\n" +
            "            margin: 0;\n" +
            "            color: #0b0c0c;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 580px;\n" +
            "            margin: 0 auto;\n" +
            "        }\n" +
            "        .header {\n" +
            "            background-color: #0b0c0c;\n" +
            "            padding: 20px;\n" +
            "            text-align: center;\n" +
            "            color: #ffffff;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 20px;\n" +
            "            background-color: #f9f9f9;\n" +
            "            border-left: 10px solid #b1b4b6;\n" +
            "            margin-top: 20px;\n" +
            "        }\n" +
            "        a {\n" +
            "            color: #1D70B8;\n" +
            "            text-decoration: none;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1>Confirm your email</h1>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <p>Hi " + name + ",</p>\n" +
            "            <p>Thank you for registering in Armenian Speech Recognition platform." +
            " Please click on the below link to activate your account:</p>\n" +
            "            <blockquote>\n" +
            "                <p><a href=\"" + link + "\">Activate Now</a></p>\n" +
            "            </blockquote>\n" +
            "            <p>See you soon!</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }

}



