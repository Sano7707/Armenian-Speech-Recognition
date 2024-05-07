import React, { useState, useEffect } from 'react';
import { AudioRecorder } from 'react-audio-voice-recorder';
import audioBufferToWav from 'audiobuffer-to-wav';
import './Recorder.css';
import { ScaleLoader } from 'react-spinners';

function Recorder() {
  const [uploadedFile, setUploadedFile] = useState(null);
  const [fileName, setFileName] = useState('');
  const [transcription, setTranscription] = useState('Your Transcription Here ...');
  const [audioUrl, setAudioUrl] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [selectedModel, setSelectedModel] = useState('Microsoft Azure');
  const [loading, setLoading] = useState(false);
  const [youtubeLink, setYoutubeLink] = useState('');


  const addAudioElement = (blob) => {
    const reader = new FileReader();
    reader.onload = () => {
      const audioData = reader.result;
      const context = new AudioContext();
      context.decodeAudioData(audioData, (buffer) => {
        const wavBuffer = audioBufferToWav(buffer);
        const wavBlob = new Blob([wavBuffer], { type: 'audio/wav' });
        const url = URL.createObjectURL(wavBlob);
        setAudioUrl(url);
        setFileName('');
        setTranscription('Your Transcription Here ...');
        setUploadedFile(null);
        setYoutubeLink('');
      });
    };
    reader.onerror = (error) => {
      console.error('Error reading audio blob:', error);
    };
    reader.readAsArrayBuffer(blob);
  };

  const displayFileName = (event) => {
    if (event.target.files && event.target.files.length > 0) {
      const uploadedFile = event.target.files[0];
      setFileName(uploadedFile.name);

      if (uploadedFile.type.startsWith('audio/')) {
        const reader = new FileReader();
        reader.onload = () => {
          const audioData = reader.result;
          const context = new AudioContext();
          context.decodeAudioData(audioData, (buffer) => {
            const wavBuffer = audioBufferToWav(buffer);
            const wavBlob = new Blob([wavBuffer], { type: 'audio/wav' });
            const url = URL.createObjectURL(wavBlob);
            setAudioUrl(url);
            setTranscription('Your Transcription Here ...');
            setUploadedFile(wavBlob);
            setYoutubeLink('');
          });
        };
        reader.onerror = (error) => {
          console.error('Error reading audio blob:', error);
        };
        reader.readAsArrayBuffer(uploadedFile);
      } else {
        setUploadedFile(uploadedFile);
        const url = URL.createObjectURL(uploadedFile);
        setAudioUrl(url);
        setTranscription('Your Transcription Here ...');
        setYoutubeLink('');
      }
    }
  };

  const handleGetTranscriptions = () => {
    if (youtubeLink) {
      setProcessing(true);
      setLoading(true);
      //http://http://20.52.101.91:8081
      fetch(`http://20.52.101.91:8081/process-audio-link?url=${youtubeLink}`, {
        method: 'POST',
      })
        .then((response) => response.text())
        .then((data) => {
          setTranscription(data);
          setProcessing(false);
          setLoading(false);
        })
        .catch((error) => {
          console.error('Error sending audio file:', error);
          setProcessing(false);
          setLoading(false);
        });
    } else if (audioUrl || uploadedFile) {
      setProcessing(true);
      setLoading(true);
      const formData = new FormData();
      if (audioUrl && !uploadedFile) {
        fetch(audioUrl)
          .then((response) => response.blob())
          .then((wavBlob) => {
            formData.append('file', wavBlob, 'audio.wav');
            formData.append('modelName', selectedModel);
            sendFormData(formData);
          })
          .catch((error) => {
            console.error('Error fetching audio:', error);
            setProcessing(false);
            setLoading(false);
          });
      } else if (uploadedFile) {
        formData.append('file', uploadedFile, uploadedFile.name);
        formData.append('modelName', selectedModel);
        sendFormData(formData);
      }
    } else {
      alert('No audio recorded, file uploaded, or YouTube link provided.');
    }
  };

  const sendFormData = (formData) => {
    fetch('http://20.52.101.91:8081/process-audio', {
      method: 'POST',
      body: formData,
    })
      .then((response) => response.text())
      .then((data) => {
        setTranscription(data);
        setProcessing(false);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Error sending audio file:', error);
        setProcessing(false);
        setLoading(false);
      });
  };

  const handleYoutubeLinkChange = (event) => {
    setYoutubeLink(event.target.value);
    setAudioUrl(null);
    setUploadedFile(null);
    setFileName('');
  };
  
  return (
    <div className="page-container">
      <div className="audio-recorder-uploader-container">
        <div className="audio-recorder-container">
          <p>Record your voice and convert it into text</p>
          <p>Your recordings will be deleted after processing</p>
          <AudioRecorder
            onRecordingComplete={addAudioElement}
            audioTrackConstraints={{
              noiseSuppression: true,
              echoCancellation: true,
            }}
            onNotAllowedOrFound={(err) => console.table(err)}
            downloadOnSavePress={false}
            downloadFileExtension="webm"
            mediaRecorderOptions={{
              audioBitsPerSecond: 128000,
            }}
          />
          <br />
        </div>
        <p>or</p>
        <div className="upload-container">
          <span className="upload-icon"><i className="fas fa-upload"></i></span>
          Upload Audio
          <input type="file" className="upload-input" onChange={displayFileName} accept="audio/wav, audio/mp3, audio/m4a, audio/flac, audio/aac" />
        </div>
        <p>Supported audio formats: WAV, MP3, M4A, FLAC, AAC</p>

        <div className="youtube-link-container">
          <input
              type="text"
              value={youtubeLink}
              onChange={handleYoutubeLinkChange}
              placeholder="Paste your youtube link here"
            />
        </div>

        <div className="output-container">
          {fileName && <p>Uploaded File: {fileName}</p>}
          {audioUrl && (
            <div>
              <p>Uploaded Audio:</p>
              <audio src={audioUrl} controls />
            </div>
          )}
          <p>Choose a model</p>
          <div className="select-wrapper">
            <select value={selectedModel} onChange={(e) => setSelectedModel(e.target.value)} disabled={youtubeLink}>
              <option value="Microsoft Azure">Microsoft Azure</option>
              <option value="Whisper Small">Whisper Small</option>
              <option value="Wav2Vec2-BERT">Wav2Vec2-BERT</option>
            </select>
          </div>
          <textarea
            className="transcription-textarea"
            value={transcription}
            onChange={(e) => setTranscription(e.target.value)}
            placeholder="Your Transcription Here ..."
          ></textarea>

          <button className="get-transcriptions-btn" onClick={handleGetTranscriptions}>Get my transcriptions</button>
        </div>
      </div>
      {loading && (
        <div className="overlay">
          <ScaleLoader
            loading={loading}
            height={70}
            width={6}
            aria-label="Loading Spinner"
            data-testid="loader"
          />
        </div>
      )}
    </div>
  );
}

export default Recorder;
