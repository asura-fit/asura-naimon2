/**
 *
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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
	private List<SelfObject> candidates;

	private FieldPanel fieldPanel;
	private ControlPanel controlPanel;

	private static final int WIDTH = Field.FULL_WIDTH;
	private static final int HEIGHT = Field.FULL_HEIGHT;

	public static final int WOBJ_SIZE = 10;
	public static final int WOBJ_CF_SIZE = 35;
	public static final float WOBJ_CF_ALPHA = 0.5f;
	public static final int OWN_SIZE = 25;
	public static final double OWN_SHAPE_RATIO = 0.6d;

	public static Shape createOwnShape() {
		int[] ownXPoints = new int[] { -(int) (OWN_SIZE * (1d / 3d)),
				(int) (OWN_SIZE * (2d / 3d)), -(int) (OWN_SIZE * (1d / 3d)), };
		int[] ownYPoints = new int[] {
				-(int) (OWN_SIZE * (1d / 2d) * OWN_SHAPE_RATIO), 0,
				(int) (OWN_SIZE * (1d / 2d) * OWN_SHAPE_RATIO), };
		return new Polygon(ownXPoints, ownYPoints, ownXPoints.length);
	}

	public static final Color STRING_COLOR = Color.WHITE;
	public static final Color BACKGROUND_COLOR = Color.GRAY;
	public static final Color RED_TEAM_COLOR = Color.RED;
	public static final Color RED_OWN_COLOR = RED_TEAM_COLOR.brighter();
	public static final Color BLUE_TEAM_COLOR = Color.BLUE;
	public static final Color BLUE_OWN_COLOR = BLUE_TEAM_COLOR.brighter();
	public static final Color CANDIDATES_COLOR = Color.LIGHT_GRAY;
	public static final Color BALL_COLOR = Color.ORANGE;
	public static final Color RED_GOAL_COLOR = Color.YELLOW;
	public static final Color BLUE_GOAL_COLOR = Color.CYAN;
	public static final Color FIELD_COLOR = Color.GREEN.darker();
	public static final Color LINE_COLOR = Color.WHITE;

	public FieldFrame() {
		init(WIDTH, HEIGHT);
		fieldPanel = new FieldPanel();
		controlPanel = new ControlPanel();

		Container cpane = this.getContentPane();
		BoxLayout layout = new BoxLayout(cpane, BoxLayout.Y_AXIS);
		cpane.setLayout(layout);
		cpane.add(fieldPanel);
		cpane.add(controlPanel);

		// setMinimumSize(layout.preferredLayoutSize(this.getContentPane()));
		pack();
	}

	private void init(int width, int height) {
		objs = new EnumMap<WorldObjects, WorldObject>(WorldObjects.class);
		objs.put(WorldObjects.Ball, new WorldObject(WorldObjects.Ball));
		objs.put(WorldObjects.BlueGoal, new WorldObject(WorldObjects.BlueGoal));
		objs.put(WorldObjects.YellowGoal, new WorldObject(WorldObjects.YellowGoal));
		objs.put(WorldObjects.RedNao, new WorldObject(WorldObjects.RedNao));
		objs.put(WorldObjects.BlueNao, new WorldObject(WorldObjects.BlueNao));
		self = new SelfObject();
		candidates = new ArrayList<SelfObject>();

		fieldImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		setTitle(this.getName());

		synchronized (fieldImage) {
			Graphics g = fieldImage.createGraphics();
			drawFieldImage(g);
		}
	}

	private void drawFieldImage(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = fieldImage.getWidth();
		int height = fieldImage.getHeight();

		BasicStroke defaultStroke = (BasicStroke) g2.getStroke();
		g2.setStroke(new BasicStroke(5.0f));

		// Fieldの下地色
		g.setColor(FIELD_COLOR);
		g.fillRect(0, 0, width, height);

		// ゴールの色
		g.setColor(BLUE_GOAL_COLOR);
		g.drawRect(
				Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.GOAL_WIDTH / 2),
				Field.TOP_MARGIN - Field.GOAL_HEIGHT, Field.GOAL_WIDTH,
				Field.GOAL_HEIGHT);
		g.setColor(RED_GOAL_COLOR);
		g.drawRect(
				Field.LEFT_MARGIN + (Field.WIDTH / 2 - Field.GOAL_WIDTH / 2),
				Field.TOP_MARGIN + Field.HEIGHT, Field.GOAL_WIDTH,
				Field.GOAL_HEIGHT);

		// Fieldラインの色
		g.setColor(LINE_COLOR);
		// 上ライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN, Field.FULL_WIDTH
				- Field.RIGHT_MARGIN, Field.TOP_MARGIN);
		// 下ライン
		g.drawLine(Field.LEFT_MARGIN, Field.FULL_HEIGHT - Field.BOTTOM_MARGIN,
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.FULL_HEIGHT
						- Field.BOTTOM_MARGIN);
		// 左ライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN, Field.LEFT_MARGIN,
				Field.FULL_HEIGHT - Field.BOTTOM_MARGIN);
		// 右ライン
		g.drawLine(Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.TOP_MARGIN,
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.FULL_HEIGHT
						- Field.BOTTOM_MARGIN);
		// センターライン
		g.drawLine(Field.LEFT_MARGIN, Field.TOP_MARGIN + (Field.HEIGHT / 2),
				Field.FULL_WIDTH - Field.RIGHT_MARGIN, Field.TOP_MARGIN
						+ (Field.HEIGHT / 2));
		// ブルー側ペナルティライン
		g.drawRect(Field.LEFT_MARGIN
				+ (Field.WIDTH / 2 - Field.PENALTY_WIDTH / 2),
				Field.TOP_MARGIN, Field.PENALTY_WIDTH, Field.PENALTY_HRIGHT);
		// イエロー側ペナルティライン
		g.drawRect(Field.LEFT_MARGIN
				+ (Field.WIDTH / 2 - Field.PENALTY_WIDTH / 2),
				Field.FULL_HEIGHT - Field.BOTTOM_MARGIN - Field.PENALTY_HRIGHT,
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
				Field.TOP_MARGIN + Field.HEIGHT - Field.DOT_MARGIN
						- Field.DOT_RADIUS, Field.DOT_RADIUS, Field.DOT_RADIUS,
				0, 360);

		// Strokeを変更前に戻す
		g2.setStroke(defaultStroke);
	}

	private void drawSelf(Graphics g, SelfObject so) {
		int x = (-so.x / 10 + Field.LEFT_MARGIN + Field.WIDTH / 2);
		int y = (-so.y / 10 + Field.LEFT_MARGIN + Field.HEIGHT / 2);

		Shape shape = createOwnShape();
		Graphics2D g2 = (Graphics2D) g;

		AffineTransform trans_tmp = g2.getTransform();
		AffineTransform transform = g2.getTransform();

		transform.translate(x, y);
		transform.rotate(-Math.PI / 2 - so.yaw);
		g2.setTransform(transform);
		g2.setColor(RED_OWN_COLOR);
		g2.fill(shape);
		g2.setTransform(trans_tmp);
		g.setColor(STRING_COLOR);
		g.drawString(so.type.name(), x + OWN_SIZE / 2, y + OWN_SIZE / 2);
	}

	private void drawCandidate(Graphics g, SelfObject so) {
		int x = (-so.x / 10 + Field.LEFT_MARGIN + Field.WIDTH / 2);
		int y = (-so.y / 10 + Field.LEFT_MARGIN + Field.HEIGHT / 2);

		Shape shape = createOwnShape();
		Graphics2D g2 = (Graphics2D) g;

		AffineTransform trans_tmp = g2.getTransform();
		AffineTransform transform = g2.getTransform();

		transform.translate(x, y);
		transform.rotate(-Math.PI / 2 - so.yaw);
		g2.setTransform(transform);
		g2.setColor(CANDIDATES_COLOR);
		g2.fill(shape);
		g2.setTransform(trans_tmp);
	}

	private void drawObject(Graphics g, Color c, WorldObject wo, SelfObject so) {
		if (wo.cf == 0) {
			return;
		}
		int x = (-wo.x / 10 + Field.LEFT_MARGIN + Field.WIDTH / 2);
		int y = (-wo.y / 10 + Field.TOP_MARGIN + Field.HEIGHT / 2);

		int sx = (-so.x / 10 + Field.LEFT_MARGIN + Field.WIDTH / 2);
		int sy = (-so.y / 10 + Field.TOP_MARGIN + Field.HEIGHT / 2);

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c.brighter());
		Composite comp_temp = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				WOBJ_CF_ALPHA));
		g2.fillArc(x - WOBJ_CF_SIZE / 2, y - WOBJ_CF_SIZE / 2, WOBJ_CF_SIZE,
				WOBJ_CF_SIZE, 0, (int) ((wo.cf / 1000.0) * 360));
		g2.setComposite(comp_temp);
		g.setColor(c);
		g.fillArc(x - WOBJ_SIZE / 2, y - WOBJ_SIZE / 2, WOBJ_SIZE, WOBJ_SIZE,
				0, 360);
		g.drawLine(x, y, sx, sy);
		g.setColor(c.darker());
		g.drawArc(x - WOBJ_SIZE / 2, y - WOBJ_SIZE / 2, WOBJ_SIZE, WOBJ_SIZE,
				0, 360);
		
		g.setColor(STRING_COLOR);
		g.drawString(wo.type.name(), x + WOBJ_SIZE, y + WOBJ_SIZE);
	}

	@Override
	public String getName() {
		return "Field";
	}

	@Override
	public void update(Document document) {

		init(WIDTH, HEIGHT);

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
			case RedNao:
			case BlueNao:
				WorldObject wo = objs.get(WorldObjects.values()[type]);
				wo.x = x;
				wo.y = y;
				wo.cf = cf;
				break;
			case Self:
				self.x = x;
				self.y = y;
				self.yaw = (float) Math.toRadians(yaw);
				self.cf = cf;
				break;
			default:
			}
		}
		NodeList candidateList = document.getElementsByTagName("Candidates");
		for (int i = 0; i < candidateList.getLength(); i++) {
			Element cElement = (Element) candidateList.item(i);
			int x = Integer.parseInt(cElement.getAttribute("x"));
			int y = Integer.parseInt(cElement.getAttribute("y"));
			float h = Float.parseFloat(cElement.getAttribute("h"));
			float w = Float.parseFloat(cElement.getAttribute("w"));
			SelfObject so = new SelfObject();
			so.x = x;
			so.y = y;
			so.yaw = h;
			so.w = w;
			candidates.add(so);
		}

		synchronized (fieldImage) {
			Graphics g = fieldImage.createGraphics();
			// Fieldを描画
			drawFieldImage(g);
			if (controlPanel.isDrawCandidate) {
				for (SelfObject so : candidates)
					drawCandidate(g, so);
			}
			drawObject(g, RED_GOAL_COLOR, objs.get(WorldObjects.YellowGoal),
					self);
			drawObject(g, BLUE_GOAL_COLOR, objs.get(WorldObjects.BlueGoal),
					self);
			drawObject(g, RED_TEAM_COLOR, objs.get(WorldObjects.RedNao), self);
			drawObject(g, BLUE_TEAM_COLOR, objs.get(WorldObjects.BlueNao), self);
			drawObject(g, BALL_COLOR, objs.get(WorldObjects.Ball), self);
			drawSelf(g, self);
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
			g.setColor(BACKGROUND_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());

			int drawWidth = fieldImage.getWidth();
			int drawHeight = fieldImage.getHeight();
			int x = (getWidth() - drawWidth) / 2;
			int y = (getHeight() - drawHeight) / 2;

			if (controlPanel.isAutoScale) {
				double n = (double) fieldImage.getWidth()
						/ fieldImage.getHeight();
				drawHeight = (int) (getHeight() * 1.0); // 100%
				drawWidth = (int) (drawHeight * n);
				x = (getWidth() - drawWidth) / 2;
				y = (getHeight() - drawHeight) / 2;
			}

			synchronized (fieldImage) {
				if (controlPanel.isAutoScale) {
					g.drawImage(changSize(fieldImage, drawWidth, drawHeight),
							x, y, BACKGROUND_COLOR, null);
				} else {
					g.drawImage(fieldImage, x, y, BACKGROUND_COLOR, null);
				}
			}
		}
	}
	
	private BufferedImage changSize(BufferedImage image, int width, int height) {
		BufferedImage shrinkImage = new BufferedImage(width, height, image
				.getType());
		Graphics2D g2d = shrinkImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
//		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
//				RenderingHints.VALUE_STROKE_NORMALIZE);
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return shrinkImage;
	}
	
	class ControlPanel extends JPanel {

		protected boolean isAutoScale = true;
		protected boolean isDrawCandidate = true;

		public ControlPanel() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);

			JCheckBox drawCandidateCheckBox = new JCheckBox("Candidate");
			drawCandidateCheckBox.setSelected(isDrawCandidate);
			drawCandidateCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isDrawCandidate = !isDrawCandidate;
				}
			});

			JCheckBox autoScaleCheckBox = new JCheckBox("自動スケール");
			autoScaleCheckBox.setSelected(isAutoScale);
			autoScaleCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isAutoScale = !isAutoScale;
					fieldPanel.repaint();
				}
			});

			add(drawCandidateCheckBox);
			add(autoScaleCheckBox);

			setPreferredSize(layout.preferredLayoutSize(this));
		}

	}

	private static class WorldObject {
		WorldObjects type;
		int x;
		int y;
		int cf;
		
		WorldObject(WorldObjects type) {
			this.type = type;
		}
	}

	private static class SelfObject extends WorldObject {
		float yaw;
		float w;
		
		SelfObject() {
			super(WorldObjects.Self);
		}
	}

	public enum WorldObjects {
		Self, Ball, RedNao, BlueNao, YellowGoal, BlueGoal
	}

	private static class Field {
		private static final int WIDTH = 4050 / 10;
		private static final int HEIGHT = 6050 / 10;
		private static final int TOP_MARGIN = 675 / 10;
		private static final int BOTTOM_MARGIN = 675 / 10;
		private static final int LEFT_MARGIN = 675 / 10;
		private static final int RIGHT_MARGIN = 675 / 10;
		private static final int FULL_WIDTH = LEFT_MARGIN + WIDTH
				+ RIGHT_MARGIN;
		private static final int FULL_HEIGHT = TOP_MARGIN + HEIGHT
				+ BOTTOM_MARGIN;
		private static final int PENALTY_WIDTH = 3050 / 10;
		private static final int PENALTY_HRIGHT = 650 / 10;
		private static final int CIRCLE_RADIUS = 625 / 10;
		private static final int GOAL_WIDTH = 1400 / 10;
		private static final int GOAL_HEIGHT = 500 / 10;
		private static final int DOT_MARGIN = 1825 / 10;
		private static final int DOT_RADIUS = 50 / 10;
	}

}
