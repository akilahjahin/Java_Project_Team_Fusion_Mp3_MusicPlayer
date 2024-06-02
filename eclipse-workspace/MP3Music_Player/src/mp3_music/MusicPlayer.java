package mp3_music;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener {
	
	// Used to UPDATE 'isPaused' more synchronously
	private static final Object playSignal = new Object();
	
	// REFERENCE required to UPDATE GUI in this class
	private MusicPlayerGUI musicPlayerGUI;
	
	// A way is needed to store our Songs' Details : Hence, this song class
	private Song currentSong;
	public Song getCurrentSong() {
		return currentSong;
	}
	
	private ArrayList<Song> playlist; 
	
	// We need to keep track of the index we are in the play-list
	private int currentPlaylistIndex = 0;
	
	// Using JavaZoom JLayer library to create an 'Advanced Player object' which will handle playing the music
	private AdvancedPlayer advancedPlayer;
	
	// pause Boolean Flag : To INDICATE WHETHER THE PLAYER has been 'PAUSED'
	private boolean isPaused;
	
	// Boolean Flag used to indicate when the song has finished
	private boolean songFinished;
	
	private boolean pressedNext, pressedPrev;
	
	// Stores in the LAST-FRAME when the play back is 'finished':
	// Used for PAUSING & RESUMING
	private int currentFrame;
	public void setCurrentFrame(int frame) {
		currentFrame = frame;
	}
	
	// We have to keep TRACK of the Timer by ourselves as JLayer as no way
	// To TRACK 'how many milliseconds' has passed since tracking the song
	private int currentTimeIn_Milliseconds;
	public void setCurrentTimeIn_Milliseconds(int timeInMillisecs) {
		currentTimeIn_Milliseconds = timeInMillisecs;
	}
	
	// constructor
	public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
		this.musicPlayerGUI = musicPlayerGUI;
	}
    
	public void loadSong(Song song) {
		
		currentSong = song;
		playlist = null;
		
		// STOP the song (if possible)
		if(!songFinished)
			stopSong();
		
		// Playing the current song if not null
		if(currentSong != null) {
			// RESET Frame
			currentFrame = 0;
			
			// RESET Current Time in milliseconds
			currentTimeIn_Milliseconds = 0;
			
			// UPDATE GUI
			musicPlayerGUI.setPlaybackSlider_Value(0);
			
			playCurrentSong();
		}
	}
	
	public void loadPlaylist(File playlistFile) {
		
		playlist = new ArrayList<>();
		
		// STORE the paths from the text file into the play-list array list
		try {
			FileReader fileReader = new FileReader(playlistFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// REACH each line from the text-file and store the text into the songPath variable
			String songPath;
			while((songPath = bufferedReader.readLine()) != null) {
				// CREATE song object based on song path
				Song song = new Song(songPath);
				
				// ADD to play-list array-list
				playlist.add(song);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(playlist.size() > 0) {
			// RESET play_back slider
			musicPlayerGUI.setPlaybackSlider_Value(0);
			currentTimeIn_Milliseconds = 0;
			
			// UPDATE current song to the 1st song in the play-list
			currentSong = playlist.get(0);
			
			// START from the beginning frame
			currentFrame = 0;
			
			// UPDATE GUI
			musicPlayerGUI.enable_PauseButton_disable_PlayButton();
			musicPlayerGUI.update_SongTitle_and_Artist(currentSong);
			musicPlayerGUI.update_Playback_Slider(currentSong);
			
			// START SONG
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
	
	public void nextSong() {
		// NO NEED to go to the next song if there is NO Play-list
		if(playlist == null) return;
		
		// TO CHECK IF REACHED THE PLAYLIST END : IF SO, DON'T DO ANYTHING
		if(currentPlaylistIndex + 1 > playlist.size() - 1) return;
		
		pressedNext = true;
		
		// STOP the song (if possible)
		if(!songFinished)
			stopSong();
		
		// Increase CURRENT Play-list index 
		currentPlaylistIndex++;
		
		// UPDATE CURRENT Song
		currentSong = playlist.get(currentPlaylistIndex);
		
		// RESET Frame
		currentFrame = 0;
		
		// RESET CURRENT Frame in milliseconds
		currentTimeIn_Milliseconds = 0;
		
		// UPDATE GUI
		musicPlayerGUI.enable_PauseButton_disable_PlayButton();
		musicPlayerGUI.update_SongTitle_and_Artist(currentSong);
		musicPlayerGUI.update_Playback_Slider(currentSong);
		
		// PLAY the song
		playCurrentSong();
	}
	
	public void prevSong() {
		// NO NEED to go to the next song if there is NO Play-list
		if(playlist == null) return;
		
		// TO CHECK IF ANY PREVIOUS SONG EXISTS IN THE PLAYLIST
		if(currentPlaylistIndex - 1 < 0) return;
		
		pressedPrev = true;
		
		// STOP the song (if possible)
		if(!songFinished)
			stopSong();
		
		// Decrease CURRENT Play-list index 
		currentPlaylistIndex--;
		
		// UPDATE CURRENT Song
		currentSong = playlist.get(currentPlaylistIndex);
		
		// RESET Frame
		currentFrame = 0;
		
		// RESET CURRENT Frame in milliseconds
		currentTimeIn_Milliseconds = 0;
		
		// UPDATE GUI
		musicPlayerGUI.enable_PauseButton_disable_PlayButton();
		musicPlayerGUI.update_SongTitle_and_Artist(currentSong);
		musicPlayerGUI.update_Playback_Slider(currentSong);
		
		// PLAY the song
		playCurrentSong();
	}
	
	public void playCurrentSong() {
		// To avoid error-showing as currentSong was null at the beginning when 'play' button pressed
		if(currentSong == null) {
			return;
		}
					
		try {
			// READ MP3 Audio Data
			FileInputStream fileInputStream = new FileInputStream(currentSong.getfilePath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			
			// Creating a new Advanced Player
			advancedPlayer = new AdvancedPlayer(bufferedInputStream);
			advancedPlayer.setPlayBackListener(this);
            
			// START MUSIC
			startMusic_Thread();
			
			// START play-back slider thread
			startPlaybackSlider_Thread();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// Creating a thread that will handle playing the music
	private void startMusic_Thread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					if(isPaused) {
						synchronized (playSignal) {
							// UPDATE 'isPaused' flag to false if the music is played 
							// Since update_slider_Thread will not work until 'isPaused' is false
							isPaused = false;
							
							// Notify other thread to continue(ensures 'isPaused' being set to false properly)
							playSignal.notify();
						}
						
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

	
	// A Thread to handle UPDATING the slider
	private void startPlaybackSlider_Thread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if (isPaused) {
					try {
						// WAIT till it get NOTIFIED BY other Thread to continue
						// To make sure that isPaused boolean flag updates to false BEFORE CONTINUING
						synchronized (playSignal) {
							playSignal.wait();
							
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				
				while(!isPaused && !songFinished && !pressedNext && !pressedPrev) {
					try {
						// Increment current time in milliseconds
						currentTimeIn_Milliseconds++;
						
						// Calculate milliseconds to Frame since slider is relevant to the frame
						int calculatedFrame = (int)((double) currentTimeIn_Milliseconds * 2.08 * currentSong.getFrameRatePerMillisecond());
						
						// UPDATE GUI
						musicPlayerGUI.setPlaybackSlider_Value(calculatedFrame);
						
						// Mimic 1 millisecond using Thread sleep
						Thread.sleep(1);
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	
	@Override
	public void playbackStarted(PlaybackEvent evt) {
		// This method is called in the BEGINNING of the song
		System.out.println("Playback Started");
		songFinished = false;
		pressedNext = false;
		pressedPrev = false;
		
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
		} else {
			// If user presses 'Previous' OR 'Next' Buttons, we don't execute the rest of the following code
			if(pressedNext || pressedPrev) return;
			
			// when the song ENDS, it will attempt to go to the NEXT song
			songFinished = true;
					
			if(playlist == null) {
				// UPDATE GUI
				musicPlayerGUI.enable_PlayButton_disable_PauseButton();
			} else {
				// LAST Song in the play-list
				if(currentPlaylistIndex == playlist.size() - 1) {
					// UPDATE GUI
					musicPlayerGUI.enable_PlayButton_disable_PauseButton();
				} else {
					// GO to the NEXT Song in the play-list
					nextSong();
				}
			}
		}
		
	}
}
