package de.kumpelblase2.dragonslair.map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class DLMapRenderer extends MapRenderer
{
	private int rendercall = 0;

	@Override
	public void render(final MapView view, final MapCanvas canvas, final Player player)
	{
		this.rendercall++;
		if(this.rendercall < 20)
			return;
		this.rendercall = 0;
		if(player.getItemInHand() == null || player.getItemInHand().getType() != Material.MAP)
			return;
		final ItemStack mapstack = player.getItemInHand();
		if(!mapstack.getEnchantments().containsKey(Enchantment.ARROW_INFINITE))
			return;
		if(DragonsLairMain.getDungeonManager().getDungeonOfPlayer(player.getName()) == null)
			return;
		final DLMap map = DragonsLairMain.getDungeonManager().getMapOfPlayer(player);
		if(map == null)
			return;
		map.checkUpdate();
		if(map.isRendered())
			return;
		DLMapRenderer.clear(canvas);
		final String[] text = map.getSplittedText(map.getTitle() + "////" + map.getText().replace("#player#", player.getName()));
		int maxLine = text.length;
		if(maxLine > map.maxLinesPerMap)
			maxLine = map.getCurrentLine() + map.maxLinesPerMap;
		if(maxLine > text.length)
			maxLine = text.length;
		int currentRowPos = 3;
		for(int i = map.getCurrentLine(); i < maxLine; i++)
		{
			canvas.drawText((128 / 2) - (MinecraftFont.Font.getWidth(text[i]) / 2) - 10, currentRowPos, MinecraftFont.Font, text[i]);
			currentRowPos += map.lineHeight;
		}
		player.sendMap(view);
		map.setRendered(true);
	}

	public static void clear(final MapCanvas canvas)
	{
		for(int i = 0; i < 128; i++)
			for(int i2 = 0; i2 < 128; i2++)
				canvas.setPixel(i, i2, (byte)0);
	}
}