package com.example.speechrecognition.Python;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class PythonCommand {

    public static final String PYTHON = "python3";

    @Getter
    private String python;
    @Getter
    private Path script;

    private final StringBuilder arguments;

    private PythonCommand(String python) {
        arguments = new StringBuilder();
        this.python = python;
    }

    public PythonCommand script(Path script) {
        this.script = script;
        return this;
    }

    public static PythonCommand python3() {
        return new PythonCommand(PYTHON);
    }

    public PythonCommand argument(String key, String value) {
        arguments.append(" ").append(key.trim()).append(" ").append(value);
        return this;
    }

    public PythonCommand audioArg(Path value) {
        return argument("--audio_path", String.valueOf(value));
    }
    public PythonCommand urlArg(String url) {
        return argument("--url", url);
    }


    public String build() {
        return python.trim() + " " + script + " " + arguments;
    }

    public String processCommand() {
        try {
            String[] cmdArray = build().split("\\s+");

            Process process = Runtime.getRuntime().exec(cmdArray);

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                StringBuilder errorOutput = new StringBuilder();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                return "Error executing command: " + errorOutput;
            }

            return output.toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing command: " + e.getMessage();
        }
    }


}
