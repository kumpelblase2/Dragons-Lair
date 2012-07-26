package de.kumpelblase2.dragonslair.api;

import org.bukkit.ChatColor;

public enum TriggerTypeOptions
{
	BLOCK_PLACE(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "x2", "y2", "z2" }),
	NPC_INTERACT(new String[] { "npc_id" }, new String[0]),
	MOVEMENT(new String[] { "world", "x", "y", "z" }, new String[] { "x2", "y2", "z2" }),
	BLOCK_BREAK(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "x2", "y2", "z2" }),
	NPC_TOUCH(new String[] { "npc_id" }, new String[0]),
	DIALOG_OCCUR(new String[] { "npc_id", "dialog_id" }, new String[0]),
	BLOCK_INTERACT(new String[] { "world", "x", "y", "z" }, new String[] { "block_id", "x2", "y2", "z2" }),
	CHAPTER_CHANGE(new String[] { "chapter_id" }, new String[] { "dungeon_id" }),
	OBJECTIVE_CHANGE(new String[] { "objective_id" }, new String[] { "dungeon_id" }),
	NPC_DAMAGE(new String[] { "npc_id" }, new String[0]),
	GATHER_ITEM(new String[] { "item_id", "amount" }, new String[] { "dungeon_id" }),
	MOBS_KILLED(new String[] { "amount", "dungeon_id" }, new String[] { "mob_id", "spawned_by" }),
	NPC_DEATH(new String[] { "npc_id" }, new String[0]),
	LEVEL_ACHIEVE(new String[] { "amount" }, new String[] { "dungeon_id" }),
	ITEM_CRAFT(new String[] { "item_id" }, new String[0]);
	
	private String[] requiredOptions;
	private String[] optionalOptions;
	private final String[] generalOptions = new String[] { "delay", "cooldown" };
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
		String[] temp = new String[this.generalOptions.length + this.optionalOptions.length];
		System.arraycopy(this.optionalOptions, 0, temp, 0, this.optionalOptions.length);
		System.arraycopy(this.generalOptions, 0, temp, this.optionalOptions.length, this.generalOptions.length);
		return temp;
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
		for(String required : this.getRequiredOptions())
		{
			sb.append(ChatColor.AQUA + required + ChatColor.WHITE + ", ");
		}
		
		String[] optional = this.getOptionalOptions();
		if(optional.length == 0 && sb.length() > 2)
			sb.substring(0, sb.length() - 2);
		
		for(int i = 0; i < optional.length; i++)
		{
			sb.append(ChatColor.YELLOW + optional[i] + ChatColor.WHITE);
			if(i != optional.length - 1)
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
