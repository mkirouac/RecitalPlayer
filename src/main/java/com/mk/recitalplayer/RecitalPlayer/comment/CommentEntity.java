package com.mk.recitalplayer.RecitalPlayer.comment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Comments")
class CommentEntity implements Serializable{

	//TODO Composite primary key session + recital + songIndex
	
	@Id
	@GeneratedValue
	@Column(name = "CommentId")
	private @Getter @Setter Long commentId;
	
	@Column(name = "SessionId")
	private @Getter @Setter String sessionId;
	
	@Column(name = "Recital")
	private @Getter @Setter String recital;
	
	@Column(name = "SongIndex")
	private @Getter @Setter String songIndex;
	
	@Column(name ="Comment") 
	private @Getter @Setter String comment;
	
}
