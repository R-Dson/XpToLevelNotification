package com.vanillj.xptolevelnotification;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
		name = "Xp To Level Notification"
)
public class XpToLevelNotificationPlugin extends Plugin
{
	private final Map<String, Instant> skillDelay = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private XpToLevelNotificationConfig config;

	@Inject
	private Notifier notifier;

	@Override
	protected void startUp()
	{
		skillDelay.clear();
	}

	@Override
	protected void shutDown()
	{
		skillDelay.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			skillDelay.clear();
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final int currentXp = statChanged.getXp();

		if (currentXp >= Experience.MAX_SKILL_XP)
		{
			return;
		}

		final String skillName = statChanged.getSkill().getName();

		Instant nextNotification = skillDelay.putIfAbsent(skillName, Instant.EPOCH);
		if (nextNotification == null)
		{
			return;
		}

		if (Instant.now().isBefore(nextNotification))
		{
			return;
		}

		final int currentLevel = Experience.getLevelForXp(currentXp);
		final int xpNextLevel = currentLevel + 1 <= Experience.MAX_VIRT_LEVEL 
				? Experience.getXpForLevel(currentLevel + 1) 
				: Experience.MAX_SKILL_XP;

		final int xpDelta = xpNextLevel - currentXp;

		if (xpDelta <= 0)
		{
			return;
		}

		if (xpDelta < config.xpThreshold())
		{
			int delaySeconds = 60 * config.xpNotificationDelay();
			Instant newNotificationTime = Instant.now().plusSeconds(delaySeconds);
			
			skillDelay.put(skillName, newNotificationTime);
			
			log.debug("Next notification time: {}", newNotificationTime);
			notifier.notify("XP left to level: " + xpDelta + " in " + skillName);
		}
	}

	@Provides
	XpToLevelNotificationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XpToLevelNotificationConfig.class);
	}
}
