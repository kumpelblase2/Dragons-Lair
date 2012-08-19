package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import de.kumpelblase2.dragonslair.*;

public class Party
{
	private int id;
	private String[] members;
	private int objectiveID;
	private int chapterID;
	private int dungeonID;
	
	public Party()
	{
		this.members = new String[0];
		this.id = -1;
	}
	
	public Party(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Parties.ID);
			String memberList = result.getString(TableColumns.Parties.MEMBERS);
			this.members = memberList.split(":");
			this.objectiveID = result.getInt(TableColumns.Parties.OBJECTIVE_ID);
			this.chapterID = result.getInt(TableColumns.Parties.CHAPTER_ID);
			this.dungeonID = result.getInt(TableColumns.Parties.DUNGEON_ID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public Party(String[] players, Dungeon d)
	{
		this.members = players;
		this.objectiveID = d.getStartingObjective().getID();
		this.chapterID = d.getStartingChapter().getID();
		this.dungeonID = d.getID();
		this.id = -1;
		this.save();
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public String[] getMembers()
	{
		return this.members;
	}
	
	public void setMembers(String[] inMembers)
	{
		this.members = inMembers;
	}
	
	public void setMember(String inName, int inNumber)
	{
		if(inNumber > 4 || inNumber < 1)
			return;
		
		this.members[inNumber - 1] = inName;
	}
	
	public String getMemberString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.members.length; i++)
		{
			sb.append(this.members[i]);
			if(i != this.members.length - 1)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public void setCurrentChapter(int id)
	{
		this.chapterID = id;
	}
	
	public void setCurrentObjective(int id)
	{
		this.objectiveID = id;
	}
	
	public Objective getCurrentObjective()
	{
		return DragonsLairMain.getSettings().getObjectives().get(this.objectiveID);
	}
	
	public Chapter getCurrentChapter()
	{
		return DragonsLairMain.getSettings().getChapters().get(this.chapterID);
	}
	
	public Dungeon getDungeon()
	{
		return DragonsLairMain.getSettings().getDungeons().get(this.dungeonID);
	}
	
	public void save()
	{
		try
		{
			String memberstring = this.getMemberString();
			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.PARTIES + "(" +
						"party_id," +
						"party_members," +
						"party_objective_id," +
						"party_chapter_id," +
						"party_dungeon_id" +
						") VALUES(?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, memberstring);
				st.setInt(3, this.objectiveID);
				st.setInt(4, this.chapterID);
				st.setInt(5, this.dungeonID);
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.PARTIES + "(" +
						"party_members," +
						"party_objective_id," +
						"party_chapter_id," +
						"party_dungeon_id" +
						") VALUES(?,?,?,?)");
				st.setString(1, memberstring);
				st.setInt(2, this.objectiveID);
				st.setInt(3, this.chapterID);
				st.setInt(4, this.dungeonID);
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save party " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
	
	public boolean hasPlayer(String player)
	{
		for(String p : this.members)
		{
			if(p.equals(player))
				return true;
		}
		return false;
	}
	
	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.PARTIES + " WHERE `party_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Party getPartyOfPlayers(String[] players, int dungeonid)
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("SELECT * FROM " + Tables.PARTIES + " WHERE (party_members = ?) AND (party_dungeon_id = ?)");
			String memberstring = "";
			for(int i = 0; i < players.length; i++)
			{
				memberstring += players[i] + ((i == players.length - 1) ? "" : ",");
			}
			st.setString(1, memberstring);
			st.setInt(2, dungeonid);
			ResultSet result = st.executeQuery();
			if(result == null || !result.next())
			{
				return new Party(players, DragonsLairMain.getSettings().getDungeons().get(dungeonid));
			}
			
			return new Party(result);
		}
		catch(Exception e)
		{
			return new Party(players, DragonsLairMain.getSettings().getDungeons().get(dungeonid));
		}
	}
}
