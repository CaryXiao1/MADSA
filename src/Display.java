import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.border.TitledBorder; 

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;
import javax.swing.JCheckBox;

public class Display extends JFrame implements PitchDetectionHandler {

	
	/** This class was created by Cary Xiao, started on November 1, 2018, and finished on Febuary .
	 *  "Display" is intended to create the window of the program and call 
	 *  the other classes to create the full window for the program. For the
	 *  run panel, I included it in here because I use the run panel to call 
	 *  the spectrogram so that when creating a new one, it has a file
	 */
	private Display display;
	private InputPanel inputPanel;
	public SpectrogramPanel panel;
	private JPanel specPanel;
	private JPanel runPanel;
	private JPanel graphPanel;
	private JPanel playFilePanel;
	private JCheckBox playFileCheck;
	private JButton resetGraphBtn;
	private MetronomePanel metroPanel; // metronome
	private AudioDispatcher dispatcher;
	public Mixer currentMixer;

	private JScrollPane specScrollPane;
	public AmplitudePanel ampPanel;
	private JScrollPane ampScrollPane;
	public StatsPanel statsPanel;
	public String fileName;// change to filePath later
	public String initFileName = "";
	
	public JButton graphBtn; // used to graph & record, initialized here
	public JButton recBtn;   // so that we can change if they're enabled
	public JButton pauseBtn; // outside of the constructor.

	private double pitch; 
	
	private int sampleRate = 44100;
	private int bufferSize = 1024 * 4;
	private int overlap = bufferSize / 2;
	
	public Clip playFileClip = null;
	private boolean isStopped = false;

	public Display() {// Constructor for window referenced in main method
		display = this;
		
		JPanel mainPanel;
		JPanel otherPanel;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Music Pitch Analysis");
		
		mainPanel = new JPanel(new BorderLayout(0, 0));
		mainPanel.setPreferredSize(new Dimension(1344, 1030));
		
		otherPanel = new JPanel();
		otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.PAGE_AXIS));
		
		inputPanel = new InputPanel(this);

		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		
		runPanel = new JPanel(new GridLayout(5, 1, 10, 5));
		runPanel.setBorder(new TitledBorder(""));
		runPanel.setMaximumSize(new Dimension(130, getHeight()));
		runPanel.setPreferredSize(new Dimension(130, getHeight()));
		
		graphPanel = new JPanel();
		graphPanel.setLayout(new GridLayout(5, 1, 2, 2));
		graphBtn = new JButton("Graph File");
		graphBtn.setEnabled(false);
		graphPanel.add(graphBtn);
		resetGraphBtn = new JButton("Remove User Input"); // not added unless file is the same & graph Btn is pressed
		resetGraphBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.loadGraph();
				ampPanel.loadGraph();
				statsPanel.resetUserStats();
				panel.position = 0;
				revalidate();
				repaint();
			}	
		}
		);
		resetGraphBtn.setEnabled(false);
		graphBtn.addActionListener(startSpectrogram);

		runPanel.add(graphPanel);
		
		JPanel recPanel = new JPanel();
		
		recPanel.setLayout(new GridLayout(4, 1, 2, 2));
		recBtn = new JButton("Start Playing");
		recBtn.setEnabled(false);
		recPanel.add(recBtn);
		recBtn.addActionListener( new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean played = false;

				isStopped = false;
						
				fileName = null;
				panel.position = 0;
					
				if (metroPanel.usingMetro.isSelected()) { // plays metronome before recording
					synchronized (metroPanel) {
						played = metroPanel.startMetronome();
					}
					if (!played) return;	
				}
				pauseBtn.setEnabled(true);
				ampPanel.position = 0;
				try {
					setNewMixer(inputPanel.soundMixer);
					} catch (UnsupportedAudioFileException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}	
		}
		);
		pauseBtn = new JButton("Pause Playing");
		pauseBtn.setEnabled(false);
		recPanel.add(pauseBtn);
		pauseBtn.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isStopped) {
					isStopped = true;
				}	else {
					isStopped = false;
					try {
						setNewMixer(currentMixer);
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		);
		runPanel.add(recPanel);
		
		playFilePanel = new JPanel();
		playFilePanel.setLayout(new BoxLayout(playFilePanel, BoxLayout.PAGE_AXIS));
		playFileCheck = new JCheckBox("Speaker File Graph");
		playFilePanel.add(playFileCheck);
		playFilePanel.add(new JLabel("(Used for Unrecognizable"));
		playFilePanel.add(new JLabel(" File Graphs)"));
		runPanel.add(playFilePanel);
		
		metroPanel = new MetronomePanel(this);
		runPanel.add(metroPanel);
		
		mainPanel.add(runPanel, BorderLayout.LINE_START);
		
		ampPanel = new AmplitudePanel();
		
		ampScrollPane = new JScrollPane(ampPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		ampScrollPane.setBorder(new TitledBorder("Graph Comparing Dynamics"));
		ampScrollPane.setPreferredSize(new Dimension(560, 515));
		otherPanel.add(ampScrollPane);
		
		statsPanel = new StatsPanel(panel);
		statsPanel.setPreferredSize(new Dimension(550, 500));
		otherPanel.add(statsPanel);
		
		specPanel = new JPanel(new BorderLayout());
		specScrollPane = new JScrollPane(specPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        specScrollPane.setPreferredSize(new Dimension(0,0));
		mainPanel.add(specScrollPane, BorderLayout.CENTER);
		
		add(mainPanel);
		add(otherPanel);
		
	}
	// Listener for "remove user input" btn, replaces old drawing with condition right after it finished graphing file

	// Listener for start recording btn, starts recording and sometimes starts metronome

	// Listener for pause btn
	// changes algorithm of pitch detection, something in my old code

	// Listener for file graph, 
	private ActionListener startSpectrogram = new ActionListener() { // Starts spectrogram & amplitude panel 
		@Override
		public void actionPerformed(ActionEvent arg0) {
	        specPanel.removeAll();
	        fileName = InputPanel.fileName;
	        
			try {
				
				panel = new SpectrogramPanel(fileName, display);
				panel.position = 0;
				
				ampPanel.initAmplitude();
			
				ampScrollPane.revalidate();
				ampScrollPane.repaint();
				
				panel.hasReadFile = false;
				specPanel.add(panel, BorderLayout.CENTER);
				initFileName = fileName;
				setNewMixer(inputPanel.soundMixer);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
	        revalidate();
	        repaint();
		
		}
	};
	//changes the button from file graph to remove user input button and vise versa
	public void changeGraphBtn() {
		System.out.print("The Big Oof");
		if (panel.hasReadFile && initFileName == InputPanel.fileName) {
			
			if (graphBtn.isEnabled()) {
			graphPanel.remove(graphBtn);
			graphBtn.setEnabled(false);
			resetGraphBtn.setEnabled(true);
			graphPanel.add(resetGraphBtn);
			} 
		}
		
		else if (panel.hasReadFile) {
				graphPanel.remove(resetGraphBtn);
				resetGraphBtn.setEnabled(false);
				graphBtn.setEnabled(true);
				graphPanel.add(graphBtn);
			}
			revalidate();
			repaint();
		
		
	}
	
	void setNewMixer(Mixer mixer) throws LineUnavailableException, UnsupportedAudioFileException {
		
		
		if(dispatcher!= null){
			dispatcher.stop();
		}
		
		currentMixer = mixer;
		
		if(fileName == null || fileName != null && playFileCheck.isSelected()){
			
			final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
					false);
			final DataLine.Info dataLineInfo = new DataLine.Info(
					TargetDataLine.class, format);
			TargetDataLine line;
			line = (TargetDataLine) mixer.getLine(dataLineInfo);
			final int numberOfSamples = bufferSize;
			line.open(format, numberOfSamples);
			line.start();
			final AudioInputStream stream = new AudioInputStream(line);

			JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
			// create a new dispatcher
			
			panel.hasStarted = false;
			if (playFileCheck.isSelected() && !panel.hasReadFile) {
				dispatcher = new AudioDispatcher(audioStream,bufferSize, overlap);
				try { // plays file to more accurately get the file data
					panel.hasReadFile = false;
					File audioFile = new File(fileName);
			        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile.getAbsoluteFile()); 

			        playFileClip = AudioSystem.getClip();  // create clip reference 
			        playFileClip.open(audioInputStream);  // open audioInputStream to the clip
			        playFileClip.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
		} else { 
			
				try {
					panel.hasReadFile = false;
					File audioFile = new File(fileName);
					dispatcher = AudioDispatcherFactory.fromFile(audioFile, (int) (bufferSize * 1.044), overlap);
				} catch (IOException e) {
					e.printStackTrace();

				
			}
		}

		// add a processor, handle pitch event.
		dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.DYNAMIC_WAVELET, sampleRate, bufferSize, this));
		dispatcher.addAudioProcessor(fftProcessor);
		
		panel.hasStarted = false;

		// run the dispatcher (on a new thread).
		new Thread(dispatcher,"Audio dispatching").start();

		
		
		if (panel.hasReadFile && metroPanel.usingMetro.isSelected() && metroPanel.playInRec.isSelected()) {
			new Thread(metroPanel).start();
		}
		
		
	}
	
	AudioProcessor fftProcessor = new AudioProcessor(){
		
		FFT fft = new FFT(bufferSize);
		float[] amplitudes = new float[bufferSize/2];
		


		@Override
		public boolean process(AudioEvent audioEvent) {
			// Stops the spectrogram if you press pause
			if (isStopped) { 
				metroPanel.stop();    
				dispatcher.stop();
				}
			// Stops the spectrogram if you play to the limit of the file or the file clip you're through speakers ends
			if (!panel.hasReadFile && playFileClip != null && !playFileClip.isRunning()) {
				metroPanel.stop();
				statsPanel.updateFileLine();
				dispatcher.stop();
			}
			if (panel.hasReadFile && panel.position >= panel.endPosition) {
				
				metroPanel.stop();	
				dispatcher.stop();
			}
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize*2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length); 
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			panel.drawFFT(pitch, amplitudes,fft, 50, 20000 );
			
			if (panel.position > panel.initWidth) {                      // enables the scrollbar for the specPanel &
				Dimension area = new Dimension(panel.position, panel.getHeight()); // ampPanel when specPanel gets too big
				panel.setPreferredSize(area);
				panel.revalidate();
				panel.repaint();
			}
			if (ampPanel.position > panel.initWidth) {                      // enables the scrollbar for the specPanel &
				Dimension area = new Dimension(ampPanel.position, panel.getHeight());
				ampPanel.setPreferredSize(area);
				ampPanel.revalidate();
				ampPanel.repaint();
			}
			if (!panel.hasReadFile) panel.endPosition = panel.position;
			return true;
		}
		@Override
		public void processingFinished() {
			// TODO Auto-generated method stub;
			if (!panel.hasReadFile) {
				//panel.backupBG = panel.bufferedGraphics;
				panel.saveGraph();
				ampPanel.saveGraph();
				//= panel.bufferedImage;
				
				panel.hasReadFile = true;
				changeGraphBtn();
			}
			
			if (inputPanel.soundMixer != null) recBtn.setEnabled(true);	
			

			statsPanel.updateFileLine();
		}

	};
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
		if(pitchDetectionResult.isPitched()){
			pitch = pitchDetectionResult.getPitch();
		} else {
			pitch = -1;
		}
		
	}
	

}
