package com.mk.recitalplayer.RecitalPlayer;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "recital.player")
@Component
@Validated
public class RecitalPlayerConfigurationProperties {

	private @Getter Map<String, String[]> forumRecital = new HashMap<>();

	@NotNull
	private @Getter @Setter String defaultForumRecital;
	
}
