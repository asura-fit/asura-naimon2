/**
 *
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.BitSet;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author kilo
 *
 */
public class ValueTableFrame extends NaimonInFrame {

	private VisualObjectValuePanel voValuePanel;
	private WorldObjectValuePanel woValuePanel;
	private ValuePanel valuePanel;

	public ValueTableFrame() {
		init();
		voValuePanel = new VisualObjectValuePanel();
		woValuePanel = new WorldObjectValuePanel();
		valuePanel = new ValuePanel();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(voValuePanel);
		cpane.add(woValuePanel);
		cpane.add(valuePanel);

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
		DefaultTableModel woModel = woValuePanel.model;
		DefaultTableModel valueModel = valuePanel.model;
		NodeList node = null;
		NodeList items = null;
		Element element = null;

		voValuePanel.renderer.clear();
		node = document.getElementsByTagName("VisualObjects");
		element = (Element) node.item(0);
		items = element.getElementsByTagName("Item");
		voModel.setRowCount(items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
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

			if (Integer.parseInt(item.getAttribute("Confidence")) == 0)
				voValuePanel.renderer.highlightRow(i);

			for (int j = 0; j < row.size(); j++) {
				voModel.setValueAt(row.get(j), i, j);
			}
		}

		woValuePanel.renderer.clear();
		node = document.getElementsByTagName("WorldObjects");
		element = (Element) node.item(0);
		items = element.getElementsByTagName("Item");
		woModel.setRowCount(items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			Vector<String> row = new Vector<String>();
			row.add(item.getAttribute("name"));
			row.add(item.getAttribute("X"));
			row.add(item.getAttribute("Y"));
			row.add(item.getAttribute("Heading"));
			row.add(item.getAttribute("Yaw"));
			row.add(item.getAttribute("Confidence"));
			row.add(item.getAttribute("Distance"));

			if (Integer.parseInt(item.getAttribute("Confidence")) == 0)
				woValuePanel.renderer.highlightRow(i);

			for (int j = 0; j < row.size(); j++) {
				woModel.setValueAt(row.get(j), i, j);
			}
		}

		node = document.getElementsByTagName("Values");
		element = (Element) node.item(0);
		items = element.getElementsByTagName("Item");
		valueModel.setRowCount(items.getLength());
		for (int i = 0; i < items.getLength(); i++) {
			Element item = (Element) items.item(i);
			Vector<String> row = new Vector<String>();
			row.add(item.getAttribute("name"));
			row.add(item.getAttribute("value"));

			for (int j = 0; j < row.size(); j++) {
				valueModel.setValueAt(row.get(j), i, j);
			}
		}

	}

	class VisualObjectValuePanel extends JPanel {
		private JTable table;
		private JScrollPane scroll;
		protected DefaultTableModel model;
		protected HighlightRowTableRenderer renderer;

		public VisualObjectValuePanel() {
			model = new DefaultTableModel(0, 9);
			String[] labels = { "Name", "CenterX", "CenterY", "AngleX",
					"AngleY", "RobotAngleX", "RobotAngleY", "Confidence",
					"Distance" };
			model.setColumnIdentifiers(labels);
			table = new JTable(model);
			renderer = new HighlightRowTableRenderer();
			table.setDefaultRenderer(Object.class, renderer);
			scroll = new JScrollPane(table);
			this.setLayout(new BorderLayout());
			add(scroll, BorderLayout.CENTER);

			setPreferredSize(new Dimension(480, 120));
		}
	}

	class WorldObjectValuePanel extends JPanel {
		private JTable table;
		private JScrollPane scroll;
		protected DefaultTableModel model;
		protected HighlightRowTableRenderer renderer;

		public WorldObjectValuePanel() {
			model = new DefaultTableModel(0, 9);
			String[] labels = { "Name", "X", "Y", "Heading", "Yaw",
					"Confidence", "Distance" };
			model.setColumnIdentifiers(labels);
			table = new JTable(model);
			renderer = new HighlightRowTableRenderer();
			table.setDefaultRenderer(Object.class, renderer);
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
			String[] labels = { "Name", "Value" };
			model.setColumnIdentifiers(labels);
			table = new JTable(model);
			scroll = new JScrollPane(table);
			this.setLayout(new BorderLayout());
			add(scroll, BorderLayout.CENTER);

			setPreferredSize(new Dimension(480, 240));
		}
	}

	class HighlightRowTableRenderer extends DefaultTableCellRenderer {
		private final Color highlightedColor = new Color(220, 220, 220);
		private BitSet highlightedRows;
		private boolean defaultHighlighted;

		public HighlightRowTableRenderer() {
			highlightedRows = new BitSet();
			defaultHighlighted = false;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			boolean isHighlighted = defaultHighlighted;
			if (highlightedRows.length() > row) {
				isHighlighted = highlightedRows.get(row);
				if (defaultHighlighted)
					isHighlighted = !isHighlighted;
			}
			if (isHighlighted) {
				setForeground(table.getForeground());
				setBackground(highlightedColor);
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			setHorizontalAlignment((value instanceof Number) ? RIGHT : LEFT);
			return this;
		}

		public void highlightRow(int row) {
			highlightedRows.set(row);
		}

		public void clear() {
			highlightedRows.clear();
		}
	}
}
