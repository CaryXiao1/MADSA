import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MusicPitchAnalysis {
	/**
	 * This class was created by Cary Xiao, started on October 25, 2018
	 * This is the Main method where everything is placed inside to create the program.
	 * 
     */
	public static void main(final String... strings) throws InterruptedException,
	InvocationTargetException {
SwingUtilities.invokeAndWait(new Runnable() {
	@Override
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// ignore failure to set default look and feel;
		}
		
		JFrame frame = new Display(); // creating the frame object with the window name "Music Pitch Analysis"
		
		frame.pack(); // removes unused space on the sides of the window
		frame.setSize(1920, 1080);
		frame.setVisible(true);
	}
});
}
}
