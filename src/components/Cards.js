import React from 'react';
import './Cards.css';
import CardItem from './CardItem';

function Cards() {
  return (
    <div className='cards'>
      <h1>Check out what you can do!</h1>
      <div className='cards__container'>
        <div className='cards__wrapper'>
          <ul className='cards__items'>
            <CardItem
              src='images/note-taking.jpg'
              text='Get transcription of your Armenian lectures'
              label='Notes'
              path='/services'
            />
            <CardItem
              src='images/lyrics.jpg'
              text='Get the lyrics of your favorite Armenian language song'
              label='Lyrics'
              path='/services'
            />
          </ul>
          <ul className='cards__items'>
            <CardItem
              src='images/armenian-english.jpg'
              text='Get English translation of your Armenian audio'
              label='Trasnlation'
              path='/services'
            />
            <CardItem
              src='images/armenian-audio-to-text.png'
              text='Get your meeting transcriptions in Armenian'
              label='Meeting'
              path='/services'
            />
            <CardItem
              src='images/podcast.jpg'
              text='Your favorite podcasts in text format'
              label='Podcast'
              path='/services'
            />
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Cards;
