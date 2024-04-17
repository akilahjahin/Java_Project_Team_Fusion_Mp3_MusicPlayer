package mp3_music;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener{
	// A way is needed to store our Songs' Details : Hence, this song class
	private Song currentSong;
	
	// Using JLibrary to create an 'Advanced Player object' which will handle playing the music
	private AdvancedPlayer advancedPlayer;
	
	// pause Boolean Flag : To INDICATE WHETHER THE PLAYER has been 'PAUSED'
	private boolean isPaused;
	
	// Stores in the LAST-FRAME when the play back is 'finished':
	// Used for PAUSING & RESUMING
	private int currentFrame;
	
	// constructor
	public MusicPlayer() {
		
	}
	
	public void loadSong(Song song) {
		currentSong = song;
		
		// Playing the current song if not null
		if(currentSong != null) {
			playCurrentSong();
		}
	}
	
	public void pauseSong() {
		if(advancedPlayer != null) {
			// Updating isPaused flag
			isPaused = true;
			
			// The player should be stopped
			stopSong();
		}
	}
	
	public void stopSong() {
		if(advancedPlayer != null) {
			advancedPlayer.stop();
			advancedPlayer.close();
			advancedPlayer = null;
		}
	}
	
	public void playCurrentSong() {
		try {
			// To avoid error-showing as currentSong was null at the beginning when 'play' button pressed
			if(currentSong == null) {
				return;
			}
			
			// READ MP3 Audio Data
			FileInputStream fileInputStream = new FileInputStream(currentSong.getfilePath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			
			// Creating a new Advanced Player
			advancedPlayer = new AdvancedPlayer(bufferedInputStream);
			advancedPlayer.setPlayBackListener(this);
			
			// START MUSIC
			startMusicThread();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// Creating a thread that will handle playing the music
	private void startMusicThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if(isPaused) {
						// RESUME MUSIC : from LAST-FRAME
						// AdvancedPlayer has another method to start the song at a specified position:
						advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
					} else {
						// PLAY MUSIC : from the BEGINNING
						advancedPlayer.play();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public void playbackStarted(PlaybackEvent evt) {
		// This method is called in the BEGINNING of the song
		System.out.println("Playback Started");
	}
	
	@Override
	public void playbackFinished(PlaybackEvent evt) {
		// This method is called when : * song FINISHES or, * the player gets CLOSED
		System.out.println("Playback Finished");
		
		if(isPaused) {
			
			// *** Play-back Event has a method 'getFrame()', that returns the milliseconds of the songs (when paused)
			// This gives us the current position of the song, when the song has been 'paused'
			// Converting milliseconds value to frame-value
			
			currentFrame += (int)((double)evt.getFrame() * currentSong.getFrameRatePerMillisecond()); 
			
// A portion to check timings
//			System.out.println("Stopped @" + currentFrame);
		}
		
	}
}
