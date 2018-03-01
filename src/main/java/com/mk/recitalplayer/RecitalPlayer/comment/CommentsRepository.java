package com.mk.recitalplayer.RecitalPlayer.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface CommentsRepository extends JpaRepository<CommentEntity, String> {

	
	Optional<CommentEntity> findBySessionIdAndRecitalAndSongIndex(String sessionId, String recital, String songIndex);
	
}
