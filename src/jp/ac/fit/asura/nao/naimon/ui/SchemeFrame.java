/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import org.w3c.dom.Document;

/**
 * @author kilo
 * 
 */
public class SchemeFrame extends NaimonInFrame {

	private SchemePanel schemePanel;
	private JToolBar toolBar;

	public SchemeFrame() {
		init();
		schemePanel = new SchemePanel();
		toolBar = new JToolBar();
		initToolBar();
		JScrollPane scrollPane = new JScrollPane(schemePanel);

		Container cpane = this.getContentPane();
		cpane.setLayout(new BorderLayout());
		cpane.add(toolBar, BorderLayout.NORTH);
		cpane.add(scrollPane, BorderLayout.CENTER);
	}

	private void init() {
		setTitle(this.getName());
	}

	private static final String RES_PATH = "/jp/ac/fit/asura/nao/naimon/resource/icon/";
	private JButton newButton;
	private JButton loadButton;
	private JButton saveButton;
	private JButton redoButton;
	private JButton undoButton;
	private JButton evalButton;
	private JButton evalAllButton;

	private void initToolBar() {
		newButton = createButton("page.png");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.clear();
			}
		});
		loadButton = createButton("open.gif");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.loadfile();
			}
		});
		saveButton = createButton("disk.png");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.savefile();
			}
		});
		redoButton = createButton("arrow_right.png");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.redo();
			}
		});
		undoButton = createButton("arrow_left.png");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.undo();
			}
		});
		evalButton = createButton("control_end.png");
		evalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.evaluateCommand();
			}
		});
		evalAllButton = createButton("control_play.png");
		evalAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schemePanel.evaluateAllCommand();
			}
		});

		toolBar.add(newButton);
		toolBar.add(loadButton);
		toolBar.add(saveButton);
		toolBar.addSeparator();
		toolBar.add(undoButton);
		toolBar.add(redoButton);
		toolBar.addSeparator();
		toolBar.add(evalButton);
		toolBar.add(evalAllButton);
	}

	private JButton createButton(String filename) {
		JButton button = new JButton(loadIcon(filename));
		button.setBorderPainted(false);
		return button;
	}

	private ImageIcon loadIcon(String filename) {
		return new ImageIcon(getClass().getResource(RES_PATH + filename));
	}

	@Override
	public String getName() {
		return "Scheme";
	}

	@Override
	public void update(Document document) {

	}

	class SchemePanel extends JEditorPane {

		/** コマンド送信アクションの名前です */
		public static final String EVALUATE_ACTION_NAME = "eval";
		/** コマンド一括送信アクションの名前です */
		public static final String EVALUATE_ALL_ACTION_NAME = "eval all";
		/** アンドゥアクションの名前です */
		public static final String UNDO_ACTION_NAME = "undo";
		/** リドゥアクションの名前です */
		public static final String REDO_ACTION_NAME = "redo";

		/** アンドゥーを管理するオブジェクト */
		protected UndoManager undoManager;
		/** このコンポーネントが使用するリスナーの実装 */
		protected LocalListener listener;
		/** ポップアップメニュー */
		protected JPopupMenu popupMenu;

		/** Comment for <code>undoAction</code> */
		private UndoAction undoAction;
		/** Comment for <code>redoAction</code> */
		private RedoAction redoAction;

		public SchemePanel() {
			listener = new LocalListener();
			undoManager = new UndoManager();
			getDocument().addUndoableEditListener(listener);
			addMouseListener(listener);
			initActions();
			initPopupMenu();

			setPreferredSize(new Dimension(320, 240));
		}

		private void initActions() {
			ActionMap actionMap = getActionMap();
			InputMap inputMap = getInputMap();
			Action action = null;
			Object name = null;

			action = new EvaluateCommandAction();
			name = action.getValue(Action.NAME);
			actionMap.put(name, action);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
					KeyEvent.CTRL_MASK), name);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,
					KeyEvent.CTRL_MASK), name);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_J,
					KeyEvent.CTRL_MASK), name);

			action = new EvaluateAllCommandAciton();
			name = action.getValue(Action.NAME);
			actionMap.put(name, action);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B,
					KeyEvent.CTRL_MASK), name);

			undoAction = new UndoAction();
			action = undoAction;
			name = action.getValue(Action.NAME);
			actionMap.put(name, action);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
					KeyEvent.CTRL_MASK), name);

			redoAction = new RedoAction();
			action = redoAction;
			name = action.getValue(Action.NAME);
			actionMap.put(name, action);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
					KeyEvent.CTRL_MASK), name);

			setActionMap(actionMap);
			setInputMap(javax.swing.JComponent.WHEN_FOCUSED, inputMap);
		}

		protected void initPopupMenu() {
			popupMenu = new JPopupMenu();

			Action action = null;
			ActionMap actionMap = getActionMap();

			action = actionMap.get(EVALUATE_ACTION_NAME);
			popupMenu.add(action);
			action = actionMap.get(EVALUATE_ALL_ACTION_NAME);
			popupMenu.add(action);

			popupMenu.addSeparator();
			popupMenu.add(undoAction);
			popupMenu.add(redoAction);
		}

		/**
		 * <code>commands</code>で指定された文字列をAIBOに送信します。
		 * 
		 * @param commands
		 */
		protected void sendCommands(String commands) {
			try {
				connector.sendScheme(commands);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * アンドゥー、リドゥーの状態を更新します。
		 */
		protected void updateUndoRedoState() {
			if (undoAction != null) {
				undoAction.setEnabled(undoManager.canUndo());
				undoAction.putValue(Action.NAME, undoManager
						.getUndoPresentationName());
			}
			if (undoButton != null) {
				undoButton.setEnabled(undoManager.canUndo());
			}
			if (redoAction != null) {
				redoAction.setEnabled(undoManager.canRedo());
				redoAction.putValue(Action.NAME, undoManager
						.getRedoPresentationName());
			}
			if (redoButton != null) {
				redoButton.setEnabled(undoManager.canRedo());
			}
		}

		/**
		 * コマンド一括送信アクション。
		 */
		private class EvaluateCommandAction extends AbstractAction {

			public EvaluateCommandAction() {
				super(EVALUATE_ACTION_NAME);
			}

			public void actionPerformed(java.awt.event.ActionEvent e) {
				evaluateCommand();
			}
		}

		/**
		 * コマンド一括送信アクション。
		 */
		public class EvaluateAllCommandAciton extends AbstractAction {

			public EvaluateAllCommandAciton() {
				super(EVALUATE_ALL_ACTION_NAME);
			}

			public void actionPerformed(ActionEvent e) {
				evaluateAllCommand();
			}
		}

		/**
		 * アンドゥアクション。
		 */
		private class UndoAction extends AbstractAction {

			public UndoAction() {
				super(undoManager.getUndoPresentationName());
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent e) {
				undo();
			}
		}

		/**
		 * リドゥアクション
		 */
		private class RedoAction extends AbstractAction {

			public RedoAction() {
				super(undoManager.getRedoPresentationName());
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent e) {
				redo();
			}
		}

		private void evaluateCommand() {
			String selectedText = getSelectedText();

			if (selectedText == null || selectedText.length() <= 0) {
				int curPos = getCaretPosition();
				try {
					javax.swing.text.Document doc = getDocument();
					int elemIndex = doc.getDefaultRootElement()
							.getElementIndex(curPos);
					javax.swing.text.Element elem = doc.getDefaultRootElement()
							.getElement(elemIndex);
					int endOffset = elem.getEndOffset();
					int startOffset = elem.getStartOffset();
					endOffset = (endOffset > doc.getLength() ? doc.getLength()
							: endOffset);
					selectedText = doc.getText(startOffset, endOffset
							- startOffset);
				} catch (Exception ex) {
				}
			}

			if (selectedText != null && selectedText.length() > 0) {
				sendCommands(selectedText);
			}
		}

		private void evaluateAllCommand() {
			String allCommands = getText();
			if (allCommands != null && allCommands.length() > 0) {
				sendCommands(allCommands);
			}
		}

		private void redo() {
			try {
				undoManager.redo();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			updateUndoRedoState();
		}

		private void undo() {
			try {
				undoManager.undo();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			updateUndoRedoState();
		}

		private void clear() {
			setText("");
		}

		private void savefile() {
			JFileChooser filechooser = new JFileChooser();
			int selected = filechooser.showSaveDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				try {
					String text = getText();
					final FileOutputStream fo = new FileOutputStream(
							filechooser.getSelectedFile());
					final PrintStream ps = new PrintStream(fo);
					ps.print(text);
					ps.close();
					fo.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (selected == JFileChooser.CANCEL_OPTION) {
			} else if (selected == JFileChooser.ERROR_OPTION) {
			}

		}

		private void loadfile() {
			JFileChooser filechooser = new JFileChooser();
			int selected = filechooser.showOpenDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				try {
					String text = "";
					final FileInputStream fi = new FileInputStream(filechooser
							.getSelectedFile());
					final BufferedReader br = new BufferedReader(
							new InputStreamReader(fi));
					String str = br.readLine();
					while (str != null) {
						text += str + "\n";
						str = br.readLine();
					}

					br.close();
					fi.close();

					setText(text);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (selected == JFileChooser.CANCEL_OPTION) {
			} else if (selected == JFileChooser.ERROR_OPTION) {
			}
		}

		private class LocalListener implements MouseListener,
				UndoableEditListener {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() == true) {
					popupMenu.show(SchemePanel.this, e.getX(), e.getY());
				}
			}

			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				updateUndoRedoState();
			}

		}

	}

}
