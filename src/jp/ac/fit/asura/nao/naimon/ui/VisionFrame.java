package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class VisionFrame extends NaimonInFrame {

	// images
	private BufferedImage gcdImage = null;
	private BufferedImage blobImage = null;
	private BufferedImage houghImage = null;

	// Panels
	private ImagePanel imagePanel;
	private ControlPanel controlPanel;

	public VisionFrame() {
		init(160, 120);
		imagePanel = new ImagePanel();
		controlPanel = new ControlPanel();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(imagePanel);
		cpane.add(controlPanel);

		setMinimumSize(layout.preferredLayoutSize(this.getContentPane()));
		pack();
	}

	private void init(int width, int height) {
		gcdImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		blobImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		houghImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		setTitle(this.getName() + " " + width + "x" + height);
	}

	@Override
	public String getName() {
		return "Vision";
	}

	@Override
	public void update(Document document) {

		NodeList gcdNode = document.getElementsByTagName("GCD");
		Element gcd = (Element) gcdNode.item(0);
		int width = Integer.parseInt(gcd.getAttribute("width"));
		int height = Integer.parseInt(gcd.getAttribute("height"));
		int length = Integer.parseInt(gcd.getAttribute("length"));
		String gdata = gcd.getTextContent();
		// 使用するBufferedImageを初期化する
		init(width, height);

		// Base64をでコード後、展開する
		ByteArrayInputStream bin = new ByteArrayInputStream(Base64
				.decode(gdata));
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

		int[] pixels = ((DataBufferInt) gcdImage.getRaster().getDataBuffer())
				.getData();
		// gcdPlaneをgcdImageに反映
		GCD.gcd2rgb(gcdPlane, pixels);

		synchronized (blobImage) {
			Graphics2D g = blobImage.createGraphics();
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);

			NodeList blobs = document.getElementsByTagName("Blobs");
			for (int i = 0; i < blobs.getLength(); i++) {
				NodeList blob = (NodeList) blobs.item(i);
				Element be = (Element) blob;
				int index = Integer.parseInt(be.getAttribute("colorIndex"));
				g.setColor(getColorWithIndex(index));
				for (int j = 0; j < blob.getLength(); j++) {
					Element e = (Element) blob.item(j);
					int x = Integer.parseInt(e.getAttribute("xmin"));
					int y = Integer.parseInt(e.getAttribute("ymin"));
					int x2 = Integer.parseInt(e.getAttribute("xmax"));
					int y2 = Integer.parseInt(e.getAttribute("ymax"));
					g.drawRect(x, y, x2 - x, y2 - y);
				}
			}
			g.dispose();
		}

		Element hough = (Element) document.getElementsByTagName("Hough")
				.item(0);
		if (hough != null) {
			// Houghの表示・非表示を切り替えるためのボタンを有効化
			controlPanel.houghOnCheckBox.setEnabled(true);

			int hough_width = Integer.parseInt(hough.getAttribute("width"));
			int hough_height = Integer.parseInt(hough.getAttribute("height"));
			int hough_length = Integer.parseInt(hough.getAttribute("length"));
			String hdata = hough.getTextContent();

			// Base64をでコード後、展開する
			ByteArrayInputStream bin2 = new ByteArrayInputStream(Base64
					.decode(hdata));
			InflaterInputStream iin2 = new InflaterInputStream(bin2);
			byte[] houghPlane = new byte[hough_length];
			try {
				int count = 0;
				while (true) {
					int ret = iin2.read(houghPlane, count, houghPlane.length
							- count);
					if (ret <= 0 || ret == houghPlane.length) {
						break;
					} else {
						count += ret;
					}
				}
				iin2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			drawHough(houghPlane, hough_width, hough_height);
		} else {
			// Houghノードはないのでボタンを無効にする
			controlPanel.houghOnCheckBox.setEnabled(false);
		}

		// パネルを再描画
		imagePanel.repaint();
	}

	private void drawHough(byte[] houghPlane, int hough_width, int hough_height) {
		synchronized (houghImage) {
			Graphics2D g = houghImage.createGraphics();
			g.setColor(Color.GRAY.darker());
			int w = 360 / hough_height;
			for (int y = 0; y < hough_height; y++) {
				for (int x = 0; x < hough_width; x++) {
					if ((houghPlane[y * hough_width + x] & 0xFF) > 128) {
						double th = Math.toRadians(y * w);
						if (y * w % 90 == 0)
							continue;
						int rho = x * 2;
						int x0 = 0, y0, x1, y1 = 0;
						y0 = (int) (rho / Math.sin(th));
						x1 = (int) (rho / Math.cos(th));
						g.drawLine(x0, y0, x1, y1);
					}
				}
			}
			g.dispose();
		}
	}

	private Color getColorWithIndex(int index) {
		Color color = null;
		switch (index) {
		case 0:
		case 1:
		case 3:
			color = new Color(255, 0, 255, 255);
			break;
		case 7:
			color = new Color(255, 255, 255, 255);
			break;
		default:
			color = new Color(255, 0, 255, 255);
		}
		return color;
	}

	class ImagePanel extends JPanel {

		public ImagePanel() {
			setMinimumSize(new Dimension(160, 120));
			setPreferredSize(new Dimension(160, 120));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());

			int drawWidth = gcdImage.getWidth();
			int drawHeight = gcdImage.getHeight();
			int x = (getWidth() - drawWidth) / 2;
			int y = (getHeight() - drawHeight) / 2;
			if (controlPanel.isAutoScale) {
				if (getWidth() > getHeight()) {
					double n = (double) gcdImage.getWidth()
							/ gcdImage.getHeight();
					drawHeight = (int) (getHeight() * 0.9); // 90%
					drawWidth = (int) (drawHeight * n);
					x = (getWidth() - drawWidth) / 2;
					y = (getHeight() - drawHeight) / 2;
				} else {
					double n = (double) gcdImage.getHeight()
							/ gcdImage.getWidth();
					drawWidth = (int) (getWidth() * 0.9); // 90%
					drawHeight = (int) (drawWidth * n);
					x = (getWidth() - drawWidth) / 2;
					y = (getHeight() - drawHeight) / 2;
				}
			}

			// CGDImageを描画
			g.drawImage(gcdImage, x, y, drawWidth, drawHeight, Color.BLACK,
					null);
			// Houghを描画
			if (controlPanel.isHoughOn) {
				synchronized (houghImage) {
					g.drawImage(houghImage, x, y, drawWidth, drawHeight, null);
				}
			}
			// BlobImageを描画
			if (controlPanel.isBlobOn) {
				synchronized (blobImage) {
					g.drawImage(blobImage, x, y, drawWidth, drawHeight, null);
				}
			}
		}

	}

	class ControlPanel extends JPanel {

		protected boolean isAutoScale = true;
		protected boolean isBlobOn = true;
		protected boolean isHoughOn = true;

		protected JCheckBox houghOnCheckBox;

		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);

			JCheckBox blobOnCheckBox = new JCheckBox("Blob表示");
			blobOnCheckBox.setSelected(isBlobOn);
			blobOnCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isBlobOn = !isBlobOn;
				}
			});
			houghOnCheckBox = new JCheckBox("Hough表示");
			houghOnCheckBox.setSelected(isHoughOn);
			houghOnCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isHoughOn = !isHoughOn;
				}
			});
			JCheckBox autoScaleCheckBox = new JCheckBox("自動スケール");
			autoScaleCheckBox.setSelected(isAutoScale);
			autoScaleCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isAutoScale = !isAutoScale;
					imagePanel.repaint();
				}
			});

			add(blobOnCheckBox);
			add(houghOnCheckBox);
			add(autoScaleCheckBox);

			setMaximumSize(layout.preferredLayoutSize(this));
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
