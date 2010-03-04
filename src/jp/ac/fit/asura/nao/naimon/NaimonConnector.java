/**
 * 
 */
package jp.ac.fit.asura.nao.naimon;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jp.ac.fit.asura.nao.naimon.event.NaimonEventListener;

/**
 * @author kilo
 * 
 */
public class NaimonConnector implements Runnable {
	private static final Logger log = Logger.getLogger(NaimonConnector.class
			.toString());
	private static final NaimonConfig conf = NaimonConfig.getInstance();

	private static final String NAIMON_PREFIX = "/naimon2";

	private String host;
	private int port;

	private EventListenerList listenerList;

	private Thread cThread;

	private boolean connected = false;

	private Hashtable<String, String> requestParams;

	public NaimonConnector() {
		listenerList = new EventListenerList();
		requestParams = new Hashtable<String, String>();
		cThread = new Thread(this);

		host = conf.get("naimon.connect.last.host", "localhost");
		port = conf.get("naimon.connect.last.port", 8080);

		cThread.start();
	}

	public void connect(String host, int port) {
		if (connected) {
			disconnect();
		}
		this.host = host;
		this.port = port;

		log.info("connect to " + host + ":" + port);
		connected = true;
		fireConnected();
	}

	public void disconnect() {
		log.info(host + ":" + port + " disconnected.");
		connected = false;
		fireDisconnected();
	}

	public synchronized void addUpdateListener(NaimonEventListener listener) {
		listenerList.add(NaimonEventListener.class, listener);
	}

	@Override
	public void run() {
		log.fine("thread start");

		while (true) {

			if (connected) {
				requestParams.clear();
				fireAddRequestParam(requestParams);

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
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
					String params = "";
					if (requestParams.size() > 0) {
						params += "?";
						for (Iterator<String> iterator = requestParams.keySet()
								.iterator(); iterator.hasNext();) {
							String key = iterator.next();
							params += key + "=" + requestParams.get(key);
							if (iterator.hasNext()) {
								params += "&";
							}
						}
					}
					log.fine("params:" + params);
					document = builder.parse("http://" + host + ":" + port
							+ NAIMON_PREFIX + params);
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

				fireUpdate(document);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void fireAddRequestParam(Hashtable<String, String> params) {
		for (NaimonEventListener listener : listenerList
				.getListeners(NaimonEventListener.class)) {
			listener.addRequestParam(params);
		}
	}

	private void fireUpdate(Document doc) {
		for (NaimonEventListener listener : listenerList
				.getListeners(NaimonEventListener.class)) {
			listener.update((Document) doc.cloneNode(true));
		}
	}

	private void fireConnected() {
		for (NaimonEventListener listener : listenerList
				.getListeners(NaimonEventListener.class)) {
			listener.connected(host, port);
		}
	}

	private void fireDisconnected() {
		for (NaimonEventListener listener : listenerList
				.getListeners(NaimonEventListener.class)) {
			listener.disconnected();
		}
	}

	public boolean sendScheme(String scheme) {
		String path = "/";
		String param = "eval=" + scheme;

		Socket socket = null;
		BufferedWriter writer = null;
		try {
			socket = new Socket(host, port);
			writer = new BufferedWriter(new OutputStreamWriter(socket
					.getOutputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			log.warning("接続先:" + host + ":" + port + "へのソケットを開けませんでした。");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("入出力エラーです。");
			return false;
		}
		try {
			writer.write("POST " + path + " HTTP/1.1\r\n");
			writer.write("Host: " + host + ":" + port + "\r\n");
			writer.write("Content-type: application/x-www-form-urlencoded\r\n");
			writer.write("Content-length: " + param.getBytes().length
					+ "\r\n\r\n");
			writer.write(param + "\r\n");
			writer.flush();

			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("Scheme送信中の入出力エラーです。");
			return false;
		}

		log.info("scheme:" + scheme);
		return true;
	}
}
