package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jp.ac.fit.asura.nao.naimon.event.SimpleTelnetEventListener;
import jp.ac.fit.asura.nao.naimon.net.SimpleTelnet;

import org.w3c.dom.Document;

public class LogFrame extends NaimonInFrame implements
		SimpleTelnetEventListener {

	private static final int TELNET_PORT = 59000;

	private LogPanel logPanel;
	private ControlPanel controlPanel;

	private SimpleTelnet telnet;

	public LogFrame() {
		logPanel = new LogPanel();
		controlPanel = new ControlPanel();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(logPanel);
		cpane.add(controlPanel);

		setTitle(getName());
		pack();
	}

	@Override
	public String getName() {
		return "Log";
	}

	@Override
	public void connected(String host, int port) {
		telnet = new SimpleTelnet(host, TELNET_PORT);
		if (!telnet.open()) {
			logPanel.appendString(host + ":" + TELNET_PORT + "に接続できませんでした");
		} else {
			telnet.addListener(this);
			telnet.startproc();
		}
	}

	@Override
	public void disconnected() {
		telnet.close();
	}

	@Override
	public void update(Document document) {
	}

	@Override
	public void received(String str) {
		logPanel.appendString(str);

	}

	class LogPanel extends JPanel {

		private JTextArea textArea;

		public LogPanel() {
			textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scroll = new JScrollPane(textArea);
			setLayout(new BorderLayout());
			add(scroll);
		}

		public void appendString(String str) {
			textArea.append(str);
			if (controlPanel.isAutoScroll) {
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}
		}

		private void clearTextArea() {
			textArea.setText("");
		}
	}

	class ControlPanel extends JPanel {

		private boolean isAutoScroll = true;

		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);
			JCheckBox autoScrollCheckBox = new JCheckBox("自動スクロール");
			autoScrollCheckBox.setSelected(isAutoScroll);
			autoScrollCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isAutoScroll = !isAutoScroll;
				}
			});

			JButton clearBtn = new JButton("クリア");
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logPanel.clearTextArea();
				}
			});

			add(autoScrollCheckBox);
			add(clearBtn);
			setMaximumSize(layout.preferredLayoutSize(this));
		}
	}

}
