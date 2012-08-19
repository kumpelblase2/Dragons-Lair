package de.kumpelblase2.dragonslair.api;

import java.sql.PreparedStatement;
import org.bukkit.Location;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class DeathLocation
{
	private Location deathLocation;
	private int partyID;
	private String player;
	
	public DeathLocation(String player, Location death, int party)
	{
		this.deathLocation = death;
		this.player = player;
		this.partyID = party;
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
	
	public void save()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO `death_locations` VALUES(?,?,?)");
			st.setString(1, this.player);
			st.setInt(2, this.partyID);
			st.setString(3, WorldUtility.locationToString(this.deathLocation));
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save death location for player " + this.player);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
