package com.mk.recitalplayer.RecitalPlayer.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
class JpaCommentsService implements CommentsService{

	private final CommentsRepository repository;
	
	public JpaCommentsService(CommentsRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void save(String sessionId, String recital, String songIndex, String comment) {
		
		Optional<CommentEntity> optional = repository.findBySessionIdAndRecitalAndSongIndex(sessionId, recital, songIndex); 
		CommentEntity entity =  null;
		if(optional.isPresent()) {
			entity = optional.get();
		} else {
			entity = new CommentEntity();
		}
		entity.setSessionId(sessionId);
		entity.setRecital(recital);
		entity.setSongIndex(songIndex);
		entity.setComment(comment);
		repository.saveAndFlush(entity);
	}

	@Override
	public String find(String sessionId, String recital, String songIndex) {
		
		//List<CommentEntity> all = repository.findAll();
		
		String comment = null;
		
		Optional<CommentEntity> entity = repository.findBySessionIdAndRecitalAndSongIndex(sessionId, recital, songIndex);
		if(entity.isPresent()) {
			comment = entity.get().getComment();
		}
		
		return comment;
	}

	
}
