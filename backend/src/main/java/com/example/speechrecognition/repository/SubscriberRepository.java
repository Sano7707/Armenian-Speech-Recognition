package com.example.speechrecognition.repository;

import com.example.speechrecognition.model.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends MongoRepository<Subscriber,String> {

    Optional<Subscriber> findByEmail(String email);
}
