package com.mk.recitalplayer.RecitalPlayer.comment;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class JpaCommentsService implements CommentsService{

	private final CommentsRepository repository;
	
	@Autowired
	public JpaCommentsService(CommentsRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void save(String sessionId, String recital, String songIndex, String comment, String songDescription) {
		
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
		entity.setSongDescription(songDescription);
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

	@Override
	public Collection<String> selectAll(String commentsSessionKey, String recital) {
		return repository.findAllBySessionIdAndRecital(commentsSessionKey, recital).stream()
				.sorted((e1, e2) -> e1.getSongIndex().compareTo(e2.getSongIndex()))
				.map((e) -> e.getSongDescription() + "\r\n" + e.getComment())
				.collect(Collectors.toList());
	}

	
}
