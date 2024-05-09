package com.example.speechrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "Subscribers")
public class Subscriber {
    @Id
    private String _id;
    private String email;

    public Subscriber(String email){
        this.email = email;
    }
}
