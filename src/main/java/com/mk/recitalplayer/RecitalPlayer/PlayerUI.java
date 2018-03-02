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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

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

		//TODO Eventually add a desktop version where everything would fit to screen.
		
		initializeOptionsLayout();

		// Player split panel
		playerSplitPanel.setSplitPosition(25f);
		playerSplitPanel.setWidth("100%");
		playerSplitPanel.setHeight(null);
		playerSplitPanel.setFirstComponent(songList.createLayout(songs));
		playerSplitPanel.setSecondComponent(currentSong.createLayout());
		mainLayout.addComponent(playerSplitPanel);
		setContent(mainLayout);
	}

	private void initializeOptionsLayout() {
		HorizontalLayout optionsLayout = new HorizontalLayout();

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

		recitalSelectionBox.addStyleName(ValoTheme.COMBOBOX_TINY);
		recitalSelectionBox.setSelectedItem(configurationProperties.getDefaultForumRecital());

		optionsLayout.addComponent(recitalSelectionBox);

		// Textfield to display the current comments session key
		// TODO Need to improve change of session.
		// -make sure the current comment is not saved to the new session.
		// -Need to refresh the grid comments indicators
		// -Need to refresh current comment
		// -Could be easier to fully reload UI. Perhaps this should be readonly with an
		// option to go to a different UI?
		TextField commentsSavedToTextField = new TextField("Comments saved to");
		commentsSavedToTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		commentsSavedToTextField.setWidth("260px");
		commentsSavedToTextField.setValue(PlayerSession.getCurrent().getCommentsSessionKey());
		commentsSavedToTextField.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				if (event.getValue() != null && event.getValue().length() > 0) {
					PlayerSession.getCurrent().setCommentsSessionKey(event.getValue());
				}
			}
		});

		optionsLayout.addComponent(commentsSavedToTextField);

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

		preferVideoOption.addStyleName(ValoTheme.CHECKBOX_SMALL);
		
		optionsLayout.addComponent(preferVideoOption);

		
		Button showOptionsButton = new Button(VaadinIcons.PLUS.getHtml() + " Options");
		showOptionsButton.addStyleName(ValoTheme.BUTTON_TINY);
		showOptionsButton.addStyleName(ValoTheme.BUTTON_LINK);
		showOptionsButton.setCaptionAsHtml(true);
		showOptionsButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(optionsLayout.isVisible()) {
					optionsLayout.setVisible(false);
					showOptionsButton.setCaption(VaadinIcons.PLUS.getHtml() + " Options");
				} else {
					optionsLayout.setVisible(true);
					showOptionsButton.setCaption(VaadinIcons.MINUS.getHtml() + " Options");
				}
			}
		});
		
		mainLayout.addComponent(showOptionsButton);
		optionsLayout.setVisible(false);
		
		mainLayout.addComponent(optionsLayout);

	}

	private void exportComments(Collection<RecitalSong> songs) {
		System.out.println();
		System.out.println();
		System.out.println("##############################");
		System.out.println("##");
		System.out.println("##          EXPORTING COMMENTS");
		System.out.println("##");
		System.out.println("##############################");
		// TODO Won't work anymore.
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
