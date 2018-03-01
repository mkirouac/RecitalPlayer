package com.mk.recitalplayer.RecitalPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.mk.recitalplayer.RecitalPlayer.comment.CommentsService;
import com.mk.recitalplayer.RecitalPlayer.parser.ForumRecitalParser;
import com.mk.recitalplayer.RecitalPlayer.player.CurrentSong;
import com.mk.recitalplayer.RecitalPlayer.player.CurrentSongLayout;
import com.mk.recitalplayer.RecitalPlayer.player.SongList;
import com.mk.recitalplayer.RecitalPlayer.player.SongListLayout;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@StyleSheet("playerui.css")
@SpringUI
public class PlayerUI extends UI {

	private final HorizontalSplitPanel playerSplitPanel = new HorizontalSplitPanel();
	private final VerticalLayout mainLayout = new VerticalLayout();

	private SongList songList = new SongListLayout();
	private CurrentSong currentSong;
	private List<RecitalSong> songs;

	@Autowired
	private ForumRecitalParser forumRecitalParser;

	@Autowired
	private RecitalPlayerConfigurationProperties configurationProperties;

	@Autowired
	private CommentsService commentsService;
	
	@Override
	protected void init(VaadinRequest request) {

		currentSong = new CurrentSongLayout(commentsService);
		String currentCommentsSessionKey = PlayerSession.getCurrent().getCommentsSessionKey();
		if (currentCommentsSessionKey == null) {
			currentCommentsSessionKey = UUID.randomUUID().toString();
			PlayerSession.getCurrent().setCommentsSessionKey(currentCommentsSessionKey);
		}
		
		// Parse recital
		songs = forumRecitalParser.parse(configurationProperties.getDefaultForumRecital());

		// Initialize main UI

		initializeMainUI();

		songList.addCurrentSongChangedEventListener((song, preferVideo) -> {
			currentSong.setCurrentSong(song, preferVideo);
			exportComments(songs);
		});

		// Initialize the UI with the first song
		RecitalSong firstSong = songs.get(0);

		songList.selectSong(firstSong);
	}

	private void initializeMainUI() {

		// Recital selection
		List<String> recitals = new ArrayList<>();
		for (String recital : configurationProperties.getForumRecital().keySet()) {

			recitals.add(recital);
		}
		ComboBox<String> recitalSelectionBox = new ComboBox<>("Current recital: ");
		recitalSelectionBox.setItems(recitals);
		recitalSelectionBox.setEmptySelectionAllowed(false);
	
		recitalSelectionBox.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				String recital = event.getValue();
				PlayerSession.getCurrent().setCurrentRecital(recital);
				songs = forumRecitalParser.parse(recital);
				playerSplitPanel.setFirstComponent(songList.createLayout(songs));
				playerSplitPanel.setSecondComponent(currentSong.createLayout());
			}
		});
		
		recitalSelectionBox.setSelectedItem(configurationProperties.getDefaultForumRecital());
		
		mainLayout.addComponent(recitalSelectionBox);

		// Option to prefer video link
		CheckBox preferVideoOption = new CheckBox("Prefer video");
		preferVideoOption.setValue(true);
		preferVideoOption.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {

			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				songList.setPreferVideo(event.getValue());
			}
		});
		songList.setPreferVideo(true);

		mainLayout.addComponent(preferVideoOption);

		// Player split panel
		playerSplitPanel.setSplitPosition(25f);
		playerSplitPanel.setWidth("100%");
		playerSplitPanel.setHeight(null);
		playerSplitPanel.setFirstComponent(songList.createLayout(songs));
		playerSplitPanel.setSecondComponent(currentSong.createLayout());
		mainLayout.addComponent(playerSplitPanel);
		setContent(mainLayout);
	}

	private void exportComments(Collection<RecitalSong> songs) {
		System.out.println();
		System.out.println();
		System.out.println("##############################");
		System.out.println("##");
		System.out.println("##          EXPORTING COMMENTS");
		System.out.println("##");
		System.out.println("##############################");
		//TODO Won't work anymore.
		for (RecitalSong song : songs) {
			Object comment = VaadinSession.getCurrent().getAttribute("song" + song.getIndex());
			if (comment != null && comment.toString().length() > 0) {
				System.out.println(song.getExportDescription());
				System.out.println(comment);
				System.out.println();
			}
		}
	}

}
