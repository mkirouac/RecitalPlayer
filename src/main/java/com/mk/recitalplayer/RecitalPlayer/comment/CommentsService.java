package com.mk.recitalplayer.RecitalPlayer.comment;

import java.util.Collection;

public interface CommentsService {

	void save(String sessionId, String recital, String songIndex, String comment, String songDescription);
	
	String find(String sessionId, String recital, String songIndex);

	Collection<String> selectAll(String commentsSessionKey, String recital);
	
}
