package com.mk.recitalplayer.RecitalPlayer;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.Data;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class RecitalIndexParser {

	@Test
	public void parseRecitalIndex() throws Exception {

		String recitalsIndexUrl = "http://recitals.pianoworld.com/wiki/index.php/Category:Recitals";

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = template.getForEntity(recitalsIndexUrl, String.class);

		Document doc = Jsoup.parse(response.getBody());
		
		//"CategoryTreeLabel  
		
		List<RecitalUrl> recitals = new ArrayList<>();
		
		Elements recitalsLinks = doc.select(".CategoryTreeLabel");
		
		for(Element recitalLink : recitalsLinks) {
			
			System.out.println("Parsing recital " + recitalLink);
			
			String link = recitalLink.attr("href");
			String name = recitalLink.text();
			
			recitals.add(parseRecitalIndex(name, link));
			
		}
		
		//recital.player.forumRecital.42=
		Collections.reverse(recitals);
		for(RecitalUrl recital : recitals) {
			System.out.println("recital.player.forumRecital." + recital.getName().replaceAll("\\s+","") + "=" + recital.getUrl().replace(",", "%2C"));
		}

	}
	
	private RecitalUrl parseRecitalIndex(String recitalName, String recitalWikiLink) {
		
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = template.getForEntity("http://recitals.pianoworld.com/" + recitalWikiLink, String.class);

		
		Document doc = Jsoup.parse(response.getBody());
		
		Elements listItems = doc.select("li");
		
		for(Element listItem : listItems) {
			
			if(listItem.toString().contains("Recital at Piano World")) {
				
				Element a = listItem.select("a").first();
				String url = a.attr("href");
				return new RecitalUrl(recitalName, url);
			}
		}
		
		return null;
		
	}
	
	@AllArgsConstructor
	@Data
	private final class RecitalUrl {
		private String name;
		private String url;
	}

}
