package de.kumpelblase2.dragonslair;

public class TableColumns
{
	public static enum Chapters
	{
		NONE,
		ID,
		NAME,
		ORDER
	}
	
	public static enum Dialogs
	{
		NONE,
		ID,
		TEXT,
		AGREEMENT_ID,
		CONSIDER_AGREEMENT_ID,
		DISAGREEMENT_ID,
		CONSIDE_DISAGREEMENT_ID,
		CONSIDER_ID
	}
	
	public static enum Dungeons
	{
		NONE,
		ID,
		NAME,
		STARTING_OBJECTIVE,
		STARTING_CHAPTER,
		STARTING_POSITION,
		SAFE_WORD,
		MIN_PLAYERS,
		MAX_PLAYERS,
		START_MESSAGE,
		END_MESSAGE,
		PARTY_READY_MESSAGE
	}
	
	public static enum Events
	{
		NONE,
		ID,
		ACTION_TYPE,
		ACTION_OPTIONS,
		COOLDOWNS
	}
	
	public static enum NPCs
	{
		NONE,
		ID,
		NAME,
		SKIN,
		LOCATION,
		HELD_ITEM_ID,
		ARMOR,
		SHOULD_SPAWN_AT_BEGINNING,
		INVINCIBLE
	}
	
	public static enum Objectives
	{
		NONE,
		ID,
		DESCRIPTION
	}
	
	public static enum Triggers
	{
		NONE,
		ID,
		TYPE,
		TYPE_OPTIONS,
		ACTION_EVENT_ID,
		COOLDOWNS
	}
	
	public static enum Parties
	{
		NONE,
		ID,
		MEMBERS,
		OBJECTIVE_ID,
		CHAPTER_ID,
		DUNGEON_ID
	}
	
	public static enum Player_Saves
	{
		NONE,
		NAME,
		ARMOR,
		ITEMS,
		HEALTH,
		HUNGER,
		LOCATION,
		PARTY_ID
	}
	
	public static enum Messages
	{
		NONE,
		ID,
		TYPE,
		MESSAGE
	}
	
	public static enum Log
	{
		NONE,
		DUNGEON_NAME,
		PARTY_ID,
		LOG_TYPE,
		LOCATION,
		BEFORE_DATA,
		AFTER_DATA
	}
}
