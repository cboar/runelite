package net.runelite.client.plugins.customboar;

import java.lang.Runtime;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Singleton
class CustomBoarOverlay extends Overlay {

	private final Client client;
	private final CustomBoarPlugin plugin;
	private final ItemManager itemManager;
	private final PanelComponent panelComponent = new PanelComponent();

	private Runtime rt;
	private long soundTime = 0;

	@Inject
	private CustomBoarOverlay(final Client client, final CustomBoarPlugin plugin, final ItemManager itemManager){
		rt = Runtime.getRuntime();
		setPosition(OverlayPosition.TOP_RIGHT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.plugin = plugin;
		this.itemManager = itemManager;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
    panelComponent.getChildren().clear();

		int health = Stats.HITPOINTS.getValue(client);
		int prayer = Stats.PRAYER.getValue(client);
		boolean boosted  = Stats.ATTACK.getValue(client) > Stats.ATTACK.getMaximum(client);

    if(isInNightmareZone()){

      if(!boosted)
        playSound("bell");

    } else if(isInLithkren()){

      if(health <= 36 || prayer <= 8)
        playSound("alert");
      if(!boosted)
        superCombatPotion(graphics);

    }

    return panelComponent.render(graphics);
	}

	private void playSound(String name){
    long currentTime = System.currentTimeMillis();
    if(currentTime - soundTime < 3000)
      return;
		try {
      rt.exec("ffplay -nodisp -autoexit /home/boar/.osrs/"+name+".mp3");
      soundTime = currentTime;
		} catch(Exception e){}
	}

	private void superCombatPotion(Graphics2D graphics){
		ImageComponent component = new ImageComponent(itemManager.getImage(ItemID.SUPER_COMBAT_POTION));
		panelComponent.getChildren().add(component);
		panelComponent.getChildren().add(
      TitleComponent.builder().text("BOOST").color(Color.RED).build()
    );
		panelComponent.setPreferredSize(
      new Dimension(graphics.getFontMetrics().stringWidth("BOOST") + 14, 0)
    );
	}

	private static final int[] MR_LITHKREN = {5966,5967,6222,6223,6478,6479};
  private boolean isInLithkren(){
		return Arrays.equals(client.getMapRegions(), MR_LITHKREN);
  }

	private static final int[] MR_NMZ = {9033};
	private boolean isInNightmareZone(){
		return Arrays.equals(client.getMapRegions(), MR_NMZ);
	}

  private static final int[] PRAYER_POTIONS = {
    ItemID.PRAYER_POTION1, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION4,
    ItemID.SUPER_RESTORE1, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE4
  };
	private boolean hasPrayerPotion(){
		Item[] inventoryItems = client.getItemContainer(InventoryID.INVENTORY).getItems();
    for(Item item : inventoryItems){
      for(int id : PRAYER_POTIONS){
        if(item.getId() == id)
          return true;
      }
    }
		return false;
	}
}
