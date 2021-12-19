package com.xptolevelnotification;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
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
		// 5 tick delay to prevent login stat changes
		if (config.xpEnable() && client.getTickCount() > 5)
		{
			final String skillName = statChanged.getSkill().getName();
			final int currentXp = statChanged.getXp();
			final int currentLevel = statChanged.getLevel();
			final int xpNextLevel = currentLevel + 1 <= Experience.MAX_VIRT_LEVEL ? Experience.getXpForLevel(currentLevel + 1) : Experience.MAX_SKILL_XP;

			int xpDelta = xpNextLevel - currentXp;
			Instant skillInstant = skillDelay.get(skillName);

			if (xpDelta < xpThreshold)
			{
				if (skillInstant == null || Instant.now().isAfter(skillInstant))
				{
					skillDelay.put(skillName, Instant.now().plusSeconds(notificationDelay));
				}
				else
				{
					return;
				}

				notifier.notify("XP left to level: " + xpDelta + " in " + skillName);
			}
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
