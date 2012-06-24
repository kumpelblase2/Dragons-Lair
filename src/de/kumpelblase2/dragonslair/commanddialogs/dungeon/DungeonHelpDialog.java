package de.kumpelblase2.dragonslair.commanddialogs.dungeon;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.HelpDialog;

public class DungeonHelpDialog extends FixedSetPrompt
{
	public DungeonHelpDialog()
	{
		super("info", "safe word", "back");
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "About what do you want some information?");
		return ChatColor.AQUA + "info, safe word, back";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back"))
			return new HelpDialog();
		else if(arg1.equals("info"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.YELLOW + "<Description needed>");
		}
		else if(arg1.equals("safe word"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.AQUA + "The safe word can be used by players inside a dungeon to stop or pause the dungeon.");
		}
		return this;
	}

}
