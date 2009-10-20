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
	
	private boolean connected = false;
	
	public NaimonConnector() {
		listenerList = new EventListenerList();
		cThread = new Thread(this);
		
		host = conf.get("naimon.connect.last.host", "localhost");
		port = conf.get("naimon.connect.last.port", "8080");
		
		cThread.start();
	}
	
	public void connect(String host, String port) {
		if (connected) {
			disconnect();
		}
		this.host = host;
		this.port = port;
		
		log.info("connect to " + host + ":" + port);
		connected = true;
	}
	
	public void disconnect() {
		log.info(host + ":" + port + " disconnected.");
		connected = false;
	}
	
	public synchronized void addUpdateListener(NaimonEventListener listener) {
		listenerList.add(NaimonEventListener.class, listener);
	}

	@Override
	public void run() {
		log.fine("thread start");
		
		while(true) {
			
			if (connected) {
				System.out.print(".");
				fire();
			}
			
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
