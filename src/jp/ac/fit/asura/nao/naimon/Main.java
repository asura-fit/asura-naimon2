/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import java.util.logging.Logger;

import jp.ac.fit.asura.nao.naimon.ui.NaimonFrame;

/**
 * @author kilo
 *
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class.toString());
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("Naimon 2");
		NaimonConfig conf = NaimonConfig.getInstance();
		new NaimonFrame().setVisible(true);
	}

}
