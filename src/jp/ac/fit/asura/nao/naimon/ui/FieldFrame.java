/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author kilo
 *
 */
public class FieldFrame extends NaimonInFrame {

	private BufferedImage fieldImage;

	private EnumMap<WorldObjects, WorldObject> objs;
	private SelfObject self;
	
	private FieldPanel fieldPanel;
	private ControlPanel controlPanel;
	
	private static final int WIDTH = Field.FULL_WIDTH;
	private static final int HEIGHT = Field.FULL_HEIGHT;
	
	public FieldFrame() {
		init(WIDTH, HEIGHT);
		fieldPanel = new FieldPanel();
		controlPanel = new ControlPanel();
		
		BoxLayout layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		setLayout(layout);
		add(fieldPanel);
		add(controlPanel);
		
		//setMinimumSize(layout.preferredLayoutSize(this.getContentPane()));
		pack();
	}
	
	private void init(int width, int height) {
		objs = new EnumMap<WorldObjects, WorldObject>(WorldObjects.class);
		objs.put(WorldObjects.Ball, new WorldObject());
		objs.put(WorldObjects.BlueGoal, new WorldObject());
		objs.put(WorldObjects.YellowGoal, new WorldObject());
		self = new SelfObject();
		
		fieldImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		setTitle(this.getName());
		
		synchronized (fieldImage) {
			Graphics g = fieldImage.createGraphics();
			drawFieldImage(g);
		}
	}
	
	private void drawFieldImage(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		int width = fieldImage.getWidth();
		int height = fieldImage.getHeight();
		
		g2.setStroke(new BasicStroke(2.0f));
		
		// 	Fieldの下地色
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, width, height);
		
		// ゴールの色
		g.setColor(Color.CYAN);
		g.fillRect(Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.GOAL_WIDTH / 2), Field.TOP_MARGIN - Field.GOAL_HEIGHT,
				Field.GOAL_WIDTH, Field.GOAL_HEIGHT);
		g.setColor(Color.YELLOW);
		g.fillRect(Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.GOAL_WIDTH / 2), Field.TOP_MARGIN + Field.HEIGHT, 
				Field.GOAL_WIDTH, Field.GOAL_HEIGHT);
		
		// Fieldラインの色
		g.setColor(Color.WHITE);
		// 上ライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN, 
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.TOP_MARGIN);
		// 下ライン
		g.drawLine(Field.LEFT_MARGIN, Field.FULL_HEIGHT - Field.BOTTOM_MARGIN,
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.FULL_HEIGHT - Field.BOTTOM_MARGIN);
		// 左ライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN,
				Field.LEFT_MARGIN, Field.FULL_HEIGHT - Field.BOTTOM_MARGIN);
		// 右ライン
		g.drawLine(Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.TOP_MARGIN,
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.FULL_HEIGHT - Field.BOTTOM_MARGIN);
		// センターライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN + (Field.HEIGHT / 2),
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.TOP_MARGIN + (Field.HEIGHT / 2));
		// ブルー側ペナルティライン
		g.drawRect(Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.PENALTY_WIDTH / 2), Field.TOP_MARGIN,
				Field.PENALTY_WIDTH, Field.PENALTY_HRIGHT);
		// イエロー側ペナルティライン
		g.drawRect(Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.PENALTY_WIDTH / 2), Field.FULL_HEIGHT - Field.BOTTOM_MARGIN - Field.PENALTY_HRIGHT,
				Field.PENALTY_WIDTH, Field.PENALTY_HRIGHT);
		// センターサークル
		g.drawArc(Field.LEFT_MARGIN + Field.WIDTH / 2 - Field.CIRCLE_RADIUS, 
				Field.TOP_MARGIN + Field.HEIGHT / 2 - Field.CIRCLE_RADIUS, 
				Field.CIRCLE_RADIUS * 2, Field.CIRCLE_RADIUS * 2, 0, 360);
		
		// 点
		g.fillArc(Field.LEFT_MARGIN + Field.WIDTH / 2 - Field.DOT_RADIUS / 2,
				Field.TOP_MARGIN + Field.DOT_MARGIN - Field.DOT_RADIUS,
				Field.DOT_RADIUS, Field.DOT_RADIUS, 0, 360);
		g.fillArc(Field.LEFT_MARGIN + Field.WIDTH / 2 - Field.DOT_RADIUS / 2,
				Field.TOP_MARGIN + Field.HEIGHT - Field.DOT_MARGIN - Field.DOT_RADIUS,
				Field.DOT_RADIUS, Field.DOT_RADIUS, 0, 360);
		
	}

	private void drawSelf(Graphics g, Color c, SelfObject so) {
		int x = (-so.x /10 - Field.LEFT_MARGIN + Field.WIDTH / 2);
		int y = (-so.y /10 - Field.LEFT_MARGIN + Field.HEIGHT / 2);

		g.setColor(c);
		g.fillArc(x - 25 / 2, y - 25 / 2, 25, 25, 0, 360);
		g.setColor(Color.red);
		g.drawLine(x, y, x - (int) (20 * Math.sin(so.yaw)), y
				- (int) (20 * Math.cos(so.yaw)));
	}
	
	private void drawObject(Graphics g, Color c, WorldObject wo) {
		if (wo.cf == 0) {
			return;
		}
		int x = (-wo.x / 10 + Field.LEFT_MARGIN + Field.WIDTH / 2);
		int y = (-wo.y / 10 + Field.TOP_MARGIN + Field.HEIGHT / 2);
		
		System.out.println( "x = " + x + ", y= " + y);
		
		g.setColor(c);
		g.fillArc(x - 15 / 2, y - 15 / 2, 15, 15, 0, 360);
		g.setColor(c.darker());
		g.drawArc(x - 15 / 2, y - 15 / 2, 15, 15, 0, 360);
	}
	
	@Override
	public String getName() {
		return "Field";
	}

	@Override
	public void update(Document document) {

		NodeList woNodeList = document.getElementsByTagName("WorldObject");
		for (int i = 0; i < woNodeList.getLength(); i++) {
			Element woElement = (Element) woNodeList.item(i);
			int type = Integer.parseInt(woElement.getAttribute("type"));
			int x = Integer.parseInt(woElement.getAttribute("worldX"));
			int y = Integer.parseInt(woElement.getAttribute("worldY"));
			int d = Integer.parseInt(woElement.getAttribute("distance"));
			float h = Float.parseFloat(woElement.getAttribute("heading"));
			int cf = Integer.parseInt(woElement.getAttribute("confidence"));
			float yaw = Float.parseFloat(woElement.getAttribute("yaw"));

			switch (WorldObjects.values()[type]) {
			case Ball:
			case BlueGoal:
			case YellowGoal:
				WorldObject wo = objs.get(WorldObjects.values()[type]);
				wo.x = x;
				wo.y = y;
				wo.cf = cf;
				if (cf > 0) {
					System.out.println(WorldObjects.values()[type] + " d:" + d
							+ " h:" + h);
				}
				break;
			case Self:
				self.x = x;
				self.y = y;
				self.yaw = (float) Math.toRadians(yaw);
				if (true)
					self.yaw = -self.yaw;
				self.cf = cf;
				break;
			default:
			}
		}
		
		synchronized (fieldImage) {
			Graphics g = fieldImage.createGraphics();
			// Fieldを描画
			drawFieldImage(g);
			drawObject(g, Color.YELLOW, objs.get(WorldObjects.YellowGoal));
			drawObject(g, Color.CYAN, objs.get(WorldObjects.BlueGoal));
			drawObject(g, Color.ORANGE, objs.get(WorldObjects.Ball));
			g.dispose();
		}
		
		fieldPanel.repaint();
	}

	class FieldPanel extends JPanel {

		public FieldPanel() {
			setMinimumSize(new Dimension(240, 320));
			setPreferredSize(new Dimension(320, 480));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			int drawWidth = fieldImage.getWidth();
			int drawHeight = fieldImage.getHeight();
			int x = (getWidth() - drawWidth) / 2;
			int y = (getHeight() - drawHeight) / 2;
			
			if (controlPanel.isAutoScale) {
				double n = (double)fieldImage.getWidth() / fieldImage.getHeight();
				drawHeight = (int)(getHeight() * 1.0); // 100%
				drawWidth = (int)(drawHeight * n);
				x = (getWidth() - drawWidth) / 2;
				y = (getHeight() - drawHeight) / 2;
			}
			
			synchronized (fieldImage) {
				g.drawImage(fieldImage, x, y, drawWidth, drawHeight, Color.BLACK, null);
			}
		}
	}
	
	class ControlPanel extends JPanel {
		
		protected boolean isAutoScale = true;
		
		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);
			
			JCheckBox autoScaleCheckBox = new JCheckBox("自動スケール");
			autoScaleCheckBox.setSelected(isAutoScale);
			autoScaleCheckBox.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					isAutoScale = !isAutoScale;
					fieldPanel.repaint();
				}
			});
			
			add(autoScaleCheckBox);
			
			setPreferredSize(layout.preferredLayoutSize(this));
		}
		
	}
	
	private static class WorldObject {
		int x;
		int y;
		int cf;
	}
	
	private static class SelfObject extends WorldObject {
		float yaw;
		float w;
	}
	
	public enum WorldObjects {
		Self, Ball, RedNao, BlueNao, YellowGoal, BlueGoal
	}
	
	private static class Field {
		private static final int WIDTH = 4050 /10;
		private static final int HEIGHT = 6050 /10;
		private static final int TOP_MARGIN = 675 /10;
		private static final int BOTTOM_MARGIN = 675 /10;
		private static final int LEFT_MARGIN = 675 /10;
		private static final int RIGHT_MARGIN = 675 /10;
		private static final int FULL_WIDTH = LEFT_MARGIN + WIDTH + RIGHT_MARGIN;
		private static final int FULL_HEIGHT = TOP_MARGIN + HEIGHT + BOTTOM_MARGIN;
		private static final int PENALTY_WIDTH = 3050 /10;
		private static final int PENALTY_HRIGHT = 650 /10;
		private static final int CIRCLE_RADIUS = 625 /10;
		private static final int GOAL_WIDTH = 1400 / 10;
		private static final int GOAL_HEIGHT = 500 / 10;
		private static final int DOT_MARGIN = 1825 / 10;
		private static final int DOT_RADIUS = 50 / 10;
	}
	
}