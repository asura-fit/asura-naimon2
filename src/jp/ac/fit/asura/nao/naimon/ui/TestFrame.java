package jp.ac.fit.asura.nao.naimon.ui;

import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	public void update(Document document) {
		NodeList gcdNode = document.getElementsByTagName("GCD");
		Element gcd = (Element)gcdNode.item(0);
		int width = Integer.parseInt(gcd.getAttribute("width"));
		int height = Integer.parseInt(gcd.getAttribute("height"));
		String gdata = gcd.getTextContent();
		
		/*
		System.out.println("width:" + width);
		System.out.println("height:" + height);
		System.out.println("gdata:" + gdata);
		*/
	}

}
