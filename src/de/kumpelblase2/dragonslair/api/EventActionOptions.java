package de.kumpelblase2.dragonslair.api;

import org.bukkit.ChatColor;

public enum EventActionOptions
{
	NPC_SPAWN(new String[] { "npc_id" }, new String[0]),
	BLOCK_CHANGE(new String[] { "world", "block_id", "x", "y", "z" }, new String[] { "x2", "y2", "z2" }),
	MOB_SPAWN(new String[] { "world", "mob_id", "x", "y", "z" }, new String[] { "amount" }),
	OBJECTIVE_COMPLETE(new String[] { "objective_id" }, new String[0]),
	NPC_DESPAWN(new String[] { "npc_id" }, new String[0]),
	ITEM_REMOVE(new String[] { "item_id", "amount" }, new String[] { "scope", "on_failure", "on_success" }),
	PLAYER_WARP(new String[] { "world", "x", "y", "z" }, new String[] { "scope" }),
	DUNGEON_START(new String[] { "dungeon_id" }, new String[0]),
	DUNGEON_END(new String[] { "dungeon_id" }, new String[] { "warp_on_end", "give_items" }),
	DUNGEON_REGISTER(new String[] { "dungeon_id" }, new String[0]),
	ITEM_ADD(new String[] { "item_id", "amount" }, new String[] { "scope", "damage" }),
	CHAPTER_COMPLETE(new String[] { "chapter_id" }, new String[0]),
	NPC_ATTACK(new String[] { "npc_id" }, new String[] { "target" }),
	NPC_STOP_ATTACK(new String[] { "npc_id" }, new String[0]),
	NPC_WALK(new String[] { "npc_id", "x", "y", "z" }, new String[0]),
	NPC_DIALOG(new String[] { "npc_id", "dialog_id" }, new String[] { "send_to" }),
	ITEM_SPAWN(new String[] { "world", "x", "y", "z", "item_id" }, new String[] { "amount" }),
	BROADCAST_MESSAGE(new String[] { "message" }, new String[] { "permission", "dungeon_id" }),
	SAY(new String[] { "message" }, new String[0]),
	ADD_POTION_EFFECT(new String[] { "potiontype", "amplifier", "duration" }, new String[] { "scope" }),
	REMOVE_POTION_EFFECT(new String[] { "potiontype" }, new String[] { "duration", "scope" }),
	CHANGE_LEVEL(new String[] { "amount", "change_type" }, new String[] { "scope" }),
	CHANGE_HEALTH(new String[] { "amount", "change_type" }, new String[] { "scope" }),
	CHANGE_HUNGER(new String[] { "amout", "change_type" }, new String[] { "scope" }),
	EXECUTE_COMMAND(new String[] { "command" }, new String[] { "execute_as" }),
	START_SCHEDULED_EVENT(new String[] { "event_id" }, new String[0]),
	STOP_SCHEDULED_EVENT(new String[] { "event_id" }, new String[0]);
	
	private String[] requiredOptions;
	private String[] optionalOptions;
	private final String[] generalOptions = new String[] { "delay", "cooldown" };
	
	private EventActionOptions(String[] required, String[] optional)
	{
		this.requiredOptions = required;
		this.optionalOptions = optional;
	}
	
	public static EventActionOptions byType(EventActionType type)
	{
		return EventActionOptions.valueOf(type.toString());
	}
	
	public String[] getRequiredTypes()
	{
		return this.requiredOptions;
	}
	
	public String[] getOptionalTypes()
	{
		String[] temp = new String[this.generalOptions.length + this.optionalOptions.length];
		System.arraycopy(this.optionalOptions, 0, temp, 0, this.optionalOptions.length);
		System.arraycopy(this.generalOptions, 0, temp, this.optionalOptions.length, this.generalOptions.length);
		return temp;
	}
	
	public String getOptionString()
	{
		StringBuilder sb = new StringBuilder();
		for(String required : this.requiredOptions)
		{
			sb.append(ChatColor.AQUA + required + ChatColor.WHITE +  ", ");
		}
		
		if(optionalOptions.length == 0 && sb.length() > 2)
			sb.substring(0, sb.length() - 2);
		
		for(int i = 0; i < optionalOptions.length; i++)
		{
			sb.append(ChatColor.YELLOW + this.optionalOptions[i] + ChatColor.WHITE + ", ");
		}
		
		for(int i = 0; i < this.generalOptions.length; i++)
		{
			sb.append(ChatColor.YELLOW + this.generalOptions[i] + ChatColor.WHITE);
			if(i != this.optionalOptions.length - 1)
				sb.append(", ");
		}
		
		return sb.toString();
	}
	
	public boolean hasOption(String option)
	{
		for(String general : this.generalOptions)
		{
			if(general.equals(option))
				return true;
		}
		
		for(String required : this.requiredOptions)
		{
			if(required.equals(option))
				return true;
		}
		
		for(String optional : this.optionalOptions)
		{
			if(optional.equals(option))
				return true;
		}
		return false;
	}
	
	public boolean isRequired(String option)
	{
		for(String required : this.requiredOptions)
		{
			if(required.equals(option))
				return true;
		}
		return false;
	}
}
