import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class StatsPanel extends JPanel {
	/** This class was created by Cary Xiao on January 20, 2019
	 *  This class showcases the statistics panel, which tells 
	 *  you various data about your playing.
	 */
	public int fileLines = 0;
	private int playerLines = 0;
	private int correctLines = 0;
	private double lineRatio = 0;
	public float fileLoudest = 0;
	public double playerLoudest = 0;
	
	private JPanel fileLPanel;
	private JPanel playerLPanel;
	private JPanel correctLPanel;
	private JPanel lineRPanel;
	private JPanel fileLoudPanel;
	private JPanel playerLoudPanel;
	
	public StatsPanel(SpectrogramPanel inPanel) {
		
		this.setPreferredSize(new Dimension(560, 515));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(new TitledBorder("Statistics"));
		fileLPanel = new JPanel(); 
		playerLPanel = new JPanel();
		correctLPanel = new JPanel();
		lineRPanel = new JPanel();
		fileLoudPanel = new JPanel();
		playerLoudPanel = new JPanel();
		
		fileLPanel.add(new JLabel("Lines Drawn for File: " + fileLines));
		playerLPanel.add(new JLabel("Lines Drawn for Player: " + playerLines));
		correctLPanel.add(new JLabel("Lines Overlapping: " + correctLines));
		lineRPanel.add(new JLabel("Percent of lines Correct: " + lineRatio + "%"));
		fileLoudPanel.add(new JLabel("Loudest Sound for File: " + fileLoudest));
		playerLoudPanel.add(new JLabel("Loudest Sound for Player: " + playerLoudest));
		
		this.add(fileLPanel);
		this.add(playerLPanel);
		this.add(correctLPanel);
		this.add(lineRPanel);
		this.add(fileLoudPanel);
		this.add(playerLoudPanel);
	}
	public void updateFileLine() {
		fileLines++;
		fileLPanel.removeAll();
		fileLPanel.add(new JLabel("Lines Drawn for File: " + fileLines));
		revalidate();
		repaint();
	}
	public void addPlayerLine() {
		playerLines++;
		playerLPanel.removeAll();
		playerLPanel.add(new JLabel("Lines Drawn for Player: " + playerLines));
		revalidate();
		repaint();
	}
	public void addCorrectLine() {
		correctLines++;
		correctLPanel.removeAll();
		correctLPanel.add(new JLabel("Lines Overlapping: " + correctLines));
		
		lineRatio = correctLines / (fileLines / 100);
		lineRPanel.removeAll();
		lineRPanel.add(new JLabel("Percent of lines Correct: " + lineRatio + "%"));
		
		revalidate();
		repaint();
	}
	public void setFileMax(double newMax) {
		fileLoudest = (float) newMax;
		fileLoudPanel.removeAll();
		fileLoudPanel.add(new JLabel("Loudest Sound for File: " + fileLoudest));
		revalidate();
		repaint();
	}
	public void setPlayerMax(double newMax) {
		playerLoudest = newMax;
		playerLoudPanel.removeAll();
		playerLoudPanel.add(new JLabel("Loudest Sound for Player: " + playerLoudest));
		revalidate();
		repaint();
	}
	public void resetUserStats() {
		playerLines = 0;
		correctLines = 0;
		lineRatio = 0;
		playerLoudest = 0;
		 	
		playerLPanel.removeAll();
		correctLPanel.removeAll();
		lineRPanel.removeAll();
		playerLoudPanel.removeAll();
		
		playerLPanel.add(new JLabel("Lines Drawn for Player: " + playerLines));
		correctLPanel.add(new JLabel("Lines Overlapping: " + correctLines));
		lineRPanel.add(new JLabel("Percent of lines Correct: " + lineRatio + "%"));
		playerLoudPanel.add(new JLabel("Loudest Sound for Player: " + playerLoudest));
	}
	public void resetFileStats() {
		playerLines = 0;
		correctLines = 0;
		lineRatio = 0;
		playerLoudest = 0;
		 	
		playerLPanel.removeAll();
		correctLPanel.removeAll();
		lineRPanel.removeAll();
		playerLoudPanel.removeAll();
		
		playerLPanel.add(new JLabel("Lines Drawn for Player: " + playerLines));
		correctLPanel.add(new JLabel("Lines Overlapping: " + correctLines));
		lineRPanel.add(new JLabel("Percent of lines Correct: " + lineRatio + "%"));
		playerLoudPanel.add(new JLabel("Loudest Sound for Player: " + playerLoudest));
	}
}
