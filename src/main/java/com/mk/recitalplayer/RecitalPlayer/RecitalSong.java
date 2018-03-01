package com.mk.recitalplayer.RecitalPlayer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;

import lombok.Getter;
import lombok.Setter;

public class RecitalSong {

	private final @Getter String index;
	private final @Getter String pieceName;
	private final @Getter String playbackUrl;
	
	private @Getter @Setter String videoLink;
	private @Getter @Setter String performerName;
	private @Getter @Setter String performerCountry;
	private @Getter @Setter String performerExperience;
	private @Getter @Setter String duration;
	private @Getter @Setter String sourceOfMusic;
	private @Getter @Setter String instrumentUsed;
	private @Getter @Setter String recordingMethod;
	private @Getter @Setter String technicalFeedbackWanted;
	private @Getter @Setter String additionalInfo;
	private @Getter @Setter String composer;

	private @Getter @Setter boolean hasComments;
	
	public RecitalSong(String index, String pieceName, String playbackUrl) {
		this.index = index;
		this.playbackUrl = playbackUrl;
		this.pieceName = pieceName;
	}

	public String getExportDescription() {
		return "[b]" + index + "[/b]" + " " + performerName + ", " + pieceName + " (" + composer + ")";
	}
	
	public String getHTMLDescription() {

		
//		Object comment = VaadinSession.getCurrent().getAttribute("song" + index);
//		
//		boolean hasComments = comment != null && comment.toString().length() > 0; 
		
		StringBuilder sb = new StringBuilder();
		
		boolean hasVideo = videoLink != null && videoLink.length() > 0; 
		if(hasVideo) {
			sb.append(VaadinIcons.YOUTUBE.getHtml());
		} else {
			sb.append(emptyVideoIcon());
		}
		
		if(hasComments) {
			sb.append(VaadinIcons.COMMENT_O.getHtml());
			sb.append("&nbsp");
		} else {
			sb.append(emptyCommentIcon());
		}
		
		if(hasVideo && !hasComments) {
			sb.append("&nbsp&nbsp");
		}

		sb.append("&nbsp");
		
		sb.append("<b>" + index + "</b>" + " " + performerName + ", " + pieceName + " (" + composer + ")");
		return sb.toString();
	}

	private String emptyVideoIcon() {
		return "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
	}


	private String emptyCommentIcon() {
		return "&nbsp&nbsp&nbsp&nbsp&nbsp";
	}

}
