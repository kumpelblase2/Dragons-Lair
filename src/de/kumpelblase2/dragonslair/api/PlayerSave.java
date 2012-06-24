package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class PlayerSave
{
	private Player player;
	private int partyid;
	
	public PlayerSave()
	{
	}
	
	public PlayerSave(Player p, Party pa)
	{
		this.player = p;
		this.partyid = pa.getID();
	}
	
	public PlayerSave(Player p, int paid)
	{
		this.player = p;
		this.partyid = paid;
	}
	
	public void save()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.PLAYER_SAVES + "(" +
					"player_name," +
					"player_armor," +
					"player_items," +
					"player_health," +
					"player_hunger," +
					"player_location," +
					"player_party_id" +
					") VALUES(?,?,?,?,?,?,?)");
			st.setString(1, this.player.getName());
			st.setString(2, InventoryUtilities.armorToString(this.player));
			st.setString(3, InventoryUtilities.inventoryToString(this.player));
			st.setInt(4, this.player.getHealth());
			st.setInt(5, this.player.getFoodLevel());
			st.setString(6, WorldUtility.locationToString(this.player.getLocation()));
			st.setInt(7, this.partyid);
			st.executeUpdate();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save info for player " + this.player.getName() + ".");
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public boolean restore()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("SELECT * FROM " + Tables.PLAYER_SAVES + " WHERE (`player_name` = ?) AND (`player_party_id` = ?)");
			st.setString(1, this.player.getName());
			st.setInt(2, this.partyid);
			ResultSet result = st.executeQuery();
			if(result == null || !result.next())
				return false;			
			
			this.player.setHealth(result.getInt(TableColumns.Player_Saves.HEALTH.ordinal()));
			this.player.setFoodLevel(result.getInt(TableColumns.Player_Saves.HUNGER.ordinal()));
			this.player.getInventory().setArmorContents(InventoryUtilities.stringToItems(result.getString(TableColumns.Player_Saves.ARMOR.ordinal())));
			this.player.getInventory().setContents(InventoryUtilities.stringToItems(result.getString(TableColumns.Player_Saves.ITEMS.ordinal())));
			this.player.teleport(WorldUtility.stringToLocation(result.getString(TableColumns.Player_Saves.LOCATION.ordinal())));
			return true;
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to parse save state for player " + this.player.getName());
			DragonsLairMain.Log.warning(e.getMessage());
			return false;
		}
	}
	
	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.PLAYER_SAVES + " WHERE (player_name = ?) AND (player_party_id = ?)");
			st.setString(1, this.player.getName());
			st.setInt(2, this.partyid);
			st.executeUpdate();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove player save for player: " + this.player.getName());
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
