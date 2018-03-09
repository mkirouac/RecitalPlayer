package com.mk.recitalplayer.RecitalPlayer.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mk.recitalplayer.RecitalPlayer.RecitalPlayerConfigurationProperties;
import com.mk.recitalplayer.RecitalPlayer.RecitalSong;

@Component
public class ForumRecitalParser {
	
	private final RecitalPlayerConfigurationProperties configurationProperties;
	
	@Autowired
	public ForumRecitalParser(RecitalPlayerConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}
	
	
	
	
	public List<RecitalSong> parse(String recitalName) {
		
		String[] recitalUrls = configurationProperties.getForumRecital().get(recitalName);
		
		List<RecitalSong> songs = new ArrayList<>();
		for(String url : recitalUrls) {
			songs.addAll(parseUrl(url));
		}
		return songs;
	}

	private List<RecitalSong> parseUrl(String url) {
		List<RecitalSong> songs = new ArrayList<>();
		
		RestTemplate template = new RestTemplate();

		ResponseEntity<String> response = template.getForEntity(url, String.class);
		
		Document doc = Jsoup.parse(response.getBody());
		
		Elements posts = doc.select(".post-content");
		
		
		for(Element post : posts) {
			//Element channelDiv = row.select(".gr_row_head_chaine .link_to_station").first();
			Elements postRows = post.select("tr");
	
			//TODO, if postRows=0, older forum posts. Handle them separately
			
			Map<String, String> postDataMap = new HashMap<>();
			String mp3Url = "";
			
			
			boolean firstRow = true;
			
			String index = "";
			
			for(Element postRow : postRows) {
				Elements rowColumns = postRow.select("td");
				
				if(rowColumns.size() != 2) {
					continue;
				}
				
				if(firstRow) {
					
					index = rowColumns.get(0).text();
					firstRow = false;
					continue;
				}
				
				String key = rowColumns.get(0).text();
				String value = rowColumns.get(1).text();

				
				if("Direct music link:".equalsIgnoreCase(key)) {
					mp3Url = rowColumns.select("a").first().attr("href");
				}



				postDataMap.put(key, value);
				
			}
			
			if(mp3Url == null || !mp3Url.startsWith("http")) {//TODO Not a good check, should be more robust..
				continue;
			}
			
			RecitalSong song = new RecitalSong(index, postDataMap.get("Title of piece:"), mp3Url);
			
			song.setPerformerName(postDataMap.get("Performer's name:"));
			song.setPerformerCountry(postDataMap.get("From:"));
			song.setPerformerExperience(postDataMap.get("Experience:"));
			song.setComposer(postDataMap.get("Composer:"));
			song.setDuration(postDataMap.get("Duration:"));
			song.setSourceOfMusic(postDataMap.get("Source of music:"));
			song.setInstrumentUsed(postDataMap.get("Instrument used:"));
			song.setRecordingMethod(postDataMap.get("Recording method:"));
			song.setTechnicalFeedbackWanted(postDataMap.get("Technical feedback wanted:"));
			song.setAdditionalInfo(postDataMap.get("Additional info:"));
			song.setVideoLink(postDataMap.get("Video link:"));
			songs.add(song);
		}
		

		return songs;
	}
	


	/*
	02	 
Performer's name:	earlofmar
From:	Australia
Experience:	5.3 years
Direct music link:	click to download
Title of piece:	Bagatelle No 11, Op
Composer:	Beethoven
Duration:	01:35
Source of music:	Sheet Muisic
Instrument used:	Kawai K8
Recording method:	Zoom H4n
Technical feedback wanted:	Yes
Additional info:	This Bagatelle sounds simple enough but proved to be a huge technical challenge, perhaps why AMEB chose it as part of their syllabus. As well as the usual fingering tricks, (contortion really), Beethoven has played on me before, this time he also had me trilling while holding down another note. I thought he was just been mean, but then he had me do it again, this time trilling with the third and fourth fingers. This did not begin to feel comfortable for a very long time, and even now is far from effortless. However, the piece has come a long way since my early days with it when I would lift my hands between chords. My fantastic and patient teacher showed me, with only the aid of a pencil, how to play the chord sequences legato. Just a few months ago I played this piece as part of my AMEB exam. It probably was not much better on the day, but neither did it have the glaring mistake I have kept in for this recording. The examiner remarked there was elegantly shaped phrasing and the touch clear but not always precise. Thankfully she kept a record of the event because it is all just a blur to me.

	*/

}
