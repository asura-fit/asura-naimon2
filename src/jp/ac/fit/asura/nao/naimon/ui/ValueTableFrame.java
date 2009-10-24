/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author kilo
 *
 */
public class ValueTableFrame extends NaimonInFrame {

	private VisutalObjectValuePanel voValuePanel;
	private ValuePanel valuePanel;
	
	public ValueTableFrame() {
		init();
		voValuePanel = new VisutalObjectValuePanel();
		valuePanel = new ValuePanel();
		
		BoxLayout layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		setLayout(layout);
		add(voValuePanel);
		add(valuePanel);
		
		setPreferredSize(layout.preferredLayoutSize(this.getContentPane()));
		pack();
	}
	
	private void init() {
		setTitle(this.getName());
	}
	
	@Override
	public String getName() {
		return "ValueTable";
	}

	@Override
	public void update(Document document) {
		DefaultTableModel voModel = voValuePanel.model;
		DefaultTableModel valueModel = valuePanel.model;
		NodeList node = null;
		NodeList items = null;
		Element element = null;
		
		node = document.getElementsByTagName("VisualObjects");
		element = (Element)node.item(0);
		items = element.getElementsByTagName("Item");
		voModel.setRowCount(items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element)items.item(i);
			Vector<String> row = new Vector<String>();
			row.add(item.getAttribute("name"));
			row.add(item.getAttribute("CenterX"));
			row.add(item.getAttribute("CenterY"));
			row.add(item.getAttribute("AngleX"));
			row.add(item.getAttribute("AngleY"));
			row.add(item.getAttribute("RobotAngleX"));
			row.add(item.getAttribute("RobotAngleY"));
			row.add(item.getAttribute("Confidence"));
			row.add(item.getAttribute("Distance"));
			
			for (int j = 0; j < row.size(); j++) {
				voModel.setValueAt(row.get(j), i, j);
			}
		}
		
		node = document.getElementsByTagName("Values");
		element = (Element)node.item(0);
		items = element.getElementsByTagName("Item");
		valueModel.setRowCount(items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element)items.item(i);
			Vector<String> row = new Vector<String>();
			row.add(item.getAttribute("name"));
			row.add(item.getAttribute("value"));
			
			for (int j = 0; j < row.size(); j++) {
				valueModel.setValueAt(row.get(j), i, j);
			}
		}

	}
	
	class VisutalObjectValuePanel extends JPanel {
		
		private JTable table;
		private JScrollPane scroll;
		protected DefaultTableModel model;
		
		public VisutalObjectValuePanel() {
			model = new DefaultTableModel(0, 9);
			String[] labels = {
					"Name", "CenterX", "CenterY", "AngleX", "AngleY",
					"RobotAngleX", "RobotAngleY", "Confidence", "Distance"
			};
			model.setColumnIdentifiers(labels);
			table = new JTable(model);
			scroll = new JScrollPane(table);
			this.setLayout(new BorderLayout());
			add(scroll, BorderLayout.CENTER);
			
			setPreferredSize(new Dimension(480, 120));
		}
	}
	
	class ValuePanel extends JPanel {
		
		private JTable table;
		private JScrollPane scroll;
		protected DefaultTableModel model;
		
		public ValuePanel() {
			model = new DefaultTableModel(0, 2);
			String[] labels = {
					"Name", "Value"
			};
			model.setColumnIdentifiers(labels);
			table = new JTable(model);
			scroll = new JScrollPane(table);
			this.setLayout(new BorderLayout());
			add(scroll, BorderLayout.CENTER);
			
			setPreferredSize(new Dimension(480, 240));
		}
	}

}
