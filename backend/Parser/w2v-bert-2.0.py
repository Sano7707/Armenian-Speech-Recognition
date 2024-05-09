from transformers import AutoModelForCTC, Wav2Vec2Processor, SeamlessM4TFeatureExtractor
import librosa
import torch
import argparse

parser = argparse.ArgumentParser(description='Transcribe audio files using the w2v-bert-2.0 model.')
parser.add_argument('--audio_path', type=str, required=True, help='Path to the audio file to transcribe.')

# Parse arguments
args = parser.parse_args()

# Load model and processor
model = AutoModelForCTC.from_pretrained("anah1tbaghdassarian/w2v-bert-2.0-armenian-CV17.0")
processor = Wav2Vec2Processor.from_pretrained("anah1tbaghdassarian/w2v-bert-2.0-armenian-CV17.0")
feature_extractor = SeamlessM4TFeatureExtractor.from_pretrained("anah1tbaghdassarian/w2v-bert-2.0-armenian-CV17.0")

def transcribe_audio(audio_file):
  # Load audio file
  audio, sampling_rate = librosa.load(audio_file, sr=16000, mono=True)

  # Extract features using the feature extractor
  inputs = feature_extractor(audio, sampling_rate=sampling_rate, return_tensors="pt")

  # Perform inference
  with torch.no_grad():
      logits = model(**inputs).logits

  predicted_ids = torch.argmax(logits, dim=-1)

  # Transcribe speech
  transcription = processor.batch_decode(predicted_ids)

  return transcription[0]

result = transcribe_audio(args.audio_path)
print(result)
