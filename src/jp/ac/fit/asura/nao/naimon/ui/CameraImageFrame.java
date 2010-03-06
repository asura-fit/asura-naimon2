package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CameraImageFrame extends NaimonInFrame {

	private ControlPanel controlPanel;

	public CameraImageFrame() {
		init(160, 120);
		controlPanel = new ControlPanel();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(controlPanel);

		setMinimumSize(layout.preferredLayoutSize(this.getContentPane()));
		pack();
	}

	private void init(int width, int height) {
		setTitle(this.getName() + " " + width + "x" + height);
	}

	public void addRequestParam(Hashtable<String, String> params) {
		if (controlPanel.isCapture) {
			controlPanel.isCapture = false;
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
			init(width, height);
			// base64 展開
			byte[] decoded = new byte[length];
			VisionFrame.inflateWithBase64(data, decoded);

			try {
				File dir = new File("snapshot");
				if (!dir.exists()) {
					dir.mkdir();
				}
				OutputStream os = new FileOutputStream("snapshot/image" + frame
						+ ".ppm");
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

			controlPanel.capButton.setEnabled(true);
		}

	}

	class ControlPanel extends JPanel {

		private boolean isCapture = false;

		private JButton capButton;

		public ControlPanel() {
			capButton = new JButton("Capture");
			capButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					isCapture = true;
					capButton.setEnabled(false);
				}
			});

			add(capButton);
		}
	}

}
