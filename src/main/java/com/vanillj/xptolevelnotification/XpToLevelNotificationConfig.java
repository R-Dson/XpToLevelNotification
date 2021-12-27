package com.vanillj.xptolevelnotification;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(XpToLevelNotificationConfig.GROUP)
public interface XpToLevelNotificationConfig extends Config
{
	String GROUP = "xptolevelnotification";

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

	@ConfigItem(
			keyName = "xpDelay",
			name = "Notification delay (minutes)",
			description = "The delay until a notification can be sent again for a skill. This prevents spamming.",
			position = 1
	)
	default int xpNotificationDelay()
	{
		return 5;
	}
}
