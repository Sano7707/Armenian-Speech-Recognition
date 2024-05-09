package com.example.speechrecognition.Python;

import java.nio.file.Path;

public class PythonScripts {

    public static final Path PYTHON_SCRIPTS_ROOT = Path.of("/home/azureuser/Parser");

    public static final Path WHISPER_SMALL_PY = PYTHON_SCRIPTS_ROOT.resolve("whisper_small.py");
    public static final Path BERT_PY = PYTHON_SCRIPTS_ROOT.resolve("w2v-bert-2.0.py");

    public static final Path WHISPER_SMALL_YOUTUBE_PY = PYTHON_SCRIPTS_ROOT.resolve("whisper_small_youtube_overlapping.py");

}
