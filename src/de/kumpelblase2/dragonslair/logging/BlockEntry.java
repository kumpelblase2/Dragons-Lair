package de.kumpelblase2.dragonslair.logging;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.Tables;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class BlockEntry implements Recoverable
{
	protected Map<String, String> m_before = new HashMap<String, String>();
	protected final Location m_loc;
	protected final String m_dungeon;
	protected final int m_party;
	protected Map<String, String> m_new = new HashMap<String, String>();

	public BlockEntry(final BlockState inState, final ActiveDungeon ad)
	{
		this.m_dungeon = ad.getInfo().getName();
		this.m_party = ad.getCurrentParty().getID();
		this.m_loc = inState.getLocation();
		this.m_before = this.getOptions(inState);
		this.m_new = this.getOptions(this.m_loc.getBlock().getState());
	}

	public BlockEntry(final String inDungeon, final int inParty, final Location inLoc, final Map<String, String> inBefore, final Map<String, String> inNew)
	{
		this.m_dungeon = inDungeon;
		this.m_party = inParty;
		this.m_loc = inLoc;
		this.m_before = inBefore;
		this.m_new = inNew;
	}

	@Override
	public void recover()
	{
		this.restoreState(this.m_before);
	}

	@Override
	public void save()
	{
		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement(LoggingManager.logQuery);
			st.setString(1, this.m_dungeon);
			st.setInt(2, this.m_party);
			st.setString(3, this.getType().toString().toLowerCase());
			st.setString(4, WorldUtility.locationToString(this.m_loc));
			StringBuilder sb = new StringBuilder();
			for(final String key : this.m_before.keySet())
			{
				sb.append(key + (0x1D) + this.m_before.get(key) + ";");
			}

			if(sb.length() > 1)
				sb.substring(0, sb.length() - 1);

			st.setString(5, sb.toString());
			sb = new StringBuilder();
			for(final String key : this.m_new.keySet())
			{
				sb.append(key + (0x1D) + this.m_new.get(key) + ";");
			}

			if(sb.length() > 1)
				sb.substring(0, sb.length() - 1);

			st.setString(6, sb.toString());
			st.execute();
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to log BlockEntry: " + e.getMessage());
		}
	}

	@Override
	public void remove()
	{
		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.LOG + " WHERE `dungeon_name` = ? AND `location` = ? AND `party_id` = ?");
			st.setString(1, this.m_dungeon);
			st.setString(2, WorldUtility.locationToString(this.m_loc));
			st.setInt(3, this.m_party);
			st.execute();
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove log entry: " + e.getMessage());
		}
	}

	@Override
	public void setNew()
	{
		this.restoreState(this.m_new);
	}

	@Override
	public LogType getType()
	{
		return null;
	}

	@Override
	public Map<String, String> getOldData()
	{
		return this.m_before;
	}

	@Override
	public void setOldData(final Map<String, String> inOld)
	{
		this.m_before = inOld;
	}

	protected Map<String, String> getOptions(final BlockState inBroken)
	{
		final Map<String, String> options = new HashMap<String, String>();
		options.put("data", "" + inBroken.getData().getData());
		options.put("block_type", inBroken.getTypeId() + "");
		if(inBroken instanceof Sign)
		{
			final Sign s = (Sign)inBroken;
			for(int i = 0; i < s.getLines().length; i++)
			{
				options.put("line" + i, s.getLine(i));
			}
		}
		else if(inBroken instanceof Chest)
		{
			final Chest c = (Chest)inBroken;
			final String itemString = InventoryUtilities.itemsToString(c.getBlockInventory().getContents());
			final String[] split = itemString.split(";");
			for(int i = 0; i < split.length; i++)
			{
				if(!split[i].equals("0:0:0"))
					options.put("slot" + i, split[i]);
			}
		}
		else if(inBroken instanceof BrewingStand)
		{
			final BrewingStand b = (BrewingStand)inBroken;
			options.put("brewing_time", "" + b.getBrewingTime());
			final String itemString = InventoryUtilities.itemsToString(b.getInventory().getContents());
			final String[] split = itemString.split(";");
			for(int i = 0; i < split.length; i++)
			{
				if(!split[i].equals("0:0:0"))
					options.put("slot" + i, split[i]);
			}
		}
		else if(inBroken instanceof Dispenser)
		{
			final Dispenser d = (Dispenser)inBroken;
			final String itemString = InventoryUtilities.itemsToString(d.getInventory().getContents());
			final String[] split = itemString.split(";");
			for(int i = 0; i < split.length; i++)
			{
				if(!split[i].equals("0:0:0"))
					options.put("slot" + i, split[i]);
			}
		}
		else if(inBroken instanceof Furnace)
		{
			final Furnace f = (Furnace)inBroken;
			options.put("burn_time", f.getBurnTime() + "");
			options.put("cooking_time", f.getCookTime() + "");
			final String itemString = InventoryUtilities.itemsToString(f.getInventory().getContents());
			final String[] split = itemString.split(";");
			for(int i = 0; i < split.length; i++)
			{
				if(!split[i].equals("0:0:0"))
					options.put("slot" + i, split[i]);
			}
		}
		else if(inBroken instanceof Jukebox)
		{
			final Jukebox j = (Jukebox)inBroken;
			options.put("playing", j.getPlaying().toString());
		}
		else if(inBroken instanceof NoteBlock)
		{
			final NoteBlock n = (NoteBlock)inBroken;
			options.put("note", "" + n.getRawNote());
		}

		return options;
	}

	protected void restoreState(final Map<String, String> options)
	{
		final int id = Integer.parseInt(options.get("block_type"));
		final byte data = Byte.parseByte(options.get("data"));
		if(this.m_loc.getBlock().getState() instanceof InventoryHolder)
			((InventoryHolder)this.m_loc.getBlock().getState()).getInventory().clear();

		this.m_loc.getBlock().setTypeIdAndData(id, data, true);
		this.m_loc.getBlock().getState().update();
		final BlockState inState = this.m_loc.getBlock().getState();
		final Material m = Material.getMaterial(id);
		switch(m)
		{
			case WALL_SIGN:
			case SIGN_POST:
				final Sign s = (Sign)inState;
				s.setLine(0, options.get("line0"));
				s.setLine(1, options.get("line1"));
				s.setLine(2, options.get("line2"));
				s.setLine(3, options.get("line3"));
				break;
			case CHEST:
				final Chest c = (Chest)inState;
				for(final String key : options.keySet())
				{
					if(key.startsWith("slot"))
					{
						final int index = Integer.parseInt(key.replace("slot", ""));
						c.getBlockInventory().setItem(index, InventoryUtilities.stringToItem(options.get(key)));
					}
				}

				break;
			case BREWING_STAND:
				final BrewingStand b = (BrewingStand)inState;
				b.setBrewingTime(Integer.parseInt(options.get("brewing_time")));
				for(final String key : options.keySet())
				{
					if(key.startsWith("slot"))
					{
						final int index = Integer.parseInt(key.replace("slot", ""));
						b.getInventory().setItem(index, InventoryUtilities.stringToItem(options.get(key)));
					}
				}

				break;
			case DISPENSER:
				final Dispenser d = (Dispenser)inState;
				for(final String key : options.keySet())
				{
					if(key.startsWith("slot"))
					{
						final int index = Integer.parseInt(key.replace("slot", ""));
						d.getInventory().setItem(index, InventoryUtilities.stringToItem(options.get(key)));
					}
				}

				break;
			case FURNACE:
				final Furnace f = (Furnace)inState;
				f.setBurnTime(Short.parseShort(options.get("burn_time")));
				f.setCookTime(Short.parseShort(options.get("cooking_time")));
				for(final String key : options.keySet())
				{
					if(key.startsWith("slot"))
					{
						final int index = Integer.parseInt(key.replace("slot", ""));
						f.getInventory().setItem(index, InventoryUtilities.stringToItem(options.get(key)));
					}
				}

				break;
			case JUKEBOX:
				final Jukebox j = (Jukebox)inState;
				j.setPlaying(Material.getMaterial(options.get("playing")));
				break;
			case NOTE_BLOCK:
				final NoteBlock n = (NoteBlock)inState;
				n.setRawNote(Byte.parseByte(options.get("note")));
				break;
			default:
				break;
		}

		inState.update();
	}

	@Override
	public boolean isNegotiation(final Recoverable inEntry)
	{
		return false;
	}

	@Override
	public Map<String, String> getNewData()
	{
		return this.m_new;
	}

	@Override
	public void setNewData(final Map<String, String> inNew)
	{
		this.m_new = inNew;
	}

	@Override
	public Location getLocation()
	{
		return this.m_loc;
	}
}