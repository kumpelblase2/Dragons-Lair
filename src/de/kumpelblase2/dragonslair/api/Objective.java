package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import de.kumpelblase2.dragonslair.*;

public class Objective
{
	private int id;
	private String description;
	
	public Objective()
	{
		this.id = -1;
		this.description = "";
	}
	
	public Objective(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Objectives.ID.ordinal());
			this.description = result.getString(TableColumns.Objectives.DESCRIPTION.ordinal());
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
	
	public void setDescription(String inDesc)
	{
		this.description = inDesc;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void save()
	{
		try
		{			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.OBJECTIVES + "(" +
						"objective_id," +
						"objective_description" +
						") VALUES(?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.description);
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.OBJECTIVES + "(" +
						"objective_description" +
						") VALUES(?)");
				st.setString(1, this.description);
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save objective " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("REMOVE FROM " + Tables.OBJECTIVES + " WHERE `objective_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove objective " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
