package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.HelpDialog;

public class NPCHelpDialog extends FixedSetPrompt
{
	public NPCHelpDialog()
	{
		super("info", "skin", "back");
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "About what do you want some information?");
		return ChatColor.AQUA + "info, skin, back";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back"))
			return new HelpDialog();
		else if(arg1.equals("skin"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.YELLOW + "You can apply any skin to a NPC as long as Spout is installed on the server and the players use the Spoutcraft client.");
		}
		else if(arg1.equals("info"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.YELLOW + "NPCs can be used to guide players through the dungeon and can be required to finish it. They can also be used for dialogs outside of the dungeon.");
		}
		return this;
	}

}
