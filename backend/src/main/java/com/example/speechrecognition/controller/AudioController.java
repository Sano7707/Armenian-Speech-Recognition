package com.example.speechrecognition.controller;


import com.example.speechrecognition.model.User;
import com.example.speechrecognition.service.AudioService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;





@RestController
public class AudioController {


    private final AudioService audioService;


    public AudioController(AudioService audioService) {
        this.audioService  = audioService;
    }

    @PostMapping("/process-audio")
    public ResponseEntity<String> processAudio(@RequestParam(name = "file") MultipartFile file,
                                               @RequestParam String modelName)  {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

      return new ResponseEntity<>(audioService.processAudio(file,modelName),HttpStatus.OK);
    }


    @PostMapping("/process-audio/{userId}")
    public ResponseEntity<User> processAudio(@PathVariable(name = "userId") String userId,
                                                       @RequestParam MultipartFile file,
                                                       @RequestParam String modelName,
                                                       @RequestParam String chatTitle) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(new User(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(audioService.processAudio(userId,file,modelName,chatTitle),HttpStatus.OK);
    }


    @PostMapping("/process-audio-link")
    public ResponseEntity<String> processAudio(@RequestParam String url) {
        return new ResponseEntity<>(audioService.processAudio(url),HttpStatus.OK);
    }

    @PostMapping("/process-audio-link/{userId}")
    public ResponseEntity<User> processAudio(@PathVariable("userId") String userId,
                                             @RequestParam String url,
                                             @RequestParam String chatTitle) {
        return new ResponseEntity<>(audioService.processAudio(userId,url,chatTitle),HttpStatus.OK);
    }


    @GetMapping(value = "/audio/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getAudio(@PathVariable String fileName) {
      return audioService.getAudio(fileName);
    }

}
