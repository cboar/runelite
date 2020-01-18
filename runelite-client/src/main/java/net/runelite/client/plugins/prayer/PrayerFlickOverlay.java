/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.prayer;

import java.lang.Runtime;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;


@Singleton
class PrayerFlickOverlay extends Overlay
{
	private final Client client;
	private final PrayerPlugin plugin;
	private final TooltipManager tooltipManager;

	private boolean tickComplete = false;
	private double prevTick = 0.0;
	private Runtime rt;

	@Inject
	private PrayerFlickOverlay(Client client, PrayerPlugin plugin, final TooltipManager tooltipManager)
	{
		rt = Runtime.getRuntime();
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		this.client = client;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// If there are no prayers active or flick location is set to the prayer bar we don't require the flick helper
		/*if ((ticksWithPrayerOff > 50 && !plugin.isPrayerFlickAlwaysOn())
			|| plugin.getPrayerFlickLocation().equals(PrayerFlickLocation.NONE)
			|| plugin.getPrayerFlickLocation().equals(PrayerFlickLocation.PRAYER_BAR))
		{
			return null;
		}*/

		Widget xpOrb = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
		if (xpOrb == null)
		{
			return null;
		}

		Rectangle2D bounds = xpOrb.getBounds().getBounds2D();
		if (bounds.getX() <= 0)
		{
			return null;
		}

		double t = plugin.getTickProgress();
		if(t - prevTick < -1.0){
			tickComplete = false;
		}

		if(plugin.isPrayersActive()){
			//Purposefully using height twice here as the bounds of the prayer orb includes the number sticking out the side
			int orbInnerHeight = (int) bounds.getHeight();
			int orbInnerX = (int) (bounds.getX() + 24);//x pos of the inside of the prayer orb
			int orbInnerY = (int) (bounds.getY() - 1);//y pos of the inside of the prayer orb
			int xOffset = (int) (-Math.cos(t) * orbInnerHeight / 2) + orbInnerHeight / 2;
			int indicatorHeight = (int) (Math.sin(t) * orbInnerHeight);
			int yOffset = (orbInnerHeight / 2) - (indicatorHeight / 2);

			graphics.setColor(Color.cyan);
			graphics.fillRect(orbInnerX + xOffset, orbInnerY + yOffset, 1, indicatorHeight);
		}

		if(plugin.isPrayersActive() && !tickComplete && (
			(t > 0.6 && Math.random() < 0.70) ||
			(t > 1.0 && Math.random() < 0.90)
		)){
			try {
				boolean hoveringQuickPray = tooltipManager.getTooltips().get(0).getText().contains("Quick-prayers");
				if(hoveringQuickPray){
					tickComplete = true;
					rt.exec("/home/boar/code/rotbot/double-click");
				}
			} catch(Exception e){}
		}

		prevTick = t;
		return new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
	}
}
