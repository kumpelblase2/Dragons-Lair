package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
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

	public Dialog(final int inId, final String inText, final Map<AnswerType, Integer> inNext)
	{
		this.id = inId;
		this.text = inText;
		this.nextIDs = inNext;
	}

	public Dialog(final ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Dialogs.ID);
			this.text = result.getString(TableColumns.Dialogs.TEXT);
			this.nextIDs.put(AnswerType.AGREEMENT, result.getInt(TableColumns.Dialogs.AGREEMENT_ID));
			if(result.wasNull() || this.nextIDs.get(AnswerType.AGREEMENT) == 0)
				this.nextIDs.remove(AnswerType.AGREEMENT);
			this.nextIDs.put(AnswerType.CONSIDERING_AGREEMENT, result.getInt(TableColumns.Dialogs.CONSIDER_AGREEMENT_ID));
			if(result.wasNull() || this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT) == 0)
				this.nextIDs.remove(AnswerType.CONSIDERING_AGREEMENT);
			this.nextIDs.put(AnswerType.DISAGREEMENT, result.getInt(TableColumns.Dialogs.DISAGREEMENT_ID));
			if(result.wasNull() || this.nextIDs.get(AnswerType.DISAGREEMENT) == 0)
				this.nextIDs.remove(AnswerType.DISAGREEMENT);
			this.nextIDs.put(AnswerType.CONSIDERING_DISAGREEMENT, result.getInt(TableColumns.Dialogs.CONSIDER_DISAGREEMENT_ID));
			if(result.wasNull() || this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT) == 0)
				this.nextIDs.remove(AnswerType.CONSIDERING_DISAGREEMENT);
			this.nextIDs.put(AnswerType.CONSIDERING, result.getInt(TableColumns.Dialogs.CONSIDER_ID));
			if(result.wasNull() || this.nextIDs.get(AnswerType.CONSIDERING) == 0)
				this.nextIDs.remove(AnswerType.CONSIDERING);
			if(this.nextIDs.size() > 0)
				this.nextIDs.put(AnswerType.NOTHING, this.id);
		}
		catch(final Exception e)
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

	public void setText(final String inText)
	{
		this.text = inText;
	}

	public void setNextID(final AnswerType inType, final int inId)
	{
		if(inId == 0)
			this.nextIDs.remove(inType);
		else
			this.nextIDs.put(inType, inId);
	}

	public void setNextIDs(final Map<AnswerType, Integer> inNext)
	{
		for(final Entry<AnswerType, Integer> entry : inNext.entrySet())
			this.setNextID(entry.getKey(), entry.getValue());
	}

	public Map<AnswerType, Integer> getNextIDs()
	{
		return this.nextIDs;
	}

	public int getNextID(final AnswerType inType)
	{
		final Integer id = this.nextIDs.get(inType);
		if(id == null || id == 0)
			return 0;
		return id;
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
				final PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.DIALOGS + "(" + "dialog_id," + "dialog_text," + "next_agreement_id," + "next_consider_agreement_id," + "next_disagreement_id," + "next_consider_disagreement_id," + "next_consider_id" + ") VALUES(?,?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.text);
				if(this.nextIDs.get(AnswerType.AGREEMENT) == null)
					st.setNull(3, Types.INTEGER);
				else
					st.setInt(3, this.nextIDs.get(AnswerType.AGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT) == null)
					st.setNull(4, Types.INTEGER);
				else
					st.setInt(4, this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT));
				if(this.nextIDs.get(AnswerType.DISAGREEMENT) == null)
					st.setNull(5, Types.INTEGER);
				else
					st.setInt(5, this.nextIDs.get(AnswerType.DISAGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT) == null)
					st.setNull(6, Types.INTEGER);
				else
					st.setInt(6, this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING) == null)
					st.setNull(7, Types.INTEGER);
				else
					st.setInt(7, this.nextIDs.get(AnswerType.CONSIDERING));
				st.execute();
			}
			else
			{
				final PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.DIALOGS + "(" + "dialog_text," + "next_agreement_id," + "next_consider_agreement_id," + "next_disagreement_id," + "next_consider_disagreement_id," + "next_consider_id" + ") VALUES(?,?,?,?,?,?)");
				st.setString(1, this.text);
				if(this.nextIDs.get(AnswerType.AGREEMENT) == null)
					st.setNull(2, Types.INTEGER);
				else
					st.setInt(2, this.nextIDs.get(AnswerType.AGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT) == null)
					st.setNull(3, Types.INTEGER);
				else
					st.setInt(3, this.nextIDs.get(AnswerType.CONSIDERING_AGREEMENT));
				if(this.nextIDs.get(AnswerType.DISAGREEMENT) == null)
					st.setNull(4, Types.INTEGER);
				else
					st.setInt(4, this.nextIDs.get(AnswerType.DISAGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT) == null)
					st.setNull(5, Types.INTEGER);
				else
					st.setInt(5, this.nextIDs.get(AnswerType.CONSIDERING_DISAGREEMENT));
				if(this.nextIDs.get(AnswerType.CONSIDERING) == null)
					st.setNull(6, Types.INTEGER);
				else
					st.setInt(6, this.nextIDs.get(AnswerType.CONSIDERING));
				st.execute();
				final ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save dialog " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void remove()
	{
		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement("REMOVE FROM " + Tables.DIALOGS + " WHERE `dialog_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove dialog " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
