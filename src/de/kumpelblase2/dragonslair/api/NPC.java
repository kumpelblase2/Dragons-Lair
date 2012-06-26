package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.utilities.*;

public class NPC
{
	private int id;
	private String name;
	private String skin; // does not work at the moment
	private Location loc;
	private Material heldItem;
	private ItemStack[] armor;
	private boolean shoudSpawnAtBeginning;
	private boolean isInvincible;
	
	public NPC()
	{
		this.id = -1;
		this.armor = new ItemStack[4];
		this.shoudSpawnAtBeginning = false;
		this.heldItem = Material.AIR;
	}
	
	public NPC(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.NPCs.ID.ordinal());
			this.name = result.getString(TableColumns.NPCs.NAME.ordinal());
			this.skin = result.getString(TableColumns.NPCs.SKIN.ordinal());
			this.loc = WorldUtility.stringToLocation(result.getString(TableColumns.NPCs.LOCATION.ordinal()));
			this.heldItem = Material.getMaterial(result.getInt(TableColumns.NPCs.HELD_ITEM_ID.ordinal()));
			this.shoudSpawnAtBeginning = result.getBoolean(TableColumns.NPCs.SHOULD_SPAWN_AT_BEGINNING.ordinal());
			this.armor = InventoryUtilities.stringToItems(result.getString(TableColumns.NPCs.ARMOR.ordinal()));
			this.isInvincible = result.getBoolean(TableColumns.NPCs.INVINCIBLE.ordinal());
		}
		catch (SQLException e)
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
	
	public void setName(String inName)
	{
		this.name = inName;
	}
	
	public String getSkin()
	{
		return this.skin;
	}
	
	public void setSkin(String inSkin)
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
	
	public void setWorld(String w)
	{
		this.loc.setWorld(Bukkit.getWorld(w));
	}
	
	public boolean shouldSpawnAtBeginning()
	{
		return this.shoudSpawnAtBeginning;
	}
	
	public boolean isInvincible()
	{
		return this.isInvincible;
	}
	
	public void setInvincible(boolean invincible)
	{
		this.isInvincible = invincible;
	}
	
	public void save()
	{
		try
		{			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.NPCS + "(" +
						"npc_id," +
						"npc_name," +
						"npc_skin," +
						"npc_location," +
						"npc_held_item," +
						"npc_armor," +
						"npc_spawned_from_beginning," +
						"npc_invincible" +
						") VALUES(?,?,?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.name);
				st.setString(3, this.skin);
				st.setString(4, WorldUtility.locationToString(this.loc));
				st.setInt(5, this.heldItem.getId());
				st.setString(6, InventoryUtilities.itemsToString(this.armor));
				st.setBoolean(7, this.shoudSpawnAtBeginning);
				st.setBoolean(8, this.isInvincible);
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.NPCS + "(" +
						"npc_name," +
						"npc_skin," +
						"npc_location," +
						"npc_held_item," +
						"npc_armor," +
						"npc_spawned_from_beginning," +
						"npc_invincible" +
						") VALUES(?,?,?,?,?,?,?)");
				st.setString(1, this.name);
				st.setString(2, this.skin);
				st.setString(3, WorldUtility.locationToString(this.loc));
				st.setInt(4, this.heldItem.getId());
				st.setString(5, InventoryUtilities.itemsToString(this.armor));
				st.setBoolean(6, this.shoudSpawnAtBeginning);
				st.setBoolean(7, this.isInvincible);
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save npc " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void setLocation(Location location)
	{
		this.loc = location;
	}

	public void setArmor(ItemStack[] itemStacks)
	{
		this.armor = itemStacks;
	}

	public void setHeldItem(Material mat)
	{
		this.heldItem = mat;
	}
	
	public void shouldSpawnAtBeginning(boolean should)
	{
		this.shoudSpawnAtBeginning = should;
	}
	
	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.NPCS + " WHERE `npc_id` = ?");
			st.setInt(1, this.getID());
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove npc from database: " + e.getMessage());
		}
	}
}