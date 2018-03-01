package com.mk.recitalplayer.RecitalPlayer.player;

import com.mk.recitalplayer.RecitalPlayer.RecitalSong;
import com.vaadin.ui.Component;

public interface CurrentSong {

	Component createLayout();
	
	void setCurrentSong(RecitalSong song, boolean preferVideo);
}
