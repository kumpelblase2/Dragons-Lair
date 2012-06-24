package de.kumpelblase2.dragonslair.commanddialogs;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.dungeon.DungeonHelpDialog;
import de.kumpelblase2.dragonslair.commanddialogs.event.EventHelpDialog;
import de.kumpelblase2.dragonslair.commanddialogs.npc.NPCHelpDialog;
import de.kumpelblase2.dragonslair.commanddialogs.trigger.TriggerHelpDialog;

public class HelpDialog extends FixedSetPrompt
{
	public HelpDialog()
	{
		super("dungeons", "npcs", "triggers", "events");
	}	
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you need help with?");
		return ChatColor.AQUA + "dungeons, npcs, triggers, events, system";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("dungeons"))
			return new DungeonHelpDialog();
		else if(arg1.equals("npcs"))
			return new NPCHelpDialog();
		else if(arg1.equals("triggers"))
			return new TriggerHelpDialog();
		else if(arg1.equals("events"))
			return new EventHelpDialog();
		return this;
	}

}
