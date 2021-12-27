package com.vanillj.xptolevelnotification;

import com.vanillj.xptolevelnotification.XpToLevelNotificationPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class XpToLevelNotificationPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(XpToLevelNotificationPlugin.class);
		RuneLite.main(args);
	}
}