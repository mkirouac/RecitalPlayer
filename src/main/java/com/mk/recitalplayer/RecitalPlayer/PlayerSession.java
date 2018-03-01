package com.mk.recitalplayer.RecitalPlayer;

import com.vaadin.server.VaadinSession;

public class PlayerSession {

	private static final String CURRENT_RECITAL_KEY = "player.current.recital";
	private static final String COMMENTS_SESSION_KEY = "player.comments.session";
	
	public static PlayerSession getCurrent() {
		return new PlayerSession(VaadinSession.getCurrent());
	}
	
	private final VaadinSession vaadinSession;
	
	private PlayerSession(VaadinSession vaadinSession) {
		this.vaadinSession = vaadinSession;
	}
	
	
	public String getCommentsSessionKey() {
		return stringOrNull(vaadinSession.getAttribute(COMMENTS_SESSION_KEY));
	}
	
	public void setCommentsSessionKey(String sessionKey) {
		vaadinSession.setAttribute(COMMENTS_SESSION_KEY, sessionKey);
	}
	
	public String getCurrentRecital() {
		return stringOrNull(vaadinSession.getAttribute(CURRENT_RECITAL_KEY));
	}
	
	public void setCurrentRecital(String recital) {
		vaadinSession.setAttribute(CURRENT_RECITAL_KEY, recital);
	}
	
	private String stringOrNull(Object object) {
		String string = null;
		if(object != null) {
			string = object.toString();
		}
		return string;
	}
}
