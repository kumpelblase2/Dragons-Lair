package de.kumpelblase2.dragonslair.logging;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;

public class BlockContentChangeEntry extends BlockEntry
{
	public BlockContentChangeEntry(final BlockState inState, final ActiveDungeon ad)
	{
		super(inState, ad);
		this.m_before = this.getOptions(inState);
	}

	public BlockContentChangeEntry(final String inDungeon, final int inParty, final Location inLoc, final Map<String, String> inBefore, final Map<String, String> inNew)
	{
		super(inDungeon, inParty, inLoc, inBefore, inNew);
	}

	@Override
	protected Map<String, String> getOptions(final BlockState inBroken)
	{
		final Map<String, String> options = new HashMap<String, String>();
		final ItemStack[] contents = ((InventoryHolder)inBroken).getInventory().getContents();
		for(int i = 0; i < contents.length; i++)
		{
			options.put("slot" + i, InventoryUtilities.itemToString(contents[i]));
		}

		return options;
	}

	@Override
	public LogType getType()
	{
		return LogType.BLOCK_CHANGE;
	}

	@Override
	public boolean isNegotiation(final Recoverable inEntry)
	{
		if(inEntry.getNewData().size() != this.getOldData().size())
			return false;

		for(final String key : this.getOldData().keySet())
		{
			if(!this.getOldData().get(key).equals(inEntry.getOldData().get(key)))
				return false;
		}

		return true;
	}

	@Override
	public void recover()
	{
		final InventoryHolder c = (InventoryHolder)this.m_loc.getBlock().getState();
		for(final String key : this.m_before.keySet())
		{
			if(key.startsWith("slot"))
			{
				final int index = Integer.parseInt(key.replace("slot", ""));
				c.getInventory().setItem(index, InventoryUtilities.stringToItem(this.m_before.get(key)));
			}
		}

		if(this.m_loc.getBlock().getState() instanceof Chest)
			((Chest)this.m_loc.getBlock().getState()).getBlockInventory().setContents(((Chest)this.m_loc.getBlock().getState()).getInventory().getContents());
	}

	@Override
	public void setNew()
	{
		final InventoryHolder c = (InventoryHolder)this.m_loc.getBlock().getState();
		for(final String key : this.m_new.keySet())
		{
			if(key.startsWith("slot"))
			{
				final int index = Integer.parseInt(key.replace("slot", ""));
				c.getInventory().setItem(index, InventoryUtilities.stringToItem(this.m_new.get(key)));
			}
		}

		if(this.m_loc.getBlock().getState() instanceof Chest)
			((Chest)this.m_loc.getBlock().getState()).getBlockInventory().setContents(((Chest)this.m_loc.getBlock().getState()).getInventory().getContents());
	}
}