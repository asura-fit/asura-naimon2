package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import jp.ac.fit.asura.nao.naimon.ui.VisionFrame.ImagePanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CameraImageFrame extends NaimonInFrame {
	private BufferedImage cameraImage = null;
	private ImagePanel imagePanel;
	private ControlPanel controlPanel;

	public CameraImageFrame() {
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
		setTitle(this.getName() + " " + width + "x" + height);
		cameraImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
	}

	public void addRequestParam(Hashtable<String, String> params) {
		if (controlPanel.isCapture || controlPanel.previewCapture) {
			params.put("camera_image", "true");
		}
	}

	public String getName() {
		return "CameraImage";
	}

	public void update(Document document) {
		Element camImage = (Element) document.getElementsByTagName("CamImage")
				.item(0);
		if (camImage != null) {
			int width = Integer.parseInt(camImage.getAttribute("width"));
			int height = Integer.parseInt(camImage.getAttribute("height"));
			int frame = Integer.parseInt(camImage.getAttribute("frame"));
			int length = Integer.parseInt(camImage.getAttribute("length"));
			String data = camImage.getTextContent();
			// bufferdimage を初期化
			if (cameraImage == null || cameraImage.getWidth() != width
					|| cameraImage.getHeight() != height)
				init(width, height);
			// base64 展開
			byte[] decoded = new byte[length];
			VisionFrame.inflateWithBase64(data, decoded);
			int[] pixels = ((DataBufferInt) cameraImage.getRaster()
					.getDataBuffer()).getData();
			if (controlPanel.rgbPreview)
				yvu2rgb(decoded, pixels);
			else
				yvu2yvu(decoded, pixels);

			// パネルを再描画
			imagePanel.repaint();
			if (controlPanel.isCapture) {
				try {
					File dir = new File("snapshot");
					if (!dir.exists()) {
						dir.mkdir();
					}
					OutputStream os = new FileOutputStream("snapshot/image"
							+ frame + ".ppm");
					os.write("P6\n".getBytes());
					os.write((width + " " + height + "\n").getBytes());
					os.write("255\n".getBytes());
					os.write(decoded, 0, length);
					os.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				controlPanel.isCapture = false;
				controlPanel.capButton.setEnabled(true);
			}
		}
	}

	public static void yvu2rgb(byte[] yvuPlane, int[] rgbPlane) {
		assert rgbPlane.length * 3 == yvuPlane.length;

		for (int i = 0; i < rgbPlane.length; i++) {
			int y = yvuPlane[i * 3 + 0];
			int v = yvuPlane[i * 3 + 1];
			int u = yvuPlane[i * 3 + 2];
			// @see /utilities-aibo/tile/tile.cc
			// なぜかuとvの計算が逆になってる? 謎
			u = u * 2 - 255;
			v = v * 2 - 255;
			// FIXME なんか色合いが変ぽい. 誰か直して
			// int r = clipping((int) (y + v), 0, 255);
			// int g = clipping((int) (y - 0.51f * v - 0.19f * u), 0, 255);
			// int b = clipping((int) (y + u), 0, 255);
			int r = (int) (y + v);
			int g = (int) (y - 0.51f * v - 0.19f * u);
			int b = (int) (y + u);

			rgbPlane[i] = ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
					| ((b & 0xFF) << 0);
		}
	}

	public static void yvu2yvu(byte[] yvuPlane, int[] yvuPlane2) {
		assert yvuPlane2.length * 3 == yvuPlane.length;

		for (int i = 0; i < yvuPlane2.length; i++) {
			byte y = yvuPlane[i * 3 + 0];
			byte v = yvuPlane[i * 3 + 1];
			byte u = yvuPlane[i * 3 + 2];
			yvuPlane2[i] = ((y & 0xFF) << 16) | ((v & 0xFF) << 8)
					| ((u & 0xFF) << 0);
		}
	}

	private static int clipping(int param, int min, int max) {
		if (param > max)
			return max;
		if (param < min)
			return min;
		return param;
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
			if (cameraImage == null)
				return;
			int drawWidth = cameraImage.getWidth();
			int drawHeight = cameraImage.getHeight();
			int x = (getWidth() - drawWidth) / 2;
			int y = (getHeight() - drawHeight) / 2;
			if (controlPanel.isAutoScale) {
				if (getWidth() > getHeight()) {
					double n = (double) cameraImage.getWidth()
							/ cameraImage.getHeight();
					drawHeight = (int) (getHeight() * 0.9); // 90%
					drawWidth = (int) (drawHeight * n);
					x = (getWidth() - drawWidth) / 2;
					y = (getHeight() - drawHeight) / 2;
				} else {
					double n = (double) cameraImage.getHeight()
							/ cameraImage.getWidth();
					drawWidth = (int) (getWidth() * 0.9); // 90%
					drawHeight = (int) (drawWidth * n);
					x = (getWidth() - drawWidth) / 2;
					y = (getHeight() - drawHeight) / 2;
				}
			}

			// cameraImageを描画
			g.drawImage(cameraImage, x, y, drawWidth, drawHeight, Color.BLACK,
					null);
		}

	}

	class ControlPanel extends JPanel {
		protected boolean isAutoScale = true;
		private boolean isCapture = false;
		private boolean previewCapture = false;
		private boolean rgbPreview = true;
		private JButton capButton;

		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);
			capButton = new JButton("Capture");
			capButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					isCapture = true;
					capButton.setEnabled(false);
				}
			});

			JCheckBox previewCheckBox = new JCheckBox("Preview");
			previewCheckBox.setSelected(previewCapture);
			previewCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					previewCapture = !previewCapture;
				}
			});
			JCheckBox rgbCheckBox = new JCheckBox("RGB");
			rgbCheckBox.setSelected(rgbPreview);
			rgbCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					rgbPreview = !rgbPreview;
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

			add(capButton);
			add(previewCheckBox);
			add(rgbCheckBox);
			add(autoScaleCheckBox);
			setMaximumSize(layout.preferredLayoutSize(this));
		}
	}

}
