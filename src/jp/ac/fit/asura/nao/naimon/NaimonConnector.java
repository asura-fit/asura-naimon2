/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import jp.ac.fit.asura.nao.naimon.event.NaimonEventListener;

/**
 * @author kilo
 *
 */
public class NaimonConnector implements Runnable {
	private static final Logger log = Logger.getLogger(NaimonConnector.class.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();
	
	private String host;
	private String port;
	
	private EventListenerList listenerList;
	
	private Thread cThread;
	
	public NaimonConnector() {
		listenerList = new EventListenerList();
		cThread = new Thread(this);
		
		host = conf.get("naimon.connect.last.host", "localhost");
		port = conf.get("naimon.connect.last.port", "8080");
		
	}
	
	public void connect(String host, String port) {
		this.host = host;
		this.port = port;
		conf.set("naimon.connect.last.host", host);
		conf.set("naimon.connect.last.port", port);
		
		if (!cThread.isAlive())
			cThread.start();
	}
	
	public void disconnect() {
		
	}
	
	public synchronized void addUpdateListener(NaimonEventListener listener) {
		listenerList.add(NaimonEventListener.class, listener);
	}

	@Override
	public void run() {
		
		while(true) {
			System.out.println("connect to " + host + ":" + port);
			fire();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void fire() {
		for (NaimonEventListener listener : listenerList.getListeners(NaimonEventListener.class)) {
			listener.update();
		}
	}
}
