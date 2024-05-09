package com.example.speechrecognition.service;

import com.example.speechrecognition.model.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface AudioService {

    String processAudio(MultipartFile file,String modelName);

    String speechToTextByWhisper(String file);

    String speechToTextByAzure(File file);

    User processAudio( String userId, MultipartFile file,String modelName, String chatTitle);
    ResponseEntity<Resource> getAudio(@PathVariable String fileName);

    String processAudio( String url);
    User processAudio(String userId,String url, String chatTitle);

    String speechToTextByBert(String file);
}
