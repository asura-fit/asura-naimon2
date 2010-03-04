/**
 * 
 */
package jp.ac.fit.asura.nao.naimon.event;

import java.util.EventListener;
import java.util.Hashtable;

import org.w3c.dom.Document;

/**
 * @author kilo
 * 
 */
public interface NaimonEventListener extends EventListener {
	public void update(Document document);
	
	public void addRequestParam(Hashtable<String, String> params);

	public void connected(String host, int port);

	public void disconnected();
}
