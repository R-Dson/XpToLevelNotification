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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.time.Instant;
import java.util.HashMap;

@Slf4j
@PluginDescriptor(
		name = "Xp To Level Notification"
)
public class XpToLevelNotificationPlugin extends Plugin
{
	private int xpThreshold;
	private int notificationDelay;
	private HashMap<String, Instant> skillDelay;

	@Inject
	private Client client;

	@Inject
	private XpToLevelNotificationConfig config;

	@Inject
	private Notifier notifier;

	@Override
	protected void startUp() throws Exception
	{
		xpThreshold = config.xpThreshold();
		notificationDelay = 60 * config.xpNotificationDelay();
		skillDelay = new HashMap<>();
	}

	@Override
	protected void shutDown() throws Exception
	{

	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final String skillName = statChanged.getSkill().getName();

		if (!skillDelay.containsKey(skillName))
		{
			skillDelay.put(skillName, Instant.now().plusSeconds(-1));
			return;
		}

		final int currentXp = statChanged.getXp();
		final int currentLevel = Experience.getLevelForXp(currentXp);
		final int xpNextLevel = currentLevel + 1 <= Experience.MAX_VIRT_LEVEL ? Experience.getXpForLevel(currentLevel + 1) : Experience.MAX_SKILL_XP;

		final int xpDelta = xpNextLevel - currentXp;

		if (Instant.now().isBefore(skillDelay.get(skillName)))
		{
			return;
		}

		if (xpDelta < xpThreshold)
		{
			skillDelay.put(skillName, Instant.now().plusSeconds(notificationDelay));
			log.debug("Next notification time: "+ Instant.now().plusSeconds(notificationDelay).toString());
			notifier.notify("XP left to level: " + xpDelta + " in " + skillName);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(XpToLevelNotificationConfig.GROUP))
		{
			return;
		}

		if (config.xpThreshold() != xpThreshold)
		{
			xpThreshold = config.xpThreshold();
		}

		if (config.xpNotificationDelay() != notificationDelay)
		{
			notificationDelay = 60 * config.xpNotificationDelay();
			skillDelay.clear();
		}
	}

	@Provides
	XpToLevelNotificationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XpToLevelNotificationConfig.class);
	}
}
