package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;

public class NPC
{
	private int id;
	private String name;
	private String skin;
	private Location loc;
	private Material heldItem;
	private ItemStack[] armor;
	private boolean shouldSpawnAtBeginning;
	private boolean isInvincible;
	private RemoteEntityType type;

	public NPC()
	{
		this.id = -1;
		this.armor = new ItemStack[4];
		this.shouldSpawnAtBeginning = false;
		this.heldItem = Material.AIR;
		this.type = RemoteEntityType.Human;
	}

	public NPC(final ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.NPCs.ID);
			this.name = result.getString(TableColumns.NPCs.NAME);
			this.skin = result.getString(TableColumns.NPCs.SKIN);
			this.loc = WorldUtility.stringToLocation(result.getString(TableColumns.NPCs.LOCATION));
			this.heldItem = Material.getMaterial(result.getInt(TableColumns.NPCs.HELD_ITEM_ID));
			this.shouldSpawnAtBeginning = result.getBoolean(TableColumns.NPCs.SHOULD_SPAWN_AT_BEGINNING);
			this.armor = InventoryUtilities.stringToItems(result.getString(TableColumns.NPCs.ARMOR));
			this.isInvincible = result.getBoolean(TableColumns.NPCs.INVINCIBLE);
			this.type = RemoteEntityType.valueOf(result.getString(TableColumns.NPCs.TYPE));
		}
		catch(final SQLException e)
		{
			e.printStackTrace();
		}
	}

	public int getID()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String inName)
	{
		this.name = inName;
	}

	public String getSkin()
	{
		return this.skin;
	}

	public void setSkin(final String inSkin)
	{
		this.skin = inSkin;
	}

	public Location getLocation()
	{
		return this.loc;
	}

	public Material getHeldItem()
	{
		return this.heldItem;
	}

	public ItemStack[] getArmorParts()
	{
		return this.armor;
	}

	public String getWorld()
	{
		return this.loc.getWorld().getName();
	}

	public void setWorld(final String w)
	{
		this.loc.setWorld(Bukkit.getWorld(w));
	}

	public boolean shouldSpawnAtBeginning()
	{
		return this.shouldSpawnAtBeginning;
	}

	public boolean isInvincible()
	{
		return this.isInvincible;
	}

	public void setInvincible(final boolean invincible)
	{
		this.isInvincible = invincible;
	}

	public RemoteEntityType getType()
	{
		return this.type;
	}

	public void setType(RemoteEntityType inType)
	{
		this.type = inType;
	}

	public void save()
	{
		try
		{
			if(this.id != -1)
			{
				final PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.NPCS + "(" + "npc_id," + "npc_name," + "npc_skin," + "npc_location," + "npc_held_item," + "npc_armor," + "npc_spawned_from_beginning," + "npc_invincible," + "npc_type" + ") VALUES(?,?,?,?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.name);
				st.setString(3, this.skin);
				st.setString(4, WorldUtility.locationToString(this.loc));
				st.setInt(5, this.heldItem.getId());
				st.setString(6, InventoryUtilities.itemsToString(this.armor));
				st.setBoolean(7, this.shouldSpawnAtBeginning);
				st.setBoolean(8, this.isInvincible);
				st.setString(9, this.type.name());
				st.execute();
			}
			else
			{
				final PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.NPCS + "(" + "npc_name," + "npc_skin," + "npc_location," + "npc_held_item," + "npc_armor," + "npc_spawned_from_beginning," + "npc_invincible," + "npc_type" + ") VALUES(?,?,?,?,?,?,?,?)");
				st.setString(1, this.name);
				st.setString(2, this.skin);
				st.setString(3, WorldUtility.locationToString(this.loc));
				st.setInt(4, this.heldItem.getId());
				st.setString(5, InventoryUtilities.itemsToString(this.armor));
				st.setBoolean(6, this.shouldSpawnAtBeginning);
				st.setBoolean(7, this.isInvincible);
				st.setString(8, this.type.name());
				st.execute();
				final ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save npc " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void setLocation(final Location location)
	{
		this.loc = location;
	}

	public void setArmor(final ItemStack[] itemStacks)
	{
		this.armor = itemStacks;
	}

	public void setHeldItem(final Material mat)
	{
		this.heldItem = mat;
	}

	public void shouldSpawnAtBeginning(final boolean should)
	{
		this.shouldSpawnAtBeginning = should;
	}

	public void remove()
	{
		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.NPCS + " WHERE `npc_id` = ?");
			st.setInt(1, this.getID());
			st.execute();
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove npc from database: " + e.getMessage());
		}
	}
}