package com.mk.recitalplayer.RecitalPlayer.player;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mk.recitalplayer.RecitalPlayer.PlayerSession;
import com.mk.recitalplayer.RecitalPlayer.RecitalSong;
import com.mk.recitalplayer.RecitalPlayer.comment.CommentsService;
import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

public class CurrentSongLayout implements CurrentSong {

	private VerticalLayout mediaLayout = new VerticalLayout();
	private final VerticalLayout selectedSongLayout = new VerticalLayout();

	private final CommentsService commentsService;

	public CurrentSongLayout(CommentsService commentsService) {// TODO Move comments persistence logic out of ui
		this.commentsService = commentsService;
	}

	@Override
	public Component createLayout() {
		selectedSongLayout.setMargin(new MarginInfo(false, true, true, true));
		selectedSongLayout.addComponent(mediaLayout);
		mediaLayout.setWidth("100%");

		return selectedSongLayout;
	}

	@Override
	public void setCurrentSong(RecitalSong song, boolean preferVideo) {
		selectedSongLayout.removeAllComponents();

		selectedSongLayout.addComponent(mediaLayout);
		mediaLayout.setWidth("100%");

		setCurrentMedia(song, preferVideo);

		// Comments
		TextArea commentsText = new TextArea("Comments");

		commentsText.addStyleName(ValoTheme.TEXTAREA_LARGE);
		commentsText.setWidth("100%");

		PlayerSession session = PlayerSession.getCurrent();
		String comment = commentsService.find(session.getCommentsSessionKey(), session.getCurrentRecital(),
				song.getIndex());
		if (comment != null) {
			commentsText.setValue(comment);
		}

		commentsText.setResponsive(true);

		commentsText.addDetachListener(new DetachListener() {

			@Override
			public void detach(DetachEvent event) {

				boolean hasComments = commentsText.getValue() != null && commentsText.getValue().length() > 0;
				song.setHasComments(hasComments);
				if (hasComments) {
					commentsService.save(session.getCommentsSessionKey(), session.getCurrentRecital(), song.getIndex(),
							commentsText.getValue(), song.getExportDescription());
				}
			}
		});
		commentsText.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				boolean hasComments = event.getValue() != null && event.getValue().length() > 0;
				song.setHasComments(hasComments);
				if (hasComments) {
					commentsService.save(session.getCommentsSessionKey(), session.getCurrentRecital(), song.getIndex(),
							event.getValue(), song.getExportDescription());
				}
			}
		});
		selectedSongLayout.addComponent(commentsText);

		Button exportCommentsButton = new Button("Export comments");
		exportCommentsButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Collection<String> comments = commentsService.selectAll(
						PlayerSession.getCurrent().getCommentsSessionKey(),
						PlayerSession.getCurrent().getCurrentRecital());
				StringBuilder allComments = new StringBuilder();

				for (String comment : comments) {
					allComments.append(comment).append("\r\n").append("\r\n");
				}

				Window window = new Window("Comments");
				window.setSizeFull();

				VerticalLayout layout = new VerticalLayout();
				layout.setSizeFull();

				TextArea ta = new TextArea();
				ta.setSizeFull();
				ta.setValue(allComments.toString());

				layout.addComponent(ta);
				layout.setExpandRatio(ta, 1);

				Button closeButton = new Button("Close");
				closeButton.addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent().removeWindow(window);
					}
				});

				layout.addComponent(closeButton);
				layout.setExpandRatio(closeButton, 0);

				window.setContent(layout);

				UI.getCurrent().addWindow(window);
			}
		});
		selectedSongLayout.addComponent(exportCommentsButton);

		selectedSongLayout.addComponent(new Label(song.getIndex()));
		selectedSongLayout.addComponent(new Label("Performer's name: " + song.getPerformerName()));
		selectedSongLayout.addComponent(new Label("From: " + song.getPerformerCountry()));
		selectedSongLayout.addComponent(new Label("Experience: " + song.getPerformerExperience()));
		selectedSongLayout.addComponent(new Label("Title of piece: " + song.getPieceName()));
		selectedSongLayout.addComponent(new Label("Composer: " + song.getComposer()));
		selectedSongLayout.addComponent(new Label("Duration: " + song.getDuration()));
		selectedSongLayout.addComponent(new Label("Source of music: " + song.getSourceOfMusic()));
		selectedSongLayout.addComponent(new Label("Instrument used: " + song.getInstrumentUsed()));
		selectedSongLayout.addComponent(new Label("Recording method: " + song.getRecordingMethod()));
		selectedSongLayout.addComponent(new Label("Technical feedback wanted: " + song.getTechnicalFeedbackWanted()));

		Label additionalInfoText = new Label(song.getAdditionalInfo(), ContentMode.PREFORMATTED);
		additionalInfoText.setWidth("100%");
		additionalInfoText.setHeight(null);
		additionalInfoText.addStyleName("wrap");
		selectedSongLayout.addComponent(additionalInfoText);

		// TODO
		// // Control buttons
		// Button nextButton = new Button("Next");
		// nextButton.addStyleName(ValoTheme.BUTTON_LINK);
		// nextButton.addClickListener(new Button.ClickListener() {
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		//
		// songList.selectNextSong();
		//
		// }
		// });
		// selectedSongLayout.addComponent(nextButton);
		//
		// Button previousButton = new Button("Previous");
		// previousButton.addStyleName(ValoTheme.BUTTON_LINK);
		// previousButton.addClickListener(new Button.ClickListener() {
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		//
		// songList.selectPreviousSong();
		//
		// }
		// });
		// selectedSongLayout.addComponent(previousButton);
		//
		// Button exportCommentsButton = new Button("Export Comments");
		// exportCommentsButton.addClickListener(new Button.ClickListener() {
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		//
		// exportComments(songs);
		// }
		// });
		// selectedSongLayout.addComponent(exportCommentsButton);
	}

	private void setCurrentMedia(RecitalSong song, boolean preferVideo) {

		mediaLayout.removeAllComponents();

		Label title = new Label(song.getHTMLDescription(), ContentMode.HTML);
		title.addStyleName(ValoTheme.LABEL_HUGE);
		mediaLayout.addComponent(title);
		mediaLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);

		if (preferVideo && song.getVideoLink() != null && song.getVideoLink().length() > 0) {

			// <iframe width="560" height="315"
			// src="https://www.youtube.com/embed/aop-tcQKKME?rel=0" frameborder="0"
			// allow="autoplay; encrypted-media" allowfullscreen></iframe>

			// https://www.youtube.com/embed/aop-tcQKKME?rel=0
			String videoId = getVideoId(song.getVideoLink());
			String embedUrl = String.format("https://www.youtube.com/embed/%s?rel=0", videoId);
			BrowserFrame iframe = new BrowserFrame(null, new ExternalResource(embedUrl));
			iframe.setWidth("560px");
			iframe.setHeight("315px");
			iframe.setId("youtube-embedded");
			JavaScript.getCurrent().execute(
					"document.getElementById('youtube-embedded').childNodes[0].setAttribute('frameborder','0')");
			JavaScript.getCurrent().execute(
					"document.getElementById('youtube-embedded').childNodes[0].setAttribute('allow','autoplay; encrypted-media')");
			JavaScript.getCurrent().execute(
					"document.getElementById('youtube-embedded').childNodes[0].setAttribute('allowfullscreen')");

			mediaLayout.setMargin(false);

			mediaLayout.addComponent(iframe);
			mediaLayout.setComponentAlignment(iframe, Alignment.MIDDLE_CENTER);
		} else {

			// TODO Eventually need to add option to switch to audio
			Audio audio = new Audio(null, new ExternalResource(song.getPlaybackUrl()));
			audio.setWidth("100%");
			audio.play();
			mediaLayout.addComponent(audio);

		}

	}

	final static String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

	public static String getVideoId(String videoUrl) {
		if (videoUrl == null || videoUrl.trim().length() <= 0)
			return null;

		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(videoUrl);

		if (matcher.find())
			return matcher.group(1);
		return null;
	}

}
