import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class MetronomePanel extends JPanel implements Runnable {
	/**This Class was created by Cary Xiao on December 27, 2018
	 * This class creates the metronome and plays the metronome 
	 * to help players stay with the file and get a more accurate
	 * reading
	 */

	private File metroSound;
	private JPanel thisPanel;
	public JCheckBox usingMetro;
	private JPanel mainPanel;
	private JLabel tempoLabel;
	public JTextField tempoField;
	private JLabel timeSigLabel;
	public JTextField timeSigField;
	public JCheckBox playInRec;
	
	private JLabel errorMessage1;
	private JLabel errorMessage2;
	
	private double tempo;
	private Clip clip;
	private int timeSig;
	private AudioInputStream audioIn;
	private AudioFormat af;
	private DataLine.Info info;
	private byte[] audio;
	private int size;
	private int rawTime;
	
	private AtomicBoolean running = new AtomicBoolean();
	
	public MetronomePanel(Display inDisplay) {
		thisPanel = this; // used to call metronomePanel in actionListener
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		metroSound = new File("C:/Users/gameb/eclipse-workspace/Science Fair Project/files/MetronomeSound.wav");
		
		usingMetro = new JCheckBox("Using Metronome");
		usingMetro.addActionListener(checkListener);
		this.add(usingMetro);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setMaximumSize(new Dimension(125, 170));
		mainPanel.setPreferredSize(new Dimension(125, 170));
		mainPanel.setBorder(new TitledBorder("Metronome"));
		
		tempoLabel = new JLabel("Set Tempo");
		tempoField = new JTextField("60");
		tempoField.setMaximumSize(new Dimension(70, 30));
		mainPanel.add(tempoLabel);
		mainPanel.add(tempoField); 
		
		timeSigLabel = new JLabel("Set Starting beats");
		timeSigField = new JTextField("4");
		timeSigField.setMaximumSize(new Dimension(70, 30));
		mainPanel.add(timeSigLabel);
		mainPanel.add(timeSigField);
		
		playInRec = new JCheckBox("Play in Recording", true);
		mainPanel.add(playInRec);
		
		errorMessage1 = new JLabel();
		errorMessage2 = new JLabel();
	}
	private ActionListener checkListener = new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (usingMetro.isSelected()) thisPanel.add(mainPanel);
		else thisPanel.remove(mainPanel);
		
		thisPanel.revalidate();
		thisPanel.repaint();
	}
	};
	public boolean startMetronome() { // plays the metronome before actually starting the recording
		tempo = 0;
		timeSig = 0;
		try {
			tempo = Double.parseDouble(tempoField.getText());
			timeSig = (int) Double.parseDouble(timeSigField.getText());
		} catch (NumberFormatException | NullPointerException nfe) {
			returnError("Tempo or Starting Beats Not a Number");
			return false;
		}
		
		if (tempo < 20 || tempo > 500 ) {
			returnError("Tempo must be between 20 and 500");
			return false;
		}
		if (timeSig <= 0 || timeSig > 50) {
			returnError("Beats must be between 1 and 50");
			return false;
		}
	
		
		try {
        audioIn = AudioSystem.getAudioInputStream(metroSound);
        af = audioIn.getFormat();
        size = (int) (af.getFrameSize() * audioIn.getFrameLength());
        audio = new byte[size];
        info = new DataLine.Info(Clip.class, af, size);
		audioIn.read(audio, 0, size);
		rawTime = (int) (60 * 1000 / tempo);

		synchronized (thisPanel) {
			for (int i = 0; i < timeSig; i++) {
	            clip = (Clip) AudioSystem.getLine(info);
	            clip.open(af, audio, 0, size);
	            clip.start();
				wait(rawTime); 
		}
		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return true;
	}
	public void run() {
		running.set(true);
		synchronized (thisPanel) {
		while (running.get()) {
				try {
				clip = (Clip) AudioSystem.getLine(info);
	            clip.open(af, audio, 0, size);
	            clip.start();
				wait(rawTime); 
				
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	public void stop() {
		running.set(false);
	}
	private void returnError(String error) {
		mainPanel.remove(errorMessage1);
		mainPanel.remove(errorMessage2);
		errorMessage1 = new JLabel(error.substring(0, error.length() / 2));
		errorMessage2 = new JLabel(error.substring(error.length() / 2, error.length()));
		mainPanel.add(errorMessage1);
		mainPanel.add(errorMessage2);
		mainPanel.revalidate();
		mainPanel.repaint();
	}

}
