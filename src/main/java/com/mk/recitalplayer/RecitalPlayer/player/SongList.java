package com.mk.recitalplayer.RecitalPlayer.player;

import java.util.List;

import com.mk.recitalplayer.RecitalPlayer.RecitalSong;
import com.vaadin.ui.Component;

public interface SongList {
	
	void setPreferVideo(boolean preferVideo);
	
	void selectSong(RecitalSong song);
	
	Component createLayout(List<RecitalSong> songs);
	
	void addCurrentSongChangedEventListener(CurrentSongChangedListener listener);
	
	void selectNextSong();
	
	void selectPreviousSong();
	
	public interface CurrentSongChangedListener {
		
		public void onCurrentSongChanged(RecitalSong song, boolean preferVideo);
		
	}
}
