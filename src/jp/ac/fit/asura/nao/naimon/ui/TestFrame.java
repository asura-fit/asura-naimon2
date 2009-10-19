package jp.ac.fit.asura.nao.naimon.ui;

import java.util.logging.Logger;

import jp.ac.fit.asura.nao.naimon.Main;

public class TestFrame extends NaimonInFrame {
	private static final Logger log = Logger.getLogger(Main.class.toString());
	
	public TestFrame() {
		this.setTitle(this.getName());
	}
	
	@Override
	public String getName() {
		return "Test Frame";
	}

	@Override
	public void update() {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("message updated.");
	}

}
