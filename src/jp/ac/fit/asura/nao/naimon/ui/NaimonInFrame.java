/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Random;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import jp.ac.fit.asura.nao.naimon.event.NaimonEventListener;

/**
 * @author kilo
 *
 */
public abstract class NaimonInFrame extends JInternalFrame implements NaimonEventListener {

	public NaimonInFrame() {
		this.setSize(new Dimension(200, 150));
		this.setClosable(true);
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
	}
	
	abstract public String getName();
}
