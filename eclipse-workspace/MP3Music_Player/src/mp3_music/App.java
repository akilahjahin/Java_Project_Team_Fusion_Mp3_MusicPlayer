package mp3_music;

import javax.swing.SwingUtilities;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new MusicPlayerGUI().setVisible(true);
				
				//Song song = new Song("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\Wind Riders - Asher Fulero.mp3");
//				System.out.println(song.getsongTitle());
//				System.out.println(song.getsongArtist());
			}
		});
	}
}
