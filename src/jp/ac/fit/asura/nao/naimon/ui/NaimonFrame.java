/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author kilo
 *
 */
public class NaimonFrame extends JFrame {
	private JDesktopPane desktop;
	private int counter;
	
	public NaimonFrame() {
		counter = 0;
		this.setSize(new Dimension(400, 300));
		desktop = new JDesktopPane();
		this.add(desktop, BorderLayout.CENTER);
		this.createMenuBar();
	}
	
	/**
	 * 
	 */
	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("File");
		JMenuItem newitem = new JMenuItem("New");
		newitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createInFrame();
			}
		});
		JMenuItem quititem = new JMenuItem("Quit");
		quititem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);	
			}
		});
		menu1.add(newitem);
		menu1.add(quititem);
		menubar.add(menu1);
		this.setJMenuBar(menubar);
	}
	
	public void createInFrame() {
		NaimonInFrame frame = new NaimonInFrame(++counter);
		desktop.add(frame);
		frame.setVisible(true);
		// 追加したフレームをアクティブにする
		desktop.getDesktopManager().activateFrame(frame);
	}
}
