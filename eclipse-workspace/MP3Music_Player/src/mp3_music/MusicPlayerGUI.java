package mp3_music;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlayerGUI extends JFrame{
//NOTE:	//MusicPlayerGUI subclass of JFrame and so, it represents
	    // Main Window of our application
	
	
	// Color Configurations
	// Assuming we have double values for hue, saturation, and brightness
	static double hueDouble = 25.0; 
	static double saturationDouble = 0.78; 
	static double brightnessDouble = 0.5;

	// Convert double values to float
	static float hue_ = (float) hueDouble;
	static float saturation_ = (float) saturationDouble;
	static float brightness_ = (float) brightnessDouble;
	public static final Color FRAME_COLOR = Color.getHSBColor(hue_, saturation_, brightness_);
	//public static final Color FRAME_COLOR = Color.BLACK;
	public static final Color TEXT_COLOR = Color.WHITE;
	
	private MusicPlayer musicPlayer;
	
	private JLabel songTitle, songArtist;
	private JPanel playbackButtons;
	
	// Allowing to use "File Explorer" in our Application
	private JFileChooser jFileChooser;
	
	
	public MusicPlayerGUI() {
		
		// calls JFrame constructor to configure out the GUI & set the title-header to "Music Player"
		super("Tune Trip");

//super() is a keyword in Java used to call a superclass constructor from a subclass constructor.
//It is used to invoke the constructor of the immediate superclass
		
		// size of the MusicPlayer GUI itself
		setSize(400, 600);
		
		// end process when application is closed
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// To launch the application at the screen-centre
		setLocationRelativeTo(null);
		
		// To prevent the application from being resized
		setResizable(false);
		
		// We can control the (x,y) coordinate components by setting layout to null
		// Also, set the height & width
		setLayout(null);
		
		// Changing the frame color
		getContentPane().setBackground(FRAME_COLOR);
		
		musicPlayer = new MusicPlayer(); // Instantiate Music Player object in our constructor
		jFileChooser = new JFileChooser();
		
		// Set a default path for file explorer
		jFileChooser.setCurrentDirectory(new File("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\assets"));
        
		// Filtering File-Chooser to only see MP3 files(.mp3 extension)
		jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3")); // description // ...extensions
		
		addGUIcomponents();		
	}
	
	
	private void addGUIcomponents() {
		// Adding toolBar
		addToolbar();
		
		// Play back Buttons
		addPlaybackButtons();
		
		// Adding a transparent panel as a background
		    JPanel transparentPanel = new JPanel();
		    transparentPanel.setBounds(15, 264, getWidth()-42, 236);
		    transparentPanel.setBackground(new Color(0, 0, 0, 176)); // Adjust transparency here
		    transparentPanel.setLayout(new BoxLayout(transparentPanel, BoxLayout.Y_AXIS)); // Set layout to Y_AXIS
		
		add(transparentPanel);
		
		// Song Title
			songTitle = new JLabel("Song Title");
			songTitle.setBounds(0, 306, getWidth()-10, 40);
			songTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // To center align the component horizontally
			songTitle.setFont(new Font("Dialog", Font.BOLD, 30));
			songTitle.setForeground(TEXT_COLOR);
				
		transparentPanel.add(songTitle);
				
		// Song Artist
		    songArtist = new JLabel("Artist");
		    songArtist.setBounds(0, 368, getWidth()-10, 30);
		    songArtist.setAlignmentX(Component.CENTER_ALIGNMENT);
		    songArtist.setFont(new Font("Dialog", Font.PLAIN, 25));
		    songArtist.setForeground(TEXT_COLOR);
		    songArtist.setHorizontalAlignment(SwingConstants.CENTER);
				
		transparentPanel.add(songArtist);
		
		// Play back slider
			JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); // //min //max //value
			playbackSlider.setBounds(getWidth()/2 - 156, 375, 300, 14);//x //y //width //height
			playbackSlider.setBackground(null);
		
			add(playbackSlider);
		
		// load record image
			JLabel songImage = new JLabel(loadImage("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\Photo by Jakob Rosen on Unsplash.jpg"));
			songImage.setBounds(15, 0, getWidth()-42, 600); //x //y //getWidth() //height
		
		add(songImage);
		
	}
	
	
	private void addToolbar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 0, getWidth(), 20);  //x //y //getWidth() //height
		
		// To prevent toolBar from being moved
		toolBar.setFloatable(false);
		
		// Adding drop-down menu
		JMenuBar menuBar = new JMenuBar();
		toolBar.add(menuBar);
		
		// Creating song menu to place the loading song-option
		JMenu songMenu = new JMenu("Song");
		menuBar.add(songMenu);
		
		// We have to add the 'load-song' item to the song-menu
		JMenuItem loadSong = new JMenuItem("Load Song");
		loadSong.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				// An Integer should be returned to us to let us know what the user did
				int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
				File selectedFile = jFileChooser.getSelectedFile();
				
				//  It is MUST (so we checked to see) if the user pressed the "Open" Button
				if(result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
					// Creating a song object based on SELECTED FILE
					Song song = new Song(selectedFile.getPath());
					
					// Loading song in Music Player
					musicPlayer.loadSong(song);
					
					// Updating SongTitle and Artist
					update_SongTitle_and_Artist(song);
					
					// Toggle-ON 'Pause' Button & Toggle-OFF 'Play' Button
					enable_PauseButton_disable_PlayButton();
					
				}
			}
		});
		songMenu.add(loadSong);
		
		// Adding Play-list menu
		JMenu playlistMenu = new JMenu("Playlist");
		menuBar.add(playlistMenu);
		
		// Adding items to the Play-list menu
		JMenuItem createPlaylist = new JMenuItem("Create Playist");
		JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
		playlistMenu.add(createPlaylist);
		playlistMenu.add(loadPlaylist);
		
		
		
		add(toolBar);
	}
	
	
	private void addPlaybackButtons() {
		playbackButtons = new JPanel();
		playbackButtons.setBounds(-15, 438, getWidth()+15, 51); //x //y //width //height
		playbackButtons.setBackground(null);
		
		// 'Previous' Button
			JButton prevButton = new JButton(loadImage("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\previous (1).png"));
			prevButton.setBorderPainted(false);
			prevButton.setBackground(null);
			
		playbackButtons.add(prevButton);
		
		// 'Play' Button
			JButton playButton = new JButton(loadImage("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\play (1).png"));
			playButton.setBorderPainted(false);
			playButton.setBackground(null);
			playButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					// Toggle OFF 'Play' Button & Toggle ON 'Pause' Button
					enable_PauseButton_disable_PlayButton();
					
					// Play or Resume the current song
						musicPlayer.playCurrentSong();
				}
			});
					
		playbackButtons.add(playButton);
		
		// 'Pause' Button
			JButton pauseButton = new JButton(loadImage("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\pause (1).png"));
			pauseButton.setBorderPainted(false);
			pauseButton.setBackground(null);
			pauseButton.setVisible(false);
			pauseButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					// Toggle OFF 'Pause' Button & Toggle ON 'Play' Button
						enable_PlayButton_disable_PauseButton();
					
					// Pause the song
						musicPlayer.pauseSong();
				}
			});
							
		playbackButtons.add(pauseButton);
		
		// 'Next' Button
			JButton nextButton = new JButton(loadImage("C:\\Users\\Lenovo\\eclipse-workspace\\MP3Music_Player\\src\\next (1).png"));
			nextButton.setBorderPainted(false);
			nextButton.setBackground(null);
									
		playbackButtons.add(nextButton);
		
		add(playbackButtons);
	}
	
	
	private void update_SongTitle_and_Artist(Song song) {
		songTitle.setText(song.getsongTitle());
		songArtist.setText(song.getsongArtist());
	}
	
	
	private void enable_PauseButton_disable_PlayButton() {
		// Retrieve reference to 'Play' Button from playbackButtons panel
		JButton playButton = (JButton) playbackButtons.getComponent(1);
		JButton pauseButton = (JButton) playbackButtons.getComponent(2);
		
		// Turn OFF 'Play' Button
		playButton.setVisible(false);
		playButton.setEnabled(false);
		
		// Turn ON 'Pause' Button
		pauseButton.setVisible(true);
		pauseButton.setEnabled(true);
	}
	
	
	private void enable_PlayButton_disable_PauseButton() {
		// Retrieve reference to 'Pause' Button from playbackButtons panel
		JButton playButton = (JButton) playbackButtons.getComponent(1);
		JButton pauseButton = (JButton) playbackButtons.getComponent(2);
		
		// Turn ON 'Play' Button
		playButton.setVisible(true);
		playButton.setEnabled(true);
		
		// Turn OFF 'Pause' Button
		pauseButton.setVisible(false);
		pauseButton.setEnabled(false);
	}
	
	
	private ImageIcon loadImage(String imagePath) {
		try {
			// To READ the image file from the given path
			BufferedImage image = ImageIO.read(new File(imagePath));
			
			// Returns an image icon so that component can render the image
			return new ImageIcon(image);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		// If could NOT FIND RESOURCES
		return null;
	}
}
