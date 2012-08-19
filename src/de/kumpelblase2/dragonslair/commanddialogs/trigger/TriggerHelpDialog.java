package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class TriggerHelpDialog extends MessagePrompt
{

	@Override
	public String getPromptText(ConversationContext arg0)
	{
		return ChatColor.GREEN + "Since the chat has limited space, there's a help page on bukkitdev.";
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return new TriggerManageDialog();
	}

}
