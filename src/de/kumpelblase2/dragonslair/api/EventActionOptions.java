package de.kumpelblase2.dragonslair.api;

import org.bukkit.ChatColor;


public enum EventActionOptions
{
	NPC_SPAWN(new String[] { "npc_id" }, new String[] { "cooldown", "delay" }),
	BLOCK_CHANGE(new String[] { "world", "block_id", "x", "y", "z" }, new String[] { "x2", "y2", "z2", "cooldown", "delay" }),
	MOB_SPAWN(new String[] { "world", "mob_id", "x", "y", "z" }, new String[] { "amount", "cooldown", "delay" }),
	OBJECTIVE_COMPLETE(new String[] { "objective_id" }, new String[] { "cooldown", "delay" }),
	NPC_DESPAWN(new String[] { "npc_id" }, new String[] { "cooldown", "delay" }),
	ITEM_REMOVE(new String[] { "item_id", "amount" }, new String[] { "scope", "on_failure", "on_success", "cooldown", "delay" }),
	PLAYER_WARP(new String[] { "world", "x", "y", "z" }, new String[] { "scope", "cooldown", "delay" }),
	DUNGEON_START(new String[] { "dungeon_id" }, new String[] { "cooldown", "delay" }),
	DUNGEON_END(new String[] { "dungeon_id" }, new String[] { "warp_on_end", "give_items", "cooldown", "delay" }),
	DUNGEON_REGISTER(new String[] { "dungeon_id" }, new String[] { "cooldown", "delay" }),
	ITEM_ADD(new String[] { "item_id", "amount" }, new String[] { "scope", "damage", "cooldown", "delay" }),
	CHAPTER_COMPLETE(new String[] { "chapter_id" }, new String[] { "cooldown", "delay" }),
	NPC_ATTACK(new String[] { "npc_id" }, new String[] { "target", "cooldown", "delay" }),
	NPC_STOP_ATTACK(new String[] { "npc_id" }, new String[] { "cooldown", "delay" }),
	NPC_WALK(new String[] { "npc_id", "x", "y", "z" }, new String[] { "cooldown", "delay" }),
	NPC_DIALOG(new String[] { "npc_id", "dialog_id" }, new String[] { "send_to", "cooldown", "delay" }),
	ITEM_SPAWN(new String[] { "world", "x", "y", "z", "item_id" }, new String[] { "amount", "cooldown", "delay" }),
	BROADCAST_MESSAGE(new String[] { "message" }, new String[] { "permission", "dungeon_id", "cooldown", "delay" }),
	SAY(new String[] { "message" }, new String[] { "cooldown", "delay" }),
	ADD_POTION_EFFECT(new String[] { "potiontype", "amplifier", "duration" }, new String[] { "scope", "cooldown", "delay" }),
	REMOVE_POTION_EFFECT(new String[] { "potiontype" }, new String[] { "duration", "scope", "cooldown", "delay" });
	
	private String[] requiredOptions;
	private String[] optionalOptions;
	
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
		return this.optionalOptions;
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
			sb.append(ChatColor.YELLOW + this.optionalOptions[i] + ChatColor.WHITE);
			if(i != this.optionalOptions.length - 1)
				sb.append(", ");
		}
		return sb.toString();
	}
	
	public boolean hasOption(String option)
	{
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
