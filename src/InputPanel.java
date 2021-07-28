import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class InputPanel extends JPanel{
	
	/**
	 * This class was created by Cary Xiao on November 19, 2018.
	 * "InputPanel" is used to create the bottom panels in the program,
	 * from the microphone selection to the pitch estimation algorithm to
	 * the file selection.
	 */

	public Mixer soundMixer = null;
	public static TargetDataLine line = null;
	final JFileChooser fc = new JFileChooser(); // file chooser for sound file button
	PitchDetectionPanel panel;
	static String fileName = null;
	JPanel filePanel;
	JLabel fileSelected;
	AudioFormat format = new AudioFormat(48000, 16, 1, true, false);
	PitchEstimationAlgorithm algo;
	Display display;
	
	public InputPanel(Display inDisplay) {
	 this.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 10));
	 this.setBorder(new TitledBorder(""));
	 
	 display = inDisplay;
	 
	 JPanel micPanel = new JPanel(); // creates panel for setting which microphone you're going to use
	 micPanel.setLayout(new BoxLayout(micPanel, BoxLayout.PAGE_AXIS));
	 micPanel.setBorder(new TitledBorder("1. Choose a Microphone Input"));
	 ButtonGroup group1 = new ButtonGroup();
		for(Mixer.Info info : Shared.getMixerInfo(false, true)){ // looks at all the mixers(mics and speakers) on the computer
			JRadioButton button = new JRadioButton();            // and makes them into a list to select
			button.setText(Shared.toLocalString(info));
			micPanel.add(button);
			group1.add(button);
			button.setActionCommand(info.toString());  // TODO make a better version of this mic selection
			button.addActionListener(setMic);  
		}
		
	 this.add(micPanel);
	 
	 filePanel = new JPanel(); // creates panel that asks for file
	 filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.PAGE_AXIS));
	 filePanel.setBorder(new TitledBorder("2. Choose a Sound File"));
	 JButton fileBtn = new JButton("Sound File");
	 filePanel.add(fileBtn);
	 fileBtn.addActionListener(setFile);
	 fileSelected = new JLabel("Current File Chosen: " + fileName); // insert name of file
	 filePanel.add(fileSelected);	
	 FileNameExtensionFilter filter = new FileNameExtensionFilter(".WAV Files", "wav", "wave");
	 fc.setFileFilter(filter);
	 this.add(filePanel);
	 
	}
	
	private ActionListener setFile = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(InputPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            fileName = fc.getSelectedFile().getPath(); // stores file name in inputPanel
		            display.fileName = fileName; // changes filename in display
		            filePanel.remove(fileSelected);
		            if (display.panel != null && display.initFileName != fileName) { // checks if a file has been graphed and if the current file matches the graped one
		            	display.changeGraphBtn();                                    //  to change graph button
		            }
		            fileSelected = new JLabel("Current File Chosen: " + fileName); // changes name of the file selected in the file selector
		            filePanel.add(fileSelected);
		            
		            filePanel.revalidate(); // updates filePanel with name
		            filePanel.repaint();    // 
		            display.graphBtn.setEnabled(true);
		        }
				
			}
		};
		
	private ActionListener setMic = new ActionListener() { // when you pick one of the mics, the program starts to listen to the mic
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(Mixer.Info info : Shared.getMixerInfo(false, true)){
				if(arg0.getActionCommand().equals(info.toString())){
					Mixer newValue = AudioSystem.getMixer(info);
					InputPanel.this.firePropertyChange("mixer", soundMixer, newValue);
					InputPanel.this.soundMixer = newValue;
					if (display.panel != null && display.panel.hasReadFile) display.recBtn.setEnabled(true);
					break;
				}
			}
		}
	};


	
}
	
