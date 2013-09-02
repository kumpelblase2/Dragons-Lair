package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.events.conversation.ConversationEndEvent;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class StorylineMessagePrompt extends MessagePrompt
{
	private final Dialog dialog;
	private final NPC npc;

	public StorylineMessagePrompt(final Dialog d, final NPC inNPC)
	{
		this.dialog = d;
		this.npc = inNPC;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		return GeneralUtilities.replaceColors("<" + this.npc.getName() + ">" + this.dialog.getText().replace("#player#", ((Player)arg0.getForWhom()).getName()));
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		final ConversationEndEvent event = new ConversationEndEvent((Player)arg0.getForWhom(), this.npc.getID(), DragonsLairMain.getInstance().getConversationHandler().getConversations().get(((Player)arg0.getForWhom()).getName()).getConversation());
		Bukkit.getPluginManager().callEvent(event);
		return END_OF_CONVERSATION;
	}
}