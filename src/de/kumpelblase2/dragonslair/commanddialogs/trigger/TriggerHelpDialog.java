package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;

public class TriggerHelpDialog extends MessagePrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		return ChatColor.GREEN + "Since the chat has limited space, there's a help page on bukkitdev.";
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new TriggerManageDialog();
	}
}