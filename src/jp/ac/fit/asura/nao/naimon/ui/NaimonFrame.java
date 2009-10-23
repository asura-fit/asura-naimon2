/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import jp.ac.fit.asura.nao.naimon.NaimonConfig;
import jp.ac.fit.asura.nao.naimon.NaimonConnector;

/**
 * @author kilo
 *
 */
public class NaimonFrame extends JFrame {
	private static final Logger log = Logger.getLogger(NaimonConnector.class.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();
	
	private JDesktopPane desktop;
	private LinkedHashSet<NaimonInFrame> frames;
	
	private NaimonConnector connector = null;
	
	public NaimonFrame() {
		setTitle("Naimon");
		
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
		//showFrames();
	}
	
	/**
	 * 
	 */
	private void registerFrames() {
		frames.add(new FieldFrame());
		frames.add(new VisionFrame());
		//frames.add(new TestFrame());
		
		for (NaimonInFrame f : frames) {
			desktop.add(f);
			f.setVisible(true);
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
		for (NaimonInFrame f : frames) {
			
		}
	}
	
	/**
	 * 
	 */
	private void createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("ファイル");
		JMenuItem newItem = new JMenuItem("新しい接続");
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showConnectDialog();
			}
		});
		JMenuItem closeItem = new JMenuItem("接続を閉じる");
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.disconnect();	
			}
		});
		JMenuItem quitItem = new JMenuItem("終了");
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);	
			}
		});
		
		fileMenu.add(newItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		
		// frame menu
		JMenu windowMenu = new JMenu("ウィンドウ");
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
			windowMenu.add(item);
		}
		
		menubar.add(fileMenu);
		menubar.add(windowMenu);
		
		this.setJMenuBar(menubar);
	}
	
	private void showConnectDialog() {
		final JDialog dialog = new JDialog(this, "新規接続", true);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		
		String str;
		str = conf.get("naimon.connect.hosts", "localhost");
		str = conf.get("naimon.connect.last.host", "localhost") + ";" + str;
		String[] hosts = str.split(";");
		str = conf.get("naimon.connect.ports", "8080");
		str = conf.get("naimon.connect.last.port", "8080") + ";" + str;
		String[] ports = str.split(";");
		
		JPanel p = new JPanel();
		
		final JComboBox hostCombo = new JComboBox(hosts);
		final JComboBox portCombo = new JComboBox(ports);
		hostCombo.setEditable(true);
		portCombo.setEditable(true);
		
		p.add(new JLabel("ホスト"));
		p.add(hostCombo);
		p.add(new JLabel("ポート"));
		p.add(portCombo);
		panel.add(p);
		
		p = new JPanel();
		JButton btn = new JButton("接続");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String host = (String)hostCombo.getSelectedItem();
				String port = (String)portCombo.getSelectedItem();
				if (!Pattern.compile("^[0-9a-z]+(.[0-9a-z]+)*").matcher(host).matches() ||
						!Pattern.compile("^[0-9]+").matcher(port).matches()) {
					log.warning("Invalid hostname : " + host + ", port : " + port);
				} else {
				// 
				conf.set("naimon.connect.last.host", host);
				conf.set("naimon.connect.last.port", port);
				connector.connect(host, port);
				}
				dialog.dispose();
			}
		});
		p.add(btn);
		btn = new JButton("キャンセル");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();	
			}
		});
		p.add(btn);
		panel.add(p);
		
		dialog.add(panel);
		
		dialog.pack();
		dialog.setVisible(true);
	}
}
