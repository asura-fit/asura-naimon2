/**
 *
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jp.ac.fit.asura.nao.naimon.NaimonConfig;
import jp.ac.fit.asura.nao.naimon.NaimonConnector;

/**
 * @author kilo
 *
 */
public class NaimonFrame extends JFrame {
	private static final Logger log = Logger.getLogger(NaimonFrame.class
			.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();

	private MyDesktopPane desktop;
	private LinkedHashSet<NaimonInFrame> frames;

	private NaimonConnector connector = null;

	public NaimonFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Naimon");
		setIconImage(new ImageIcon(getClass().getResource(
				"/jp/ac/fit/asura/nao/naimon/resource/naimon_icon.png"))
				.getImage());

		connector = new NaimonConnector();
		frames = new LinkedHashSet<NaimonInFrame>();
		frames.clear();
		desktop = new MyDesktopPane();
		this.add(desktop, BorderLayout.CENTER);

		// フレームを登録
		registerFrames();
		// setConnector
		setConnector();
		// メニュー項目を作成
		createMenuBar();
	}

	/**
	 * InFrameを登録して初期化します
	 */
	private void registerFrames() {
		frames.add(new VisionFrame());
		frames.add(new FieldFrame());
		frames.add(new LogFrame());
		frames.add(new ValueTableFrame());
		frames.add(new SchemeFrame());

		for (NaimonInFrame f : frames) {
			initInFrame(f);
		}
	}

	private void initInFrame(final NaimonInFrame f) {

		desktop.add(f);

		f.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				if (!f.isMaximum()) {
					conf.set("naimon.frame." + f.getName() + ".width", f
							.getWidth());
					conf.set("naimon.frame." + f.getName() + ".height", f
							.getHeight());
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				if (!f.isMaximum()) {
					conf.set("naimon.frame." + f.getName() + ".x", f.getX());
					conf.set("naimon.frame." + f.getName() + ".y", f.getY());
				}
			}
		});

		// 位置を復元
		int fw = conf.get("naimon.frame." + f.getName() + ".width", f
				.getWidth());
		int fh = conf.get("naimon.frame." + f.getName() + ".height", f
				.getHeight());
		int fx = conf.get("naimon.frame." + f.getName() + ".x", f.getX());
		int fy = conf.get("naimon.frame." + f.getName() + ".y", f.getY());
		f.setSize(fw, fh);
		f.setLocation(fx, fy);

		// 表示
		f.setVisible(true);
	}

	/**
	 * Connectorオブジェクトをセットします
	 */
	private void setConnector() {
		for (NaimonInFrame f : frames) {
			f.setConnector(connector);
		}
	}

	/**
	 * メニューバーを構成します
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
							if (!f.isSelected()) {
								desktop.getDesktopManager().activateFrame(f);
							} else {
								f.setIcon(true);
							}
						} catch (PropertyVetoException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			windowMenu.add(item);
		}
		windowMenu.addSeparator();
		JMenuItem item = new JMenuItem("すべて最小化");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (NaimonInFrame f : frames) {
					try {
						f.setIcon(true);
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		windowMenu.add(item);
		item = new JMenuItem("すべて元に戻す");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (NaimonInFrame f : frames) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		windowMenu.add(item);

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
				String host = (String) hostCombo.getSelectedItem();
				String port = (String) portCombo.getSelectedItem();
				if (!Pattern.compile("^[0-9a-z]+(.[0-9a-z]+)*").matcher(host)
						.matches()
						|| !Pattern.compile("^[0-9]+").matcher(port).matches()) {
					log.warning("Invalid hostname : " + host + ", port : "
							+ port);
					JOptionPane.showMessageDialog(null, "接続先:" + host
							+ " または、ポート:" + port + " が正しくありません", "エラー",
							JOptionPane.ERROR_MESSAGE);
				} else {
					//
					conf.set("naimon.connect.last.host", host);
					conf.set("naimon.connect.last.port", port);
					int p = Integer.parseInt(port);
					connector.connect(host, p);
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
		int x = this.getWidth() / 2 - dialog.getWidth() / 2;
		int y = this.getHeight() / 2 - dialog.getHeight() / 2;
		dialog.setLocation(this.getX() + x, this.getY() + y);
		dialog.setVisible(true);
	}

	class MyDesktopPane extends JDesktopPane {

		private BufferedImage image;

		public MyDesktopPane() {
			setBackground(Color.BLACK);

			String imagesrc = conf.get("naimon.window.backimage", "");
			if (imagesrc.equals("")) {
				imagesrc = getClass()
						.getResource(
								"/jp/ac/fit/asura/nao/naimon/resource/naimon_background.png")
						.toString();
			}

			try {
				image = ImageIO.read(new URL(imagesrc));
			} catch (IOException e) {
				e.printStackTrace();
				image = null;
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image == null)
				return;
			int x = getWidth() / 2 - image.getWidth() / 2;
			int y = getHeight() / 2 - image.getHeight() / 2;
			g.drawImage(image, x, y, null);
		}
	}
}
