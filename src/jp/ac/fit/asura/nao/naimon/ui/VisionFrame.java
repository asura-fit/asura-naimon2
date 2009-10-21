package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

	private BufferedImage image;
	private ImagePanel imagePanel;
	
	public VisionFrame() {
		init();
		imagePanel = new ImagePanel();
		add(imagePanel);
	}
	
	private void init() {
		image = new BufferedImage(160, 120, BufferedImage.TYPE_INT_RGB);
		setPreferredSize(new Dimension());
		setTitle(this.getName() + " " + image.getWidth() + "x" + image.getHeight());
	}
	
	private void updateImage(int width, int height, byte[] plane) {
		if (image.getWidth() != width || image.getHeight() != height) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			setTitle(this.getName() + " " + image.getWidth() + "x" + image.getHeight());
		}
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		GCD.gcd2rgb(plane, pixels);
		imagePanel.repaint();
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
		byte[] p = new byte[length];
		try {
			iin.read(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		NodeList blobs = document.getElementsByTagName("Blobs");
		for (int i = 0; i < blobs.getLength(); i++) {
			NodeList blob = (NodeList)blobs.item(i);
			Element be = (Element)blob;
			System.out.println("index: " + be.getAttribute("colorIndex"));
			for (int j = 0; j < blob.getLength(); j++) {
				Element e = (Element)blob.item(j);
				System.out.println("xmax: " + e.getAttribute("xmax"));
			}
		}
		*/
		updateImage(width, height, p);
	}
	
	class ImagePanel extends JPanel {

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			int x = (getWidth() - image.getWidth()) / 2;
			int y = (getHeight() - image.getHeight()) / 2;
			g.drawImage(image, x, y, Color.BLACK, null);
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
