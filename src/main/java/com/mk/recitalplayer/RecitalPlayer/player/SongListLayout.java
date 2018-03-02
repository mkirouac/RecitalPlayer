package com.mk.recitalplayer.RecitalPlayer.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mk.recitalplayer.RecitalPlayer.RecitalSong;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.renderers.HtmlRenderer;

public class SongListLayout implements SongList {


	
	private final List<CurrentSongChangedListener> currentSongChangedListeners = new ArrayList<>();
	private final Grid<RecitalSong> songGrid = new Grid<>();
	private List<RecitalSong> songs;
	private boolean preferVideo;

	@Override
	public void selectSong(RecitalSong song) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public Component createLayout(List<RecitalSong> songs) {
		
		this.songs = songs;
		songGrid.setHeightMode(HeightMode.UNDEFINED);
		songGrid.setWidth("100%");
		
		songGrid.setItems(songs);
		
		songGrid.setHeaderVisible(false);
		songGrid.addColumn(RecitalSong::getHTMLDescription).setRenderer(new HtmlRenderer())
				.setSortable(false);

		songGrid.addSelectionListener(new SelectionListener<RecitalSong>() {

			@Override
			public void selectionChange(SelectionEvent<RecitalSong> event) {

				if (event.getFirstSelectedItem() != null && event.getFirstSelectedItem().isPresent()) {

					final RecitalSong song = event.getFirstSelectedItem().get();
					onCurrentSongChanged(song);
					
				}
			}
		});

		new GridRowDragger<>(songGrid);

		return songGrid;

	}

	@Override
	public void selectNextSong() {
		
		//TODO: Will not work with reording of grid
		Set<RecitalSong> selectedSongs = songGrid.getSelectedItems();
		if (selectedSongs.size() > 0) {
			RecitalSong selectedSong = selectedSongs.stream().findFirst().get();
			int index = songs.indexOf(selectedSong);
			if (index < songs.size() - 1) {
				songGrid.select(songs.get(index + 1));
			}
		}
	}
	@Override
	public void selectPreviousSong() {
		
		//TODO: Will not work with reording of grid
		Set<RecitalSong> selectedSongs = songGrid.getSelectedItems();
		if (selectedSongs.size() > 0) {
			RecitalSong selectedSong = selectedSongs.stream().findFirst().get();
			int index = songs.indexOf(selectedSong);
			if (index > 0) {
				songGrid.select(songs.get(index - 1));

			}
		}
	}

	public void addCurrentSongChangedEventListener(CurrentSongChangedListener listener) {
		currentSongChangedListeners.add(listener);
	}
	
	public void removeCurrentSongChangedEVentListener(CurrentSongChangedListener listener) {
		currentSongChangedListeners.remove(listener);
	}
	
	protected void onCurrentSongChanged(RecitalSong currentSong) {
		for(CurrentSongChangedListener listener : currentSongChangedListeners) {
			listener.onCurrentSongChanged(currentSong, preferVideo);
		}
	}

	@Override
	public void setPreferVideo(boolean preferVideo) {
		this.preferVideo = preferVideo;
	}


	
	
}
