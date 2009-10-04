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

/**
 * @author kilo
 *
 */
final class NaimonConfig {
	private static final NaimonConfig instance = new NaimonConfig();
	
	private static final String CONF_FILE_NAME = "naimon.conf";
	private static final String CONF_FILE_COMMENT = "naimon config file";
	
	private Properties conf;
	
	private NaimonConfig(){
		loadConfigFile();
	}
	
	private void loadConfigFile() {
		this.conf = new Properties();
		try {
			this.conf.load(new FileInputStream(CONF_FILE_NAME));
		} catch (FileNotFoundException e) {
			setDefauts();
			save();
		} catch (IOException e) {
			setDefauts();
		}
	}
	
	public void save() {
		try {
			this.conf.store(new FileOutputStream(CONF_FILE_NAME), CONF_FILE_COMMENT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefauts() {
		this.conf.setProperty("window.width", "320");
		this.conf.setProperty("window.height", "240");
		this.conf.setProperty("connect.last.host", "localhost");
		this.conf.setProperty("connect.last.port", "8080");
	}
	
	public int get(String key, int defaultValue) {
		String value = this.conf.getProperty(key);
		if (value != null) {
			return Integer.parseInt(value);
		}
		return defaultValue;
	}
	
	public String get(String key, String defaultValue) {
		String value = this.conf.getProperty(key);
		if (value != null) {
			return value;
		}
		return defaultValue;
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
