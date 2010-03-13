/**
 *
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author sey
 *
 */
public class KinematicsFrame extends NaimonInFrame {
	public KinematicsFrame() {
		valuePanel = new ValuePanel();
		controlPanel = new ControlPanel();
		textArea = new JTextArea();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(valuePanel);
		cpane.add(controlPanel);
		cpane.add(textArea);

		setPreferredSize(layout.preferredLayoutSize(this.getContentPane()));
		setTitle("Kinematics");
		pack();
	}

	private ValuePanel valuePanel;

	private ControlPanel controlPanel;

	private JTextArea textArea;

	private boolean synchronizeMode;

	private boolean updateFlag;

	class ValuePanel extends JPanel {
		private JTable chainTable;

		private JTable frameTable;

		protected DefaultTableModel chainModel;

		protected DefaultTableModel frameModel;

		private List<String> chains;

		private List<String> frames;

		private NumberFormat format;

		public ValuePanel() {
			chains = new ArrayList<String>();
			chainModel = new DefaultTableModel(0, 6);
			String[] labels = { "Name", "x", "y", "z", "Pitch", "Yaw", "Roll" };
			chainModel.setColumnIdentifiers(labels);
			chainTable = new JTable(chainModel);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JScrollPane chainScroll = new JScrollPane(chainTable);
			add(chainScroll);

			frames = new ArrayList<String>();
			frameModel = new DefaultTableModel(0, 6);
			String[] labels2 = { "Name", "min θ", "θ[deg]", "max θ", "x", "y",
					"z", "Pitch", "Yaw", "Roll" };
			frameModel.setColumnIdentifiers(labels2);
			frameTable = new JTable(frameModel);
			JScrollPane scroll2 = new JScrollPane(frameTable);
			add(scroll2);

			setPreferredSize(new Dimension(640, 640));

			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
		}

		private String toFloat(String str) {
			try {
				float a = Float.parseFloat(str);
				return format.format(a);
			} catch (NumberFormatException e) {
				return "";
			}
		}

		private String toDegrees(String str) {
			try {
				float a = Float.parseFloat(str);
				return format.format(a * 180 / Math.PI);
			} catch (NumberFormatException e) {
				return "";
			}
		}

		public void setChain(Element fsElement) {
			Element posElement = (Element) fsElement.getElementsByTagName(
					"position").item(0);
			Element rotElement = (Element) fsElement.getElementsByTagName(
					"rotation").item(0);

			int i = chains.indexOf(fsElement.getAttribute("name"));
			if (i == -1) {
				Vector<Object> row = new Vector<Object>();
				row.add(fsElement.getAttribute("name"));
				row.add(toFloat(posElement.getAttribute("x")));
				row.add(toFloat(posElement.getAttribute("y")));
				row.add(toFloat(posElement.getAttribute("z")));
				row.add(toDegrees(rotElement.getAttribute("pitch")));
				row.add(toDegrees(rotElement.getAttribute("yaw")));
				row.add(toDegrees(rotElement.getAttribute("roll")));
				chainModel.addRow(row);
				chains.add(fsElement.getAttribute("name"));
			} else {
				int j = 1;
				chainModel.setValueAt(toFloat(posElement.getAttribute("x")), i,
						j++);
				chainModel.setValueAt(toFloat(posElement.getAttribute("y")), i,
						j++);
				chainModel.setValueAt(toFloat(posElement.getAttribute("z")), i,
						j++);
				chainModel.setValueAt(toDegrees(rotElement
						.getAttribute("pitch")), i, j++);
				chainModel.setValueAt(
						toDegrees(rotElement.getAttribute("yaw")), i, j++);
				chainModel.setValueAt(
						toDegrees(rotElement.getAttribute("roll")), i, j++);
			}
		}

		public void setFrame(Element fsElement) {
			Element posElement = (Element) fsElement.getElementsByTagName(
					"position").item(0);
			Element rotElement = (Element) fsElement.getElementsByTagName(
					"rotation").item(0);
			Element angleElement = (Element) fsElement.getElementsByTagName(
					"angle").item(0);

			int i = frames.indexOf(fsElement.getAttribute("name"));

			if (i == -1) {
				Vector<Object> row = new Vector<Object>();
				row.add(fsElement.getAttribute("name"));
				row.add(toDegrees(angleElement.getAttribute("min")));
				row.add(toDegrees(angleElement.getAttribute("value")));
				row.add(toDegrees(angleElement.getAttribute("max")));
				row.add(toFloat(posElement.getAttribute("x")));
				row.add(toFloat(posElement.getAttribute("y")));
				row.add(toFloat(posElement.getAttribute("z")));
				row.add(toDegrees(rotElement.getAttribute("pitch")));
				row.add(toDegrees(rotElement.getAttribute("yaw")));
				row.add(toDegrees(rotElement.getAttribute("roll")));
				frameModel.addRow(row);
				frames.add(fsElement.getAttribute("name"));
			} else {
				int j = 1;
				frameModel.setValueAt(toDegrees(angleElement
						.getAttribute("min")), i, j++);
				frameModel.setValueAt(toDegrees(angleElement
						.getAttribute("value")), i, j++);
				frameModel.setValueAt(toDegrees(angleElement
						.getAttribute("max")), i, j++);
				frameModel.setValueAt(toFloat(posElement.getAttribute("x")), i,
						j++);
				frameModel.setValueAt(toFloat(posElement.getAttribute("y")), i,
						j++);
				frameModel.setValueAt(toFloat(posElement.getAttribute("z")), i,
						j++);
				frameModel.setValueAt(toDegrees(rotElement
						.getAttribute("pitch")), i, j++);
				frameModel.setValueAt(
						toDegrees(rotElement.getAttribute("yaw")), i, j++);
				frameModel.setValueAt(
						toDegrees(rotElement.getAttribute("roll")), i, j++);
			}
		}
	}

	private Element calculateForward() {
		return null;
	}

	private Element calculateInverse(String chain, float x, float y, float z,
			float pitch, float yaw, float roll, float[] weights) {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docbuilder = null;
		Document document = null;
		try {
			docbuilder = dbfactory.newDocumentBuilder();
			document = docbuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		// ルートノードを作成
		Element root = document.createElement("Kinematics");
		document.appendChild(root);

		Element ikElement = document.createElement("ProcessIK");

		Element fsElement = document.createElement("FrameState");
		fsElement.setAttribute("name", chain);
		Element posElement = document.createElement("position");
		Element rotElement = document.createElement("rotation");

		posElement.setAttribute("x", Float.toString(x));
		posElement.setAttribute("y", Float.toString(y));
		posElement.setAttribute("z", Float.toString(z));
		rotElement.setAttribute("pitch", Float.toString(pitch));
		rotElement.setAttribute("yaw", Float.toString(yaw));
		rotElement.setAttribute("roll", Float.toString(roll));

		fsElement.appendChild(posElement);
		fsElement.appendChild(rotElement);
		ikElement.appendChild(fsElement);

		Element weightElement = document.createElement("WeightVector");
		weightElement.setAttribute("x", Float.toString(weights[0]));
		weightElement.setAttribute("y", Float.toString(weights[1]));
		weightElement.setAttribute("z", Float.toString(weights[2]));
		weightElement.setAttribute("pitch", Float.toString(weights[3]));
		weightElement.setAttribute("yaw", Float.toString(weights[4]));
		weightElement.setAttribute("roll", Float.toString(weights[5]));

		ikElement.appendChild(weightElement);

		root.appendChild(ikElement);

		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		StringWriter sw = new StringWriter();
		try {
			transformer = tfactory.newTransformer();
			transformer
					.transform(new DOMSource(document), new StreamResult(sw));
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		Document result = connector.sendXML("/kinematics", sw.toString());

		NodeList list = result.getElementsByTagName("ResultIK");
		if (list.getLength() == 0)
			return null;
		return (Element) list.item(0);
	}

	class ControlPanel extends JPanel {
		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);

			JButton forwardButton = new JButton("ForwardK");
			forwardButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// 現在位置を取得
					float[] angles = new float[valuePanel.frames.size()];
					int j = 0;
					for (String frame : valuePanel.frames) {
						DefaultTableModel model = valuePanel.frameModel;
						int i = valuePanel.frames.indexOf(frame);
						float angle = (float) Math.toRadians(Float
								.parseFloat(model.getValueAt(i, 2).toString()));
						angles[j++] = angle;
					}
					Element elements = calculateForward();
					NodeList nodes = elements
							.getElementsByTagName("FrameState");
					for (int i = 0; i < nodes.getLength(); i++) {
						Element fs = (Element) nodes.item(i);
						valuePanel.setFrame(fs);
					}
				}
			});
			add(forwardButton);

			final JTextField weightsField = new JTextField("0.5 0.5 0.5 1 1 1");
			JButton invButton = new JButton("InverseK");
			invButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// 取得した値を目標に逆運動学計算
					for (String chain : valuePanel.chains) {
						DefaultTableModel model = valuePanel.chainModel;
						int i = valuePanel.chains.indexOf(chain);
						int j = 1;
						float x = Float.parseFloat(model.getValueAt(i, j++)
								.toString());
						float y = Float.parseFloat(model.getValueAt(i, j++)
								.toString());
						float z = Float.parseFloat(model.getValueAt(i, j++)
								.toString());
						float pitch = (float) Math
								.toRadians(Float.parseFloat(model.getValueAt(i,
										j++).toString()));
						float yaw = (float) Math
								.toRadians(Float.parseFloat(model.getValueAt(i,
										j++).toString()));
						float roll = (float) Math
								.toRadians(Float.parseFloat(model.getValueAt(i,
										j++).toString()));

						String[] weights = weightsField.getText().split(
								"[ \t]+");
						float[] weightsVec = new float[6];
						for (int k = 0; k < 6; k++)
							try {
								weightsVec[k] = Float.valueOf(weights[k]);
							} catch (NumberFormatException e) {
								weightsVec[k] = 0;
								e.printStackTrace();
							}
						Element elements = calculateInverse(chain, x, y, z,
								pitch, yaw, roll, weightsVec);
						NodeList nodes = elements
								.getElementsByTagName("FrameState");
						for (int k = 0; k < nodes.getLength(); k++) {
							Element fs = (Element) nodes.item(k);
							valuePanel.setFrame(fs);
						}
						textArea.setText("Error! ");
					}
				}
			});
			add(invButton);

			JButton updateButton = new JButton("Update");
			updateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateFlag = true;
				}
			});
			add(updateButton);

			final JCheckBox syncButton = new JCheckBox("同期モード");
			syncButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					synchronizeMode = syncButton.isSelected();
				}
			});
			add(syncButton);
			add(weightsField);

			setMaximumSize(layout.preferredLayoutSize(this));
		}
	}

	@Override
	public void update(Document document) {
		NodeList node = null;
		NodeList items = null;
		Element element = null;

		node = document.getElementsByTagName("SomaticContext");
		element = (Element) node.item(0);
		items = element.getElementsByTagName("FrameState");
		if (!synchronizeMode && !updateFlag)
			return;
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			valuePanel.setFrame(item);

			// FIXME dirty hack
			String name = item.getAttribute("name");
			if (name.equals("HeadPitch") || name.equals("LElbowRoll")
					|| name.equals("LSole") || name.equals("RSole")
					|| name.equals("RElbowRoll"))
				valuePanel.setChain(item);
		}
		updateFlag = false;
	}

	@Override
	public String getName() {
		return "KinematicsFrame";
	}

	@Override
	public void connected(String host, int port) {
		updateFlag = true;
	}

}
