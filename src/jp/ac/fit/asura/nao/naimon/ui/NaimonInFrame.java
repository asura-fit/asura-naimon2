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

/**
 * @author kilo
 *
 */
public class NaimonInFrame extends JInternalFrame {

	public NaimonInFrame(int n) {
		this.setSize(new Dimension(200, 150));
		JLabel label = new JLabel("ì‡ïîÉtÉåÅ[ÉÄ" + n);
		label.setFont(new Font("Serif", Font.BOLD, 18));
		label.setForeground(new Color(new Random().nextInt()));
		this.add(label);
		this.setClosable(true);
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
	}
}
