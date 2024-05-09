import argparse
import yt_dlp
import os
import librosa
import torch
import numpy as np
from transformers import WhisperForConditionalGeneration, WhisperFeatureExtractor, WhisperTokenizer

parser = argparse.ArgumentParser(description='Download YouTube video and transcribe using the Whisper model.')
parser.add_argument('--url', type=str, required=True, help='YouTube video URL to download and transcribe.')

# Parse arguments
args = parser.parse_args()

tokenizer = WhisperTokenizer.from_pretrained("openai/whisper-small", language="Armenian", task="transcribe")
model_path = "/Users/sano/Desktop/SpeechRecognition/Parser/Speech_Recognition_small/whisper-small-hy/last_epoch/"
feature_extractor_save_path = "/Users/sano/Desktop/SpeechRecognition/Parser/Speech_Recognition_small/whisper-small-hy/last_epoch/"
model = WhisperForConditionalGeneration.from_pretrained(model_path)
feature_extractor = WhisperFeatureExtractor.from_pretrained(feature_extractor_save_path)
model.eval()

def download_youtube_audio(url,save_path):
    ydl_opts = {
        'format': 'bestaudio/best',
        'postprocessors': [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'mp3',
            'preferredquality': '192',
        }],
        'outtmpl': os.path.join(save_path, '%(id)s.%(ext)s'),
        'quiet': True  # Suppress output from yt_dlp
    }
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info_dict = ydl.extract_info(url, download=True)
        filename = ydl.prepare_filename(info_dict)
        base, ext = os.path.splitext(filename)
        return base + '.mp3'
save_directory = "/Users/sano/Desktop/SpeechRecognition/src/main/resources/audio"

audio_file = download_youtube_audio(args.url,save_directory)

def segment_audio(audio_file, segment_length=30, sample_rate=16000):
    audio, sr = librosa.load(audio_file, sr=sample_rate, mono=True)
    total_length = librosa.get_duration(y=audio, sr=sr)

    segments = int(np.ceil(total_length / segment_length))

    for i in range(segments):
        start = i * segment_length * sr
        end = min((i + 1) * segment_length * sr, len(audio))
        yield audio[start:end]

def transcribe_segments(audio_file):
    texts = []
    for segment in segment_audio(audio_file):
        inputs = feature_extractor(segment, return_tensors="pt", sampling_rate=16000)

        with torch.no_grad():
            predictions = model.generate(inputs.input_features)

        text = tokenizer.batch_decode(predictions, skip_special_tokens=True)[0]
        texts.append(text)

    return " ".join(texts)

# Perform transcription
result = transcribe_segments(audio_file)

# Print only the transcribed Armenian language text
print(result)

