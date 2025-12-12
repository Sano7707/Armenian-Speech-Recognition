package com.example.speechrecognition.service;

import com.example.speechrecognition.Python.PythonCommand;
import com.example.speechrecognition.model.Chat;
import com.example.speechrecognition.model.Message;
import com.example.speechrecognition.model.User;
import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.example.speechrecognition.Python.PythonScripts.BERT_PY;
import static com.example.speechrecognition.Python.PythonScripts.WHISPER_SMALL_PY;
import static com.example.speechrecognition.Python.PythonScripts.WHISPER_SMALL_YOUTUBE_PY;
import static com.example.speechrecognition.Util.Util.OUTPUT_DIRECTORY;
import static com.example.speechrecognition.Util.Util.extractArmenianText;
import static com.example.speechrecognition.Util.Util.handleFileUpload;
import static com.example.speechrecognition.Util.Util.handleFileUploadChats;

@Service
public class AudioServiceImpl implements AudioService{

    private final UserService userService;

    public AudioServiceImpl(UserService userService){
        this.userService = userService;
    }

    @Override
    public String processAudio(MultipartFile file, String modelName) {
        String outputFile = handleFileUpload(file);
        String resultText;

        try {
            if (modelName.equalsIgnoreCase("Whisper Small")) {
                resultText = speechToTextByWhisper(outputFile);
                return resultText;
            } else if (modelName.equalsIgnoreCase("Microsoft Azure")) {
                resultText = speechToTextByAzure(new File(outputFile));
                return resultText;
            }
            else if (modelName.equalsIgnoreCase("Wav2Vec2-BERT")) {
                resultText = speechToTextByBert(outputFile);
                return resultText;
            }
        } finally {
            deleteFile(outputFile);
        }
        return "";
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.err.println("Failed to delete the file.");
            }
        } else {
            System.err.println("File does not exist.");
        }
    }
    @Override
    public String speechToTextByWhisper(String file) {
        return PythonCommand.python3().script(WHISPER_SMALL_PY)
            .audioArg(Path.of((file))).processCommand();
    }

    @Override
    public String speechToTextByBert(String file) {
        return PythonCommand.python3().script(BERT_PY)
            .audioArg(Path.of((file))).processCommand();
    }

    @Override
    public String speechToTextByAzure(File audioFile) {
        try {
            String speechKey = "YOUR_KEY";
            String speechRegion = "eastus";
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
            speechConfig.setSpeechRecognitionLanguage("hy-AM");

            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFile.getPath());
            SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

            Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
            SpeechRecognitionResult speechRecognitionResult = task.get();

            if (speechRecognitionResult.getReason() == ResultReason.RecognizedSpeech) {
                return speechRecognitionResult.getText();
            }
            else if (speechRecognitionResult.getReason() == ResultReason.NoMatch) {
                return "Speech could not be recognized.";
            }
            else if (speechRecognitionResult.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(speechRecognitionResult);
                if (cancellation.getReason() == CancellationReason.Error) {
                    return "Speech recognition canceled. Error details: " + cancellation.getErrorDetails();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error performing speech recognition: " + e.getMessage();
        }
        return "";
    }

    @Override
    public User processAudio(String userId, MultipartFile file, String modelName, String chatTitle) {

        User user = userService.getUserById(userId);

        String outputFile = handleFileUploadChats(userId,file);
        String resultText;

        if(modelName.equalsIgnoreCase("Whisper Small")){
            resultText = speechToTextByWhisper(OUTPUT_DIRECTORY +"/"+ outputFile);
            return updateChat(chatTitle, user, outputFile, resultText);
        }

        else if(modelName.equalsIgnoreCase("Microsoft Azure")){
            String recognizedText = speechToTextByAzure(new File(OUTPUT_DIRECTORY+"/"+outputFile));
            return updateChat(chatTitle, user, outputFile, recognizedText);
        }
        else if(modelName.equalsIgnoreCase("Wav2Vec2-BERT")){
             resultText = speechToTextByBert(OUTPUT_DIRECTORY +"/"+ outputFile);
            return updateChat(chatTitle, user, outputFile, resultText);
        }

        return new User();
    }

    @Override
    public String processAudio(String url) {
        String recognizedText = extractArmenianText(PythonCommand.python3().script(WHISPER_SMALL_YOUTUBE_PY)
            .urlArg(url).processCommand());
        deleteFile(OUTPUT_DIRECTORY + "/" + url.substring(url.lastIndexOf('/')+1,url.indexOf('?')) + ".mp3");
        return recognizedText;
    }

    @Override
    public User processAudio(String userId,String url, String chatTitle) {
        User user = userService.getUserById(userId);
        String recognizedText = extractArmenianText(PythonCommand.python3().script(WHISPER_SMALL_YOUTUBE_PY)
            .urlArg(url).processCommand());
        return updateChat(chatTitle,user,url.substring(url.lastIndexOf('/')+1,url.indexOf('?')) + ".mp3",recognizedText);

    }

    @Override
    public ResponseEntity<Resource> getAudio(String fileName) {
        try {
            Path file = OUTPUT_DIRECTORY.resolve(fileName);
            byte[] data = Files.readAllBytes(file);
            ByteArrayResource resource = new ByteArrayResource(data);
            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
            if (fileName.toLowerCase().endsWith(".mp3")) {
                contentType = MediaType.parseMediaType("audio/mpeg");
            } else if (fileName.toLowerCase().endsWith(".wav")) {
                contentType = MediaType.parseMediaType("audio/wav");
            }
            return ResponseEntity.ok()
                .contentLength(data.length)
                .contentType(contentType)
                .body(resource);
        } catch (IOException e) {
            System.out.println("Error reading audio file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private User updateChat(String chatTitle, User user, String outputFile, String recognizedText) {
        List<Chat> chats = user.getChats();
        Optional<Chat> currentChat = chats.stream().filter(chat -> chat.getTitle().equalsIgnoreCase(chatTitle)).findFirst();
        currentChat.orElseThrow().getChatLog().add(new Message("me",outputFile));
        currentChat.orElseThrow().getChatLog().add(new Message("gpt",recognizedText));
        user.setChats(chats);
        return userService.saveUser(user);
    }
}
