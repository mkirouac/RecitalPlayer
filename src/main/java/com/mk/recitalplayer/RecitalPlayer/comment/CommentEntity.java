package com.mk.recitalplayer.RecitalPlayer.comment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
	@NotNull
	private @Getter @Setter String sessionId;
	
	@Column(name = "Recital")
	@NotNull
	private @Getter @Setter String recital;
	
	@Column(name = "SongIndex")
	@NotNull
	private @Getter @Setter String songIndex;
	
	@Column(name ="Comment", columnDefinition = "TEXT") 
	@NotNull
	private @Getter @Setter String comment;
	
	@Column(name = "SongDescription")
	@NotNull
	private @Getter @Setter String songDescription;
}
