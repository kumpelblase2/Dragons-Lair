package de.kumpelblase2.dragonslair.api;

import java.sql.PreparedStatement;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class DeathLocation
{
	private Location deathLocation;
	private int partyID;
	private String player;
	private ItemStack[] armor;
	private ItemStack[] inventory;
	
	public DeathLocation(String player, Location death, int party)
	{
		this.deathLocation = death;
		this.player = player;
		this.partyID = party;
		this.armor = new ItemStack[0];
		this.inventory = new ItemStack[0];
	}
	
	public DeathLocation(String player, Location death, int party, ItemStack[] armor, ItemStack[] inv)
	{
		this(player, death, party);
		this.armor = armor;
		this.inventory = inv;
	}
	
	public Location getDeathLocation()
	{
		return this.deathLocation;
	}
	
	public String getPlayer()
	{
		return this.player;
	}
	
	public int getPartyID()
	{
		return this.partyID;
	}
	
	public ItemStack[] getArmor()
	{
		return this.armor;
	}
	
	public ItemStack[] getInventory()
	{
		return this.inventory;
	}
	
	public void save()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO `death_locations` VALUES(?,?,?,?,?)");
			st.setString(1, this.player);
			st.setInt(2, this.partyID);
			st.setString(3, WorldUtility.locationToString(this.deathLocation));
			st.setString(4, InventoryUtilities.itemsToString(this.armor));
			st.setString(5, InventoryUtilities.itemsToString(this.inventory));
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save death location for player " + this.player);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
