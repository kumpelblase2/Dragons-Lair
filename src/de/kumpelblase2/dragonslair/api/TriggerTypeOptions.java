package de.kumpelblase2.dragonslair.api;

import org.bukkit.ChatColor;

public enum TriggerTypeOptions
{
	BLOCK_PLACE(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "delay", "cooldown" }),
	NPC_INTERACT(new String[] { "npc_id" }, new String[] { "delay", "cooldown" }),
	MOVEMENT(new String[] { "world", "x", "y", "z" }, new String[] { "delay", "x2", "y2", "z2", "cooldown" }),
	BLOCK_BREAK(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "delay", "cooldown" }),
	NPC_TOUCH(new String[] { "npc_id" }, new String[] { "delay", "cooldown" }),
	DIALOG_OCCUR(new String[] { "npc_id", "dialog_id" }, new String[] { "delay", "cooldown" }),
	BLOCK_INTERACT(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "delay", "cooldown" }),
	CHAPTER_CHANGE(new String[] { "chapter_id" }, new String[] { "dungeon_id", "delay", "cooldown" }),
	OBJECTIVE_CHANGE(new String[] { "objective_id" }, new String[] { "dungeon_id", "delay", "cooldown" }),
	NPC_DAMAGE(new String[] { "npc_id" }, new String[] { "delay", "cooldown" }),
	GATHER_ITEM(new String[] { "item_id", "amount" }, new String[] { "dungeon_id", "delay", "cooldown" }),
	MOBS_KILLED(new String[] { "amount", "dungeon_id" }, new String[] { "mob_id", "spawned_by", "cooldown" }),
	NPC_DEATH(new String[] { "npc_id" }, new String[] { "cooldown" }),
	LEVEL_ACHIEVE(new String[] { "amount" }, new String[] { "dungeon_id", "delay", "cooldown" });
	
	private String[] requiredOptions;
	private String[] optionalOptions;
	private TriggerTypeOptions(String[] required, String[] optional)
	{
		this.requiredOptions = required;
		this.optionalOptions = optional;
	}
	
	public String[] getRequiredOptions()
	{
		return this.requiredOptions;
	}
	
	public String[] getOptionalOptions()
	{
		return this.optionalOptions;
	}
	
	public boolean hasOption(String type)
	{
		for(String option : this.getRequiredOptions())
		{
			if(option.equals(type))
				return true;
		}
		
		for(String option : this.getOptionalOptions())
		{
			if(option.equals(type))
				return true;
		}
		return false;
	}
	
	public String getOptionString()
	{
		StringBuilder sb = new StringBuilder();
		for(String required : this.requiredOptions)
		{
			sb.append(ChatColor.AQUA + required + ChatColor.WHITE + ", ");
		}
		
		if(this.optionalOptions.length == 0 && sb.length() > 2)
			sb.substring(0, sb.length() - 2);
		
		for(int i = 0; i < this.optionalOptions.length; i++)
		{
			sb.append(ChatColor.YELLOW + this.optionalOptions[i] + ChatColor.WHITE);
			if(i != this.optionalOptions.length - 1)
				sb.append(", ");
		}
		return sb.toString();
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
	
	public static TriggerTypeOptions byType(TriggerType type)
	{
		return TriggerTypeOptions.valueOf(type.toString());
	}
}
