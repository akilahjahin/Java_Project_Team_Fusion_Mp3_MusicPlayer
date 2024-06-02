package mp3_music;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlaylistDialog extends JDialog{
	private MusicPlayerGUI musicPlayerGUI;
	
	// STORING all of the Paths in a textFile when we Load a Playlist
	private ArrayList<String> songPaths;
	
	public MusicPlaylistDialog (MusicPlayerGUI musicPlayerGUI) {
		this.musicPlayerGUI = musicPlayerGUI;
		songPaths = new ArrayList<>();
		
		// CONFIGURE DETAILS
		setTitle("Create Playlist");
		setSize(400, 400);
		setResizable(false);
		getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
		setLayout(null);
		setModal(true);
		setLocationRelativeTo(musicPlayerGUI);
		
		addDialogComponents();
	}
	
	private void addDialogComponents() {
		// CONTAINER to hold each song-path
		JPanel songContainer = new JPanel();
		songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(songContainer);
		scrollPane.setBounds((int)(getWidth()*0.025), 10, (int)(getWidth()*0.915), (int)(getHeight()*0.75));
		add(scrollPane);
		
		// ADD Song button
		JButton addSongButton = new JButton("Add");
		addSongButton.setBounds(60, (int)(getHeight()*0.80), 100, 25); // x, , width, height
		addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
		addSongButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// OPENING File Explorer
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
				jFileChooser.setCurrentDirectory(new File("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\assets"));//pathname
				int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);
				
				File selectedFile = jFileChooser.getSelectedFile();
				if(result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
					JLabel filePathLabel= new JLabel(selectedFile.getPath());
					filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
					filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					
					// ADD to the list
					songPaths.add(filePathLabel.getText());
					
					// ADD to the container
					songContainer.add(filePathLabel);
					
					// REFRESHES Dialog to show newly added JLabel
					songContainer.revalidate();
					
				}
				
			}
		});
		add(addSongButton);
		
		// SAVE Play-list button
		JButton savePlaylistButton = new JButton("Save");
		savePlaylistButton.setBounds(215, (int)(getHeight()*0.80), 100, 25); // x, , width, height
		savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
		add(savePlaylistButton);
	}
}
