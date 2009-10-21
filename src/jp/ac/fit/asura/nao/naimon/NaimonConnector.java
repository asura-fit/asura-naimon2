/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import jp.ac.fit.asura.nao.naimon.event.NaimonEventListener;

/**
 * @author kilo
 *
 */
public class NaimonConnector implements Runnable {
	private static final Logger log = Logger.getLogger(NaimonConnector.class.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();
	
	private static final String NAIMON_PREFIX = "/naimon2";
	
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
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				Document document = null;
				try {
					builder = dbf.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					disconnect();
					continue;
				}
				try {
					document = builder.parse("http://" + host + ":" + port + NAIMON_PREFIX);
				} catch (SAXException e) {
					e.printStackTrace();
					disconnect();
					continue;
				} catch (IllegalArgumentException e) {
					log.warning("接続できませんでした。接続先の指定がおかしいかもです。");
					disconnect();
					continue;
				} catch (SocketException e) {
					log.warning("接続できませんでした。接続先が正しくないかもしれません。");
					disconnect();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					log.warning("データを取得できませんでした。入出力エラーです。");
					disconnect();
					continue;
				}
				
				//System.out.println(".");
				fire(document);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void fire(Document doc) {
		for (NaimonEventListener listener : listenerList.getListeners(NaimonEventListener.class)) {
			listener.update((Document)doc.cloneNode(true));
		}
	}
}
