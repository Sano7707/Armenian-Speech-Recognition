import argparse
import librosa
import torch
from transformers import WhisperForConditionalGeneration, WhisperFeatureExtractor, WhisperTokenizer

parser = argparse.ArgumentParser(description='Transcribe audio files using the Whisper model.')
parser.add_argument('--audio_path', type=str, required=True, help='Path to the audio file to transcribe.')

# Parse arguments
args = parser.parse_args()

tokenizer = WhisperTokenizer.from_pretrained("openai/whisper-small", language="Armenian", task="transcribe")
model_path = "Parser/Speech_Recognition_small/whisper-small-hy/last_epoch/"
feature_extractor_save_path = "Parser/Speech_Recognition_small/whisper-small-hy/last_epoch/"
model = WhisperForConditionalGeneration.from_pretrained(model_path)
feature_extractor = WhisperFeatureExtractor.from_pretrained(feature_extractor_save_path)
model.eval()


def transcribe_audio(audio_file):
    # Load the audio file and preprocess with the feature extractor
    audio, sampling_rate = librosa.load(audio_file, sr=16000, mono=True)
    inputs = feature_extractor(audio, return_tensors="pt", sampling_rate=sampling_rate)

    # Generate transcription
    with torch.no_grad():
        predictions = model.generate(inputs.input_features)

    # Decode the generated IDs to text
    text = tokenizer.batch_decode(predictions, skip_special_tokens=True)[0]
    return text


# Transcribe audio
result = transcribe_audio(args.audio_path)

# Print and return result text
print(result)

# Return result text (optional, depending on how you want to use it in your script)
# You can use the result variable in further processing if needed
# return result

