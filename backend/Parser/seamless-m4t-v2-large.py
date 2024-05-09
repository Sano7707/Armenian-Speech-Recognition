import torch
import torchaudio
from transformers import AutoProcessor, SeamlessM4Tv2Model
import argparse

parser = argparse.ArgumentParser(description='Transcribe audio files using the seamless-m4t-v2-large model.')
parser.add_argument('--audio_path', type=str, required=True, help='Path to the audio file to transcribe.')

# Parse arguments
args = parser.parse_args()


processor = AutoProcessor.from_pretrained("facebook/seamless-m4t-v2-large")
model = SeamlessM4Tv2Model.from_pretrained("facebook/seamless-m4t-v2-large")


def transcribe_audio(audio_file):
  audio, orig_freq =  torchaudio.load(audio_file)
  audio =  torchaudio.functional.resample(audio, orig_freq=orig_freq, new_freq=16_000) 
  audio_inputs = processor(audios=audio, return_tensors="pt", sampling_rate = 16_000, src_lang = 'hye')

  # generate translation
  output_tokens = model.generate(**audio_inputs, tgt_lang="hye", generate_speech=False)
  translated_text_from_audio = processor.decode(output_tokens[0].tolist()[0], skip_special_tokens=True)
  
  return translated_text_from_audio

result = transcribe_audio(args.audio_path)
print(result)
