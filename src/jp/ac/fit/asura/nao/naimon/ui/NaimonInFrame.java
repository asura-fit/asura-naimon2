/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Dimension;

import javax.swing.JInternalFrame;

import jp.ac.fit.asura.nao.naimon.NaimonConnector;
import jp.ac.fit.asura.nao.naimon.event.NaimonEventListener;

/**
 * @author kilo
 * 
 */
public abstract class NaimonInFrame extends JInternalFrame implements
		NaimonEventListener {

	protected NaimonConnector connector;

	public NaimonInFrame() {
		this.setSize(new Dimension(320, 240));
		this.setClosable(false);
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		// デフォルトのCLOSEオペレーション
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		// 例外を転送しない
		this.setRootPaneCheckingEnabled(false);
	}

	abstract public String getName();

	public void setConnector(NaimonConnector connector) {
		this.connector = connector;
		connector.addUpdateListener(this);
	}

	public void connected(String host, int port) {
	}

	public void disconnected() {
	}
}
