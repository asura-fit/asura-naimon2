package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class VisionFrame extends NaimonInFrame {

	private BufferedImage gcdImage = null;
	private BufferedImage blobImage = null;
	
	private ImagePanel imagePanel;
	
	public VisionFrame() {
		init(160, 120);
		imagePanel = new ImagePanel();
		add(imagePanel);
	}
	
	private void init(int width, int height) {
		gcdImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		blobImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		setTitle(this.getName() + " " + width + "x" + height);
	}
	
	@Override
	public String getName() {
		return "Vision";
	}

	@Override
	public void update(Document document) {
		NodeList gcdNode = document.getElementsByTagName("GCD");
		Element gcd = (Element)gcdNode.item(0);
		int width = Integer.parseInt(gcd.getAttribute("width"));
		int height = Integer.parseInt(gcd.getAttribute("height"));
		int length = Integer.parseInt(gcd.getAttribute("length"));
		String gdata = gcd.getTextContent();
		
		// Base64をでコード後、展開する
		ByteArrayInputStream bin = new ByteArrayInputStream(Base64.decode(gdata));
		InflaterInputStream iin = new InflaterInputStream(bin);
		byte[] gcdPlane = new byte[length];
		
		try {
			int count = 0;
			while (true) {
				int ret = iin.read(gcdPlane, count, gcdPlane.length - count);
				if (ret <= 0 || ret == gcdPlane.length) {
					break;
				} else {
					count += ret;
				}
			}
			iin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 使用するBufferedImageを初期化する
		init(width, height);
		int[] pixels = ((DataBufferInt) gcdImage.getRaster().getDataBuffer()).getData();
		// gcdPlaneをgcdImageに反映
		GCD.gcd2rgb(gcdPlane, pixels);
		
		
		synchronized (blobImage) {
			Graphics2D g = blobImage.createGraphics();
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);
			
			NodeList blobs = document.getElementsByTagName("Blobs");
			for (int i = 0; i < blobs.getLength(); i++) {
				NodeList blob = (NodeList)blobs.item(i);
				Element be = (Element)blob;
				int index = Integer.parseInt(be.getAttribute("colorIndex"));
				g.setColor(getColorWithIndex(index));
				for (int j = 0; j < blob.getLength(); j++) {
					Element e = (Element)blob.item(j);
					int x =  Integer.parseInt(e.getAttribute("xmin"));
					int y =  Integer.parseInt(e.getAttribute("ymin"));
					int x2 = Integer.parseInt(e.getAttribute("xmax"));
					int y2 = Integer.parseInt(e.getAttribute("ymax"));
					g.drawRect(x, y, x2 - x, y2 - y);
				}
			}
			
		}
		
		
		
		
		// パネルを再描画
		imagePanel.repaint();
	}
	
	private Color getColorWithIndex(int index) {
		Color color = null;
		switch (index) {
		case 0:
		case 1:
		case 3:
			color = new Color(255, 0, 255, 255);
			break;
		default:
			color = new Color(255, 0, 255, 255);
		}
		return color;
	}
	
	class ImagePanel extends JPanel {

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			double n = (double)gcdImage.getHeight() / gcdImage.getWidth();
			int idrawWidth = (int)(getWidth() * 0.7); // 70%
			int idrawHeight = (int)(idrawWidth * n);
			int x = (getWidth() - idrawWidth) / 2;
			int y = (getHeight() - idrawHeight) / 2;
			g.drawImage(gcdImage, x, y, idrawWidth, idrawHeight, Color.BLACK, null);
			synchronized (blobImage) {
				g.drawImage(blobImage, x, y, idrawWidth, idrawHeight, null);
			}
		}
		
	}
	
	static class GCD {
		
		public static final byte cORANGE = 0;
		public static final byte cCYAN = 1;
		public static final byte cGREEN = 2;
		public static final byte cYELLOW = 3;
		// public static final byte cPINK = 4;
		public static final byte cBLUE = 5;
		public static final byte cRED = 6;
		public static final byte cWHITE = 7;
		// public static final byte cFGREEN = 7;
		public static final byte cBLACK = 9;
		public static final int COLOR_NUM = 10;
		
		public static void gcd2rgb(byte[] gcdPlane, int[] rgbPlane) {
			for (int i = 0; i < gcdPlane.length; i++) {
				switch (gcdPlane[i]) {
				case cORANGE:
					rgbPlane[i] = Color.ORANGE.getRGB();
					break;
				case cCYAN:
					rgbPlane[i] = Color.CYAN.getRGB();
					break;
				case cBLUE:
					rgbPlane[i] = Color.BLUE.getRGB();
					break;
				case cGREEN:
					rgbPlane[i] = Color.GREEN.getRGB();
					break;
				case cRED:
					rgbPlane[i] = Color.RED.getRGB();
					break;
				case cWHITE:
					rgbPlane[i] = Color.WHITE.getRGB();
					break;
				case cYELLOW:
					rgbPlane[i] = Color.YELLOW.getRGB();
					break;
				case cBLACK:
					rgbPlane[i] = Color.BLACK.getRGB();
					break;
				default:
					rgbPlane[i] = Color.GRAY.getRGB();
				}
			}
		}
		
	}

}
