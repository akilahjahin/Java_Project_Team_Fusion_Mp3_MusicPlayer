package mp3_music;

import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

import com.mpatric.mp3agic.Mp3File;

// Class used to describe a song
public class Song {
	
	private String songTitle;
	private String songArtist;
	private String songLength;
	private String filePath;
	private Mp3File mp3File;
	private double frameRatePerMilliseconds;
	
	public Song(String filePath) {
		this.filePath = filePath;
		try {
			// With the mp3 file object we can get specific info about our MP3 file
			mp3File = new Mp3File(filePath);
			frameRatePerMilliseconds = (double)mp3File.getFrameCount()/ mp3File.getLengthInMilliseconds();
			
			// Using the jaudio_tagger library to create an audio_file object
			// To READ MP3 File Information
			
			AudioFile audioFile = AudioFileIO.read(new File(filePath));
			
			// READ through the meta-data of the audio_file
			org.jaudiotagger.tag.Tag tag = audioFile.getTag();
			if(tag != null) {
				songTitle = tag.getFirst(FieldKey.TITLE);
				songArtist = tag.getFirst(FieldKey.ARTIST);
			} else {
				// In case COULD NOT READ through MP3 File's meta-data
				songTitle = "N/A";
				songArtist = "N/A";
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// getters
	public String getsongTitle() {
		return songTitle;
	}
	
	public String getsongArtist() {
		return songArtist;
	}
	
	public String getsongLength() {
		return songLength;
	}
	
	public String getfilePath() {
		return filePath;
	}
	
	public Mp3File getMp3File() {
		return mp3File;
	}
	
	public double getFrameRatePerMillisecond() {
		return frameRatePerMilliseconds;
	}
}
