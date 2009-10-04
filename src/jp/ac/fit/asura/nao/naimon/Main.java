/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import jp.ac.fit.asura.nao.naimon.ui.NaimonFrame;

/**
 * @author kilo
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NaimonConfig conf = NaimonConfig.getInstance();
		new NaimonFrame().setVisible(true);
	}

}
