package com.example.speechrecognition.Util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;


public class Util {
    public static final Path OUTPUT_DIRECTORY = Path.of("/home/azureuser/audios");

    public static String handleFileUpload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            return "File is empty";
        }

        String fileName = file.getOriginalFilename();
        File destFile = new File(OUTPUT_DIRECTORY.toString(), LocalDateTime.now() + fileName.replace(" ",""));

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return destFile.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file";
        }
    }

    public static String handleFileUploadChats(String userID,MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            return "File is empty";
        }

        String fileName = file.getOriginalFilename();
        String outputedFileName = userID.substring(10) + LocalDateTime.now() + fileName.replace(" ","");
        File destFile = new File(OUTPUT_DIRECTORY.toString(),outputedFileName);

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputedFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file";
        }
    }

    public static String extractArmenianText(String text) {
        String[] lines = text.split("\n");

        int emptyLineCount = 0;
        int lineCount = 0;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                emptyLineCount++;
                if (emptyLineCount == 2) {
                    return lines[lineCount+1].trim();
                }
            }
            lineCount++;
        }
        return "";
    }

}
