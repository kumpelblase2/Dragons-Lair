package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class StorylineMessagePrompt extends MessagePrompt
{
	private final Dialog dialog;
	private final String npcname;
	
	public StorylineMessagePrompt(Dialog d, String name)
	{
		this.dialog = d;
		this.npcname = name;
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		return GeneralUtilities.replaceColors("<" + this.npcname + ">" + this.dialog.getText().replace("#player#", ((Player)arg0.getForWhom()).getName()));
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return END_OF_CONVERSATION;
	}

}
