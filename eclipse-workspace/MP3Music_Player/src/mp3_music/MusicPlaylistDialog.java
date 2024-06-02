package mp3_music;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlaylistDialog extends JDialog{
	private MusicPlayerGUI musicPlayerGUI;
	
	// STORING all of the Paths in a textFile when we Load a Play-list
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
		savePlaylistButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser jFileChooser = new JFileChooser();
					jFileChooser.setCurrentDirectory(new File("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\assets"));//pathname
					int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);
					
					if(result == JFileChooser.APPROVE_OPTION) {
						// Selected File used to get reference to the file that we are about to SAVE
						File selectedFile = jFileChooser.getSelectedFile();
						
						// NOTE:
						/*
						 *Our file will be saved as a text-file
						 *If it is already not one, we check it by checking the extension .txt
						 *Otherwise, we simply convert it to ".txt"
						*/
						
						// Convert to .txt file if not done so already
						// This will check if the file does not have the ".txt" file extension
						if(!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")) {
							selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
							
						}
						
						// Create NEW File at the destinated directory
						selectedFile.createNewFile();
						
						// ALL of the Song-Paths will be written into this file
						FileWriter fileWriter = new FileWriter(selectedFile);
						BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
						
						// Iterate through our Song-Paths List & Write each string into the file
						// Each song will be written in their own row
						for(String songPath : songPaths) {
							bufferedWriter.write(songPath + "\n");
						}
						bufferedWriter.close();
						
						// Display Success Dialog
						JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Successfully Created Playlist!");
						
						// CLOSE this Dialog
						MusicPlaylistDialog.this.dispose();
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});
		add(savePlaylistButton);
	}
}
