/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import jp.ac.fit.asura.nao.naimon.NaimonConnector;

/**
 * @author kilo
 *
 */
public class NaimonFrame extends JFrame {
	private JDesktopPane desktop;
	private LinkedHashSet<NaimonInFrame> frames;
	
	private NaimonConnector connector = null;
	
	public NaimonFrame() {
		connector = new NaimonConnector();
		
		frames = new LinkedHashSet<NaimonInFrame>();
		frames.clear();
		
		desktop = new JDesktopPane();
		this.add(desktop, BorderLayout.CENTER);
		
		// フレームを登録
		registerFrames();
		// setConnector
		setConnector();
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
	
	private void setConnector() {
		for (NaimonInFrame f : frames) {
			f.setConnector(connector);
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
				//connector.connect("192.168.1.1", "8080");
				showConnectDialog();
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
	
	private void showConnectDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		
		JPanel p = new JPanel();
		p.add(new JTextField("ip"));
		p.add(new JTextField("port"));
		panel.add(p);
		p = new JPanel();
		p.add(new JButton("接続"));
		p.add(new JButton("キャンセル"));
		panel.add(p);
		
		JDialog dialog = new JDialog(this, "新規接続", true);
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}
}
