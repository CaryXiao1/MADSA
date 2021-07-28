import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;

/** 
 * This class was created by Cary Xiao and started on November 1, 2018.
 * 
 *  "SpectrogramPanel" is designed to set up the spectrogram of the sound file
 *  you enter in and map out the loudest pitch throughout the file.
 */

public class SpectrogramPanel extends JComponent implements ComponentListener {
	private Display display;
	public BufferedImage bufferedImage;
	public BufferedImage backupBI;
	public Graphics2D bufferedGraphics;
	double[] results;

	public int position;
	public int initWidth;
	public int endPosition;
	public boolean hasStarted;
	public boolean hasReadFile = false; 
    
	public SpectrogramPanel(String fileName, Display inDisplay) throws Exception {
		display = inDisplay;
		// creates size of bufferedImage, I multiplied width by 32 so that it can show the full spectrogram
		bufferedImage = new BufferedImage(640*4*16,480*4, BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		initWidth = getWidth();
		endPosition = 1000; // px, placeholder position
		hasStarted = false;
		this.addComponentListener(this);
	}
	
	
	 private int frequencyToBin(final double frequency, final double minFrequency, final double maxFrequency) {
	        int bin = 0;
	        final boolean logaritmic = true;
	        if (frequency != 0 && frequency > minFrequency && frequency < maxFrequency) {
	            double binEstimate = 0;
	            if (logaritmic) {
	                final double minCent = PitchConverter.hertzToAbsoluteCent(minFrequency);
	                final double maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency);
	                final double absCent = PitchConverter.hertzToAbsoluteCent(frequency * 2);
	                binEstimate = (absCent - minCent) / maxCent * getHeight();
	            } else {
	                binEstimate = (frequency - minFrequency) / maxFrequency * getHeight();
	            }
	            if (binEstimate > 700) {
	                System.out.println(binEstimate + "");
	            }
	            bin = getHeight() - 1 - (int) binEstimate;
	        }
	        return bin;
	    }
	
	public void paintComponent(final Graphics g) {
     g.drawImage(bufferedImage, 0, 0, null);
 }
	
	String currentPitch = "";
	
	
	public void drawFFT(double pitch,float[] amplitudes, FFT fft, final double minFrequency, final double maxFrequency){
		double maxAmplitude = 0;
		int refAmplitude = 110; // Places all sounds under the same comparison (decibels)
	
		float[] pixeledAmplitudes = new float[getHeight()]; // creates an array to place amplitudes for each pixel
		
		 for (int i = amplitudes.length/800; i < amplitudes.length; i++) {
			 
             int pixelY = frequencyToBin(i * 44100 / (amplitudes.length * 8), minFrequency, maxFrequency);
             if (display.fileName != null && display.playFileClip == null) pixelY = Math.max(pixelY - 9, 0);
             pixeledAmplitudes[pixelY] += amplitudes[i];
             maxAmplitude = Math.max(pixeledAmplitudes[pixelY], maxAmplitude);
             
         }
		if (maxAmplitude >= 30) hasStarted = true;
		
	     if (!hasReadFile && maxAmplitude > display.statsPanel.fileLoudest) display.statsPanel.setFileMax(maxAmplitude);
	     else if (hasReadFile && maxAmplitude > display.statsPanel.playerLoudest) display.statsPanel.setPlayerMax(maxAmplitude);
	     
	     if (hasStarted) {
		 //draw the pixels 
		 if (!hasReadFile) bufferedGraphics.fillRect(position, 0, 3, getHeight());
		 
		 for (int i = 0; i < pixeledAmplitudes.length; i++) {
    		 Color color = Color.white;
    		  
             if (maxAmplitude != 0 && pixeledAmplitudes[i] >= 5) {
            	 final int shade = (int) (Math.log1p(pixeledAmplitudes[i] / refAmplitude) / Math.log1p(1.0000001) * 255);
            	 int finalColor = 255 - shade;
            	 if (finalColor < 0) finalColor = 0;
            	 if (!hasReadFile) { // if the program is reading the file it turns the color blue
            		 color = new Color(finalColor, finalColor, 255, 255); // turns 
         	         display.statsPanel.fileLines++;
            	 }
            	 else {
                	 display.statsPanel.addPlayerLine();
            		 color = new Color(255, finalColor, finalColor, 255); // makes your input red
            		 
            		  int clr=  bufferedImage.getRGB(position,i); // sets the color of the fft to green if your recording matches  
            		  int  red   = (clr & 0x00ff0000) >> 16;      // the file at that specific moment
            		  int  green = (clr & 0x0000ff00) >> 8;
            		  int  blue  =  clr & 0x000000ff;
            		  if (blue > red && blue > green) {
            			  color = new Color(finalColor, 255, finalColor); 
            			  display.statsPanel.addCorrectLine();
            		  }
            		  }
            	 }
             else if (hasReadFile) color = new Color(255, 255, 255, 0);
             
             
             bufferedGraphics.setColor(color);
        	 bufferedGraphics.fillRect(position, i, 3, 1);
        	 
         }
		
		
		bufferedGraphics.setColor(Color.WHITE);	
		bufferedGraphics.fillRect(0,0, 190,30);
        bufferedGraphics.setColor(Color.BLACK);
        
        for(int i = 100 ; i < 500; i += 100){ // this area creates the lines and frequencies at the beginning of the graph
        	int bin = frequencyToBin(i, minFrequency, maxFrequency);
			bufferedGraphics.drawLine(0, bin, 5, bin);
		}
		
        for(int i = 500 ; i <= 20000; i += 500){
			int bin = frequencyToBin(i, minFrequency, maxFrequency);
			bufferedGraphics.drawLine(0, bin, 5, bin);
		}
        
        for(int i = 100 ; i <= 20000; i*=10){
			int bin = frequencyToBin(i, minFrequency, maxFrequency);
			bufferedGraphics.drawString(String.valueOf(i), 10, bin);
		}
        
		repaint();
		position += 3;

   	 	display.ampPanel.drawAmplitude(maxAmplitude, hasReadFile);
        
		}
	}
	// Places image of current bufferedImage (graph) onto a backup to be loaded later
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
		// Code still runs when window is minimized
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// Allows for the program to sill run when the window gets moved around
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		/* bufferedImage = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		position = 0; */
		// resets the window when the window gets resized
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
