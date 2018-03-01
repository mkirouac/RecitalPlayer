package com.mk.recitalplayer.RecitalPlayer.comment;

public interface CommentsService {

	void save(String sessionId, String recital, String songIndex, String comment);
	
	String find(String sessionId, String recital, String songIndex);
	
}
