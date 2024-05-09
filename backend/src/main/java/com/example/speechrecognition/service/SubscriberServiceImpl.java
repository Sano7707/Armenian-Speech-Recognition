package com.example.speechrecognition.service;

import com.example.speechrecognition.model.Subscriber;
import com.example.speechrecognition.repository.SubscriberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.speechrecognition.constants.Constants.EMAIL_SUBSCRIPTION;

@Service
@AllArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private SubscriberRepository subscriberRepository;
    private final EmailService emailService;



    @Override
    public String subscribe(String email) {
        Optional<Subscriber> subscriber = subscriberRepository.findByEmail(email);
        if(subscriber.isEmpty()){
            subscriberRepository.save(new Subscriber(email));
            emailService.send(email,EMAIL_SUBSCRIPTION,"Successful Subscription!");
            return "Success";
        }
        else{
            return "Email already subscribed";
        }

    }
}
