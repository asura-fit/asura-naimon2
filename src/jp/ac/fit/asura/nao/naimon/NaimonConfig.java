/**
 *
 */
package jp.ac.fit.asura.nao.naimon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author kilo
 * 
 */
final public class NaimonConfig {
	private static final Logger log = Logger.getLogger(NaimonConfig.class
			.toString());
	private static final NaimonConfig instance = new NaimonConfig();

	private static final String CONF_FILE_NAME = ".naimon.conf";
	private static final String CONF_FILE_COMMENT = "naimon config file";
	private static String CONF_FILE_PATH;

	private Properties conf;

	private NaimonConfig() {
		CONF_FILE_PATH = System.getProperty("user.home") + File.separator
				+ CONF_FILE_NAME;
		loadConfigFile();
	}

	private void loadConfigFile() {
		this.conf = new Properties();
		try {
			this.conf.load(new FileInputStream(CONF_FILE_PATH));
		} catch (FileNotFoundException e) {
			log.config("'" + CONF_FILE_PATH
					+ "' not found. create default config file.");
			setDefault();
			save();
		} catch (IOException e) {
			setDefault();
		}
	}

	public void save() {
		try {
			FileOutputStream out = new FileOutputStream(CONF_FILE_PATH);
			this.conf.store(out, CONF_FILE_COMMENT);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.config("Saved config file.");
	}

	private void setDefault() {
		this.conf.setProperty("naimon.window.x", "0");
		this.conf.setProperty("naimon.window.y", "0");
		this.conf.setProperty("naimon.window.width", "800");
		this.conf.setProperty("naimon.window.height", "600");

		this.conf
				.setProperty(
						"naimon.connect.hosts",
						"localhost;192.168.1.51;192.168.1.52;192.168.1.53;192.168.1.54;192.168.1.61;192.168.1.62;192.168.1.63;192.168.1.64;");
		this.conf.setProperty("naimon.connect.ports", "8080");
		this.conf.setProperty("connect.last.host", "localhost");
		this.conf.setProperty("connect.last.port", "8080");
		this.conf.setProperty("connect.autoreconnect.maxtries", "5");
		this.conf.setProperty("connect.autoreconnect.interval", "10");

		this.conf.setProperty("naimon.window.backimage", getClass()
				.getResource(
						"/jp/ac/fit/asura/nao/naimon/resource/naimon_background.png"
								+ "").toString());

		this.conf.setProperty("naimon.frame.Vision.x", "326");
		this.conf.setProperty("naimon.frame.Vision.y", "0");
		this.conf.setProperty("naimon.frame.Vision.width", "466");
		this.conf.setProperty("naimon.frame.Vision.height", "195");

		this.conf.setProperty("naimon.frame.Field.x", "0");
		this.conf.setProperty("naimon.frame.Field.y", "0");
		this.conf.setProperty("naimon.frame.Field.width", "326");
		this.conf.setProperty("naimon.frame.Field.height", "550");

		this.conf.setProperty("naimon.frame.ValueTable.x", "326");
		this.conf.setProperty("naimon.frame.ValueTable.y", "195");
		this.conf.setProperty("naimon.frame.ValueTable.width", "466");
		this.conf.setProperty("naimon.frame.ValueTable.height", "225");

		this.conf.setProperty("naimon.frame.Scheme.x", "326");
		this.conf.setProperty("naimon.frame.Scheme.y", "420");
		this.conf.setProperty("naimon.frame.Scheme.width", "466");
		this.conf.setProperty("naimon.frame.Scheme.height", "130");

		this.conf.setProperty("naimon.frame.Log.x", "0");
		this.conf.setProperty("naimon.frame.Log.y", "350");
		this.conf.setProperty("naimon.frame.Log.width", "780");
		this.conf.setProperty("naimon.frame.Log.height", "190");
	}

	public boolean get(String key, boolean defaultValue) {
		String value = this.conf.getProperty(key);
		if (value != null) {
			return Boolean.parseBoolean(value);
		}
		log.config("key:" + key + " use default value.");
		this.conf.setProperty(key, String.valueOf(defaultValue));
		save();
		return defaultValue;
	}

	public int get(String key, int defaultValue) {
		String value = this.conf.getProperty(key);
		if (value != null) {
			return Integer.parseInt(value);
		}
		log.config("key:" + key + " use default value.");
		this.conf.setProperty(key, String.valueOf(defaultValue));
		save();
		return defaultValue;
	}

	public String get(String key, String defaultValue) {
		String value = this.conf.getProperty(key);
		if (value != null) {
			return value;
		}
		log.config("key:" + key + " use default value.");
		this.conf.setProperty(key, defaultValue);
		save();
		return defaultValue;
	}

	public void set(String key, boolean value) {
		this.conf.setProperty(key, String.valueOf(value));
		save();
	}

	public void set(String key, int value) {
		this.conf.setProperty(key, String.valueOf(value));
		save();
	}

	public void set(String key, String value) {
		this.conf.setProperty(key, value);
		save();
	}

	public static NaimonConfig getInstance() {
		return NaimonConfig.instance;
	}
}
