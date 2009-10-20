/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jp.ac.fit.asura.nao.naimon.ui.NaimonFrame;

/**
 * @author kilo
 *
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		log.info("Naimon started.");
	}
	
	private static void init() {
		// Look and Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		
		// create naimon frame
		final NaimonFrame f = new NaimonFrame();
		f.setSize(conf.get("naimon.window.width", 640), conf.get("naimon.window.height", 480));
		f.setLocation(conf.get("naimon.window.x", 0), conf.get("naimon.window.y", 0));
		
		f.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				if (!isMaximized(f.getExtendedState())) {
					conf.set("naimon.window.width", f.getWidth());
					conf.set("naimon.window.height", f.getHeight());
				}
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				if (!isMaximized(f.getExtendedState())) {
					conf.set("naimon.window.x", f.getX());
					conf.set("naimon.window.y", f.getY());
				}
			}
		});
		
		f.setVisible(true);
	}
	
	private static boolean isMaximized(int state) {
		return (state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
	}
}
