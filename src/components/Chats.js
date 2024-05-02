import React, { useState, useEffect } from 'react';
import { AudioRecorder } from 'react-audio-voice-recorder';
import audioBufferToWav from 'audiobuffer-to-wav';
import './Chat.css';
import { ScaleLoader } from 'react-spinners';

function Chats() {
  const [chatTitle, setChatTitle] = useState("");
  const [uploadedFile, setUploadedFile] = useState(null);
  const [user, setUser] = useState(null);
  const [activeChatIndex, setActiveChatIndex] = useState(null);
  const [chatLog, setChatLog] = useState([]);
  const [audioUrl, setAudioUrl] = useState(null);
  const [fileName, setFileName] = useState("");
  const [processing, setProcessing] = useState(false);
  const [newChatTitle, setNewChatTitle] = useState("");
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const [deleteConfirmation, setDeleteConfirmation] = useState(false);
  const [chatToDelete, setChatToDelete] = useState(null);
  const [selectedModel, setSelectedModel] = useState("Microsoft Azure");
  const[loading,setLoading] = useState(false);
  const [youtubeLink, setYoutubeLink] = useState('');
  const userId = sessionStorage.getItem('userId');



  useEffect(() => {
    fetchChats();
  }, []);

  useEffect(() => {
    if (activeChatIndex !== null) {
      fetchChatMessages(activeChatIndex);
    }
  }, [activeChatIndex]);

  const fetchChats = async () => {
    try {
      const response = await fetch(`https://wealthy-wired-kodiak.ngrok-free.app/users/${userId}/chats`, {
        method: 'GET',
      });
      if (!response.ok) {
        throw new Error('Network response was not ok.');
      }
      const data = await response.json();
      setUser(data);
      setActiveChatIndex(0)
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const fetchChatMessages = (chatIndex) => {
    if(user.chats[chatIndex] === undefined){
      setChatLog([])
    }
    else{
    const chatLogData = user.chats[chatIndex].chatLog;
    setChatLog(chatLogData);
  }
  };

  const handleChatSelection = (chatIndex) => {
    if (chatIndex === activeChatIndex) {
      return;
    }
    setChatLog([]);
    setActiveChatIndex(chatIndex);
    setChatTitle(user.chats[chatIndex].title)
    setAudioUrl(null)
    setUploadedFile(null)
    setFileName("")
  };

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
        setFileName("");
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
        setYoutubeLink('');
      }
    }
  };

  
  const handleGetTranscriptions = async () => {
    const formData = new FormData();
    if (youtubeLink) {
      setProcessing(true);
      setLoading(true);
      formData.append('chatTitle',user.chats[activeChatIndex].title);
      formData.append('url',youtubeLink);
      fetch(`https://wealthy-wired-kodiak.ngrok-free.app/process-audio-link/${userId}`, {
        method: 'POST',
        body: formData
      })
      .then(response => response.json())
      .then(data => {
        setUser(data);
        setProcessing(false);
        setLoading(false);
        const currentChat = user.chats[activeChatIndex];
        const updatedChatLog = data.chats.find(chat => chat.title === currentChat.title)?.chatLog || [];
        setChatLog(updatedChatLog);
       })
      .catch(error => {
        console.error('Error sending audio file:', error);
        setProcessing(false);
        setLoading(false);
        });
        }
    else if (audioUrl || uploadedFile) {
      setProcessing(true);
      setLoading(true);
      const formData = new FormData();
      if (audioUrl && !uploadedFile) {
        fetch(audioUrl)
        .then(response => response.blob())
        .then(wavBlob => {
          formData.append('file', wavBlob, 'audio.wav');
          formData.append('modelName', selectedModel);
          formData.append('chatTitle', user.chats[activeChatIndex].title);
          sendFormData(formData);
        })
      .catch(error => {
        console.error('Error fetching audio:', error);
        setProcessing(false);
        setLoading(false);

      })
    }else if (uploadedFile) {
      formData.append('file', uploadedFile, uploadedFile.name);
      formData.append('modelName', selectedModel);
      formData.append('chatTitle', user.chats[activeChatIndex].title);
      sendFormData(formData);
    };   
    

  }else {
    alert('No audio recorded, file uploaded, or YouTube link provided.');
  }
};
  
  
  const sendFormData = (formData) => {
    fetch(`https://wealthy-wired-kodiak.ngrok-free.app/process-audio/${userId}`, {
      method: 'POST',
      body: formData,
    })
      .then(response => response.json())
      .then(data => {
        setUser(data);
        setProcessing(false);
        setLoading(false);
        const currentChat = user.chats[activeChatIndex];
        const updatedChatLog = data.chats.find(chat => chat.title === currentChat.title)?.chatLog || [];
        setChatLog(updatedChatLog);
       })
      .catch(error => {
        console.error('Error sending audio file:', error);
        setProcessing(false);
        setLoading(false);

      });
  };


  const handleNewChatModal = () => {
    setShowNewChatModal(true);
  };

  const handleNewChatCreate = async () => {
    if (newChatTitle.trim() !== "") {
      const chatTitle = encodeURIComponent(newChatTitle.trim());
      try {
        const response = await fetch(`https://wealthy-wired-kodiak.ngrok-free.app/users/${userId}/chats?chatTitle=${chatTitle}`, {
          method: 'POST',
        });
        if (response.ok) {
          setShowNewChatModal(false); 
          setNewChatTitle(""); 
          fetchChats(); 
        } else {
          console.error('Failed to create new chat');
        }
      } catch (error) {
        console.error('Error:', error);
      }
    }
  };

  const handleCancelNewChat = () => {
    setNewChatTitle("");
    setShowNewChatModal(false);
  };

  const handleDeleteChat = async () => {
    if (chatToDelete !== null) {
      const chatTitle = user.chats[chatToDelete].title;
      try {
        const response = await fetch(`https://wealthy-wired-kodiak.ngrok-free.app/users/${userId}/chats?chatTitle=${encodeURIComponent(chatTitle)}`, {
          method: 'DELETE'
        });
        if (response.ok) {
          console.log("Chat deleted successfully!");
          setChatLog([])
          fetchChats(); 
          if(chatToDelete === 0){
            fetchChatMessages(1);
          }
        } else {
          console.error('Failed to delete chat:', response.statusText);
        }
      } catch (error) {
        console.error('Error deleting chat:', error);
      }
    }
    setDeleteConfirmation(false); 
    setChatToDelete(null); 
  };


  const handleCancelDeleteChat = () => {
    setDeleteConfirmation(false); 
    setChatToDelete(null); 
  };
  const handleYoutubeLinkChange = (event) => {
    setYoutubeLink(event.target.value);
    setAudioUrl(null);
    setUploadedFile(null);
    setFileName('');
  };

  return (
    <div className='chat-container'>
      <aside className='sidemenu'>
        <div className="side-menu-button addChat" onClick={handleNewChatModal}>
          <span>+</span>
          New Chat
        </div>
        <div className="side-menu-list">
          {user && user.chats.map((chat, index) => (
            <div key={index} className={`side-menu-button ${activeChatIndex === index ? 'active' : ''}`} onClick={() => handleChatSelection(index)}>
              <div className={`chat-title ${activeChatIndex === index ? 'active-chat' : ''}`}>{chat.title}</div>
              <button className="delete-chat-btn" onClick={() => { setDeleteConfirmation(true); setChatToDelete(index); }}>
                <i className="fas fa-trash"></i>
              </button>
            </div>
          ))}
        </div>
      </aside>
      <section className='chatbox'>
        <div className="chat-log">
          {chatLog.map((message, index) => (
            <ChatMessage key={index} message={message} />
          ))}
        </div>
        <div className="bottom-buttons">
          <div className="audio-recorder-uploader-container">
            <div className="audio-recorder-container">
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
            </div>
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
              </select>
            </div>
            <button className="get-transcriptions-btn" onClick={handleGetTranscriptions}>Get my transcriptions</button>
          </div>
        </div>
      </section>
      {showNewChatModal && (
        <div className="modal">
          <div className="modal-content">
            <h2>Create New Chat</h2>
            <input type="text" value={newChatTitle} onChange={(e) => setNewChatTitle(e.target.value)} placeholder="Enter chat title" />
            <div className="modal-buttons">
              <button className="create-chat-btn" onClick={handleNewChatCreate}>Create</button>
              <button className="cancel-chat-btn" onClick={handleCancelNewChat}>Cancel</button>
            </div>
          </div>
        </div>
      )}
      {deleteConfirmation && (
        <div className="modal">
          <div className="modal-content">
            <h2>Confirm Deletion</h2>
            <p>Are you sure you want to delete this chat?</p>
            <div className="modal-buttons">
              <button className="confirm-delete-btn" onClick={handleDeleteChat}>Delete</button>
              <button className="cancel-delete-btn" onClick={handleCancelDeleteChat}>Cancel</button>
            </div>
          </div>
        </div>
      )}
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

const ChatMessage = ({ message }) => {
  const [audioUrl, setAudioUrl] = useState(null);

  useEffect(() => {
    if (message.user === "me") {
      fetch(`https://wealthy-wired-kodiak.ngrok-free.app/audio/${encodeURIComponent(message.message)}`)
        .then(response => response.blob())
        .then(blob => {
          setAudioUrl(URL.createObjectURL(blob));
        })
        .catch(error => console.error('Error fetching audio file:', error));
    } else {
      setAudioUrl(null);
    }
  }, [message]);


  return (
    <div className={`chat-message ${message.user === "gpt" && "chatgpt"}`}>
      <div className="chat-message-center">
        <div className={`avatar ${message.user === "gpt" && "chatgpt"}`}></div>
        {message.user === "me" && audioUrl ? (
          <AudioPlayer audioUrl={audioUrl} />
        ) : (
          <div className="message">
            {message.message}
          </div>
        )}
      </div>
    </div>
  );
}

const AudioPlayer = ({ audioUrl }) => {
  return (
    <div className="audio-message">
      <audio controls>
        <source src={audioUrl} type="audio/mpeg" />
      </audio>
    </div>
  );
}

export default Chats;
