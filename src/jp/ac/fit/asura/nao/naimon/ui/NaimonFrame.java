/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

/**
 * @author kilo
 *
 */
public class NaimonFrame extends JFrame {
	private JDesktopPane desktop;
	private LinkedHashSet<NaimonInFrame> frames;
	
	
	public NaimonFrame() {
		frames = new LinkedHashSet<NaimonInFrame>();
		frames.clear();
		
		this.setSize(new Dimension(400, 300));
		desktop = new JDesktopPane();
		this.add(desktop, BorderLayout.CENTER);
		
		// フレームを登録
		registerFrames();
		
		// メニュー項目を作成
		createMenuBar();
		
		// フレーム表示
		showFrames();
	}
	
	/**
	 * 
	 */
	private void registerFrames() {
		frames.add(new VisionFrame());
		frames.add(new TestFrame());
		
		for (NaimonInFrame f : frames) {
			desktop.add(f);
			f.setVisible(true);
			desktop.getDesktopManager().activateFrame(f);
		}
	}
	
	/**
	 * 
	 */
	private void showFrames() {
		
	}
	
	/**
	 * 
	 */
	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("ファイル");
		JMenuItem newitem = new JMenuItem("新規");
		newitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createInFrame();
			}
		});
		JMenuItem quititem = new JMenuItem("終了");
		quititem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);	
			}
		});
		menu1.add(newitem);
		menu1.add(quititem);
		menubar.add(menu1);
		
		// frame menu
		JMenu frameMenu = new JMenu("表示");
		for (final NaimonInFrame f : frames) {
			JMenuItem item = new JMenuItem(f.getName());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (f.isIcon()) {
						try {
							f.setIcon(false);
						} catch (PropertyVetoException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							f.setIcon(true);
						} catch (PropertyVetoException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			frameMenu.add(item);
		}
		menubar.add(frameMenu);
		
		this.setJMenuBar(menubar);
	}
	
	public void performeFrame(String name) {
		
	}
	
	public void createInFrame() {
		/*
		NaimonInFrame frame = new NaimonInFrame();
		desktop.add(frame);
		frame.setVisible(true);
		// フォーカスをあわせる
		desktop.getDesktopManager().activateFrame(frame);
		*/
	}
}
