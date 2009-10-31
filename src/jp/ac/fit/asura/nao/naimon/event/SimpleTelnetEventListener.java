package jp.ac.fit.asura.nao.naimon.event;

import java.util.EventListener;

public interface SimpleTelnetEventListener extends EventListener {
	public void received(String str);
}
