package com.vanillj.xptolevelnotification;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(XpToLevelNotificationConfig.GROUP)
public interface XpToLevelNotificationConfig extends Config
{
	String GROUP = "xptolevelnotification";

	@Range(min = 1)
	@ConfigItem(
			keyName = "xpThreshold",
			name = "XP Threshold",
			description = "When to trigger the XP notification.",
			position = 0
	)
	default int xpThreshold()
	{
		return 5000;
	}

	@Range(min = 0)
	@Units(Units.MINUTES)
	@ConfigItem(
			keyName = "xpDelay",
			name = "Notification delay",
			description = "The delay until a notification can be sent again for a skill. This prevents spamming.",
			position = 1
	)
	default int xpNotificationDelay()
	{
		return 5;
	}
}
