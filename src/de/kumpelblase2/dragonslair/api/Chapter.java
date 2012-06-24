package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import de.kumpelblase2.dragonslair.*;

public class Chapter
{
	private int id;
	private String name;
	
	public Chapter()
	{
		this.id = -1;
	}
	
	public Chapter(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Chapters.ID.ordinal());
			this.name = result.getString(TableColumns.Chapters.NAME.ordinal());
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
	
	public void setName(String newname)
	{
		this.name = newname;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void save()
	{
		try
		{			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.CHAPTERS + "(" +
						"chapter_id," +
						"chapter_name" +
						") VALUES(?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.name);
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.CHAPTERS + "(" +
						"chapter_name" +
						") VALUES(?)");
				st.setString(1, this.name);
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save chapter " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("REMOVE FROM " + Tables.CHAPTERS + " WHERE `chapter_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove chapter " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
