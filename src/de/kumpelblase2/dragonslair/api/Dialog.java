package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import java.util.*;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public class Dialog
{
	private int id;
	private String text;
	private Map<AnswerType, Integer> nextIDs = new HashMap<AnswerType, Integer>();
	
	public Dialog()
	{
		this.id = -1;
	}
	
	public Dialog(int inId, String inText, Map<AnswerType, Integer> inNext)
	{
		this.id = inId;
		this.text = inText;
		this.nextIDs = inNext;
	}
	
	public Dialog(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Dialogs.ID.ordinal());
			this.text = result.getString(TableColumns.Dialogs.TEXT.ordinal());
			this.nextIDs.put(AnswerType.AGREEMENT, result.getInt(TableColumns.Dialogs.AGREEMENT_ID.ordinal()));
			if(result.wasNull())
				this.nextIDs.remove(AnswerType.AGREEMENT);
			
			this.nextIDs.put(AnswerType.CONSIDERING_AGREEMENT, result.getInt(TableColumns.Dialogs.CONSIDER_AGREEMENT_ID.ordinal()));
			if(result.wasNull())
				this.nextIDs.remove(AnswerType.CONSIDERING_AGREEMENT);
			
			this.nextIDs.put(AnswerType.DISAGREEMENT, result.getInt(TableColumns.Dialogs.DISAGREEMENT_ID.ordinal()));
			if(result.wasNull())
				this.nextIDs.remove(AnswerType.DISAGREEMENT);
			
			this.nextIDs.put(AnswerType.CONSIDERING_DISAGREEMENT, result.getInt(TableColumns.Dialogs.CONSIDE_DISAGREEMENT_ID.ordinal()));
			if(result.wasNull())
				this.nextIDs.remove(AnswerType.CONSIDERING_DISAGREEMENT);
			
			this.nextIDs.put(AnswerType.CONSIDERING, result.getInt(TableColumns.Dialogs.CONSIDERING_ID.ordinal()));
			if(result.wasNull())
				this.nextIDs.remove(AnswerType.CONSIDERING);
			
			if(this.nextIDs.size() > 0)
				this.nextIDs.put(AnswerType.NOTHING, this.id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String inText)
	{
		this.text = inText;
	}
	
	public void setNextID(AnswerType inType, int inId)
	{
		this.nextIDs.put(inType, id);
	}
	
	public void setNextIDs(Map<AnswerType, Integer> inNext)
	{
		this.nextIDs = inNext;
	}
	
	public Map<AnswerType, Integer> getNextIDs()
	{
		return this.nextIDs;
	}
	
	public int getNextID(AnswerType inType)
	{
		return this.nextIDs.get(inType);
	}
	
	public DialogType getType()
	{
		if(this.nextIDs.size() > 0)
			return DialogType.QUESTION;
		else
			return DialogType.MESSAGE;
	}
	
	public void save()
	{
		try
		{			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.DIALOGS + "(" +
						"dialog_id," +
						"dialog_text," +
						"next_agreement_id," +
						"next_consider_agreement_id," +
						"next_disagreement_id," +
						"next_consider_disagreement_id," +
						"next_considering_id" +
						") VALUES(?,?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.text);
				st.setInt(3, this.nextIDs.get(AnswerType.AGREEMENT));
				st.setInt(4, this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT));
				st.setInt(5, this.nextIDs.get(AnswerType.DISAGREEMENT));
				st.setInt(6, this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT));
				st.setInt(7, this.nextIDs.get(AnswerType.CONSIDERING));
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.DIALOGS + "(" +
						"dialog_text," +
						"next_agreement_id," +
						"next_consider_agreement_id," +
						"next_disagreement_id," +
						"next_consider_disagreement_id," +
						"next_considering_id" +
						") VALUES(?,?,?,?,?,?)");
				st.setString(1, this.text);
				st.setInt(2, this.nextIDs.get(AnswerType.AGREEMENT));
				st.setInt(3, this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT));
				st.setInt(4, this.nextIDs.get(AnswerType.DISAGREEMENT));
				st.setInt(5, this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT));
				st.setInt(6, this.nextIDs.get(AnswerType.CONSIDERING));
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save dialog " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
