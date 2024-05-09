package com.example.speechrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    private String _id;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    private String email;
    private String password;
    private List<Chat> chats;
    private boolean verified;
    private String verificationToken;



    public User(String firstName, String lastName, String email,String password, List<Chat> chats,boolean verified, String verificationToken) {
        this.firstName = firstName;
        this.email = email;
        this.lastName = lastName;
        this.password = password;
        this.chats = chats;
        this.verified = verified;
        this.verificationToken = verificationToken;
    }

}
