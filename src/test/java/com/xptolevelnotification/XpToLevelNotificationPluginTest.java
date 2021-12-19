package com.xptolevelnotification;

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