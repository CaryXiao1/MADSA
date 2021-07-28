import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**This class was created by Cary Xiao on January 1, 2019
 * This class is designed to create the graph of the amplitudes
 * for the .wav and the real time playing
 */
public class AmplitudePanel extends JComponent implements ComponentListener {

	private BufferedImage bufferedImage;
	private BufferedImage backupBI;
	public Graphics2D bufferedGraphics;
	public int position = 0;
	public int initWidth;
	private Dimension area;
	public AmplitudePanel() {
		initWidth = getWidth();
		area = new Dimension(initWidth, getHeight());
		this.setPreferredSize(area);
	}
	public void paintComponent(final Graphics g) {
	     g.drawImage(bufferedImage, 0, 0, null);
	 }
	
	public void initAmplitude()
	{
		bufferedImage = new BufferedImage(640*4*8, 480*4, BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		this.addComponentListener(this);
		position = 0;
	}
	public void drawAmplitude(double maxAmplitude, boolean hasReadFile) { // draws the amplitude for the panel,
							  // only called through drawFFT

			//int pixeledAmplitude = (int) Math.min(maxAmplitude, 110) / 110 * getHeight();
			int pixeledAmplitude = (int) maxAmplitude;
		
			if (!hasReadFile) {
				bufferedGraphics.setColor(Color.WHITE);		
				bufferedGraphics.drawRect(position, 0, 1, getHeight());
				bufferedGraphics.setColor(new Color(0, 0, 255, 128));
				
			}
			else {
				bufferedGraphics.setColor(new Color(255, 0, 0, 128));
				
			}
			
			bufferedGraphics.fillRect(position, getHeight() - pixeledAmplitude- 275, 1, pixeledAmplitude);

		 	repaint();
		 	
			position++;
		
	}
	public void saveGraph() {
		Graphics2D graphics;
		backupBI = new BufferedImage(640*4*16,480*4, BufferedImage.TYPE_INT_RGB);
		
		graphics = backupBI.createGraphics();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		graphics.drawImage(bufferedImage, 0, 0, null);
		graphics.dispose();
	}
	// Gets backup of graph and lays it over the current graph
	public void loadGraph() {
		bufferedGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		bufferedGraphics.drawImage(backupBI, 0, 0, null);
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
