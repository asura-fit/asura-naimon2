package jp.ac.fit.asura.nao.naimon.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.event.EventListenerList;

import jp.ac.fit.asura.nao.naimon.event.SimpleTelnetEventListener;

public class SimpleTelnet {

	private static final byte IAC = (byte) 255;
	private static final byte DONT = (byte) 254;
	private static final byte DO = (byte) 253;
	private static final byte WONT = (byte) 252;
	private static final byte WILL = (byte) 251;

	private String host;
	private int port;

	private Socket socket;
	private BufferedInputStream input;
	private OutputStream out;

	Thread thread;
	boolean running = true;

	private EventListenerList listenerList = new EventListenerList();

	public SimpleTelnet(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public boolean open() {
		try {
			socket = new Socket(host, port);
			out = socket.getOutputStream();
			input = new BufferedInputStream(socket.getInputStream());
			negotiation();
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
		return true;
	}

	private void negotiation() {
		byte[] buff = new byte[3];
		while (true) {
			try {
				input.mark(buff.length);
				if (input.available() >= buff.length) {
					input.read(buff);
					if (buff[0] != IAC) {
						input.reset();
						return;
					} else if (buff[1] == DO) {
						buff[1] = WONT;
						out.write(buff);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startproc() {
		StreamConnector connector = new StreamConnector(input);
		thread = new Thread(connector);
		thread.start();
	}

	public void close() {
		if (thread != null) {
			if (thread.isAlive()) {
				running = false;
			}
		}
	}

	public void addListener(SimpleTelnetEventListener l) {
		listenerList.add(SimpleTelnetEventListener.class, l);
	}

	private void received(String str) {
		for (SimpleTelnetEventListener listener : listenerList
				.getListeners(SimpleTelnetEventListener.class)) {
			listener.received(str);
		}
	}

	class StreamConnector implements Runnable {

		private InputStream in = null;

		public StreamConnector(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			byte[] buff = new byte[1024];
			while (running) {
				try {
					int n = in.read(buff);
					if (n > 0) {
						String str = new String(buff, 0, n);
						received(str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}