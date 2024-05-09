import nemo
import nemo.collections.asr as nemo_asr
import argparse

parser = argparse.ArgumentParser(description='Transcribe audio files using the quartznet15x5 model.')
parser.add_argument('--audio_path', type=str, required=True, help='Path to the audio file to transcribe.')

# Parse arguments
args = parser.parse_args()

# Load model and processor
model = nemo_asr.models.ASRModel.from_pretrained("anah1tbaghdassarian/stt_hy-AM_quartznet15x5_armenian-CV17.0_WER11.75")

def transcribe_audio(audio_file):

  transcription = model.transcribe([audio_file])

  return transcription[0]

result = transcribe_audio(args.audio_path)
print(result)