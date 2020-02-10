// prayeralert - Plugin for the OSRS RuneLite client to alert the player when their prayer is low.
// Copyright (C) 2019  lachlan-smith, mikkikur

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

package net.runelite.client.plugins.customboar;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Boar's Custom Plugins",
	tags = {"boar","custom"},
	type = PluginType.UTILITY,
	enabledByDefault = false
)
@Singleton
public class PrayerAlertPlugin extends Plugin
{

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private PrayerAlertOverlay overlay;
	@Inject
	private PrayerAlertConfig config;

	@Provides
	PrayerAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PrayerAlertConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("prayeralert")){}
	}

}
