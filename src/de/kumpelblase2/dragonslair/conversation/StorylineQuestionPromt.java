package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.events.conversation.ConversationNextDialogEvent;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class StorylineQuestionPromt extends ValidatingPrompt
{
	private final Dialog dialog;
	private final NPC npc;

	public StorylineQuestionPromt(final Dialog d, final NPC inNPC)
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
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		final AnswerType type = new AnswerConverter(arg1).convert();
		final ConversationNextDialogEvent event = new ConversationNextDialogEvent((Player)arg0.getForWhom(), this.npc.getID(), DragonsLairMain.getInstance().getConversationHandler().getConversations().get(((Player)arg0.getForWhom()).getName()).getConversation(), this.dialog.getNextID(type));
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
			return this;

		final Integer next = this.dialog.getNextID(type);
		if(next == 0)
			return END_OF_CONVERSATION;

		return ConversationHandler.getPromptByID(next, this.npc);
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		final AnswerType type = new AnswerConverter(arg1).convert();
		if(type != AnswerType.NOTHING)
			return true;

		arg0.getForWhom().sendRawMessage("<" + this.npc.getName() + ">" + "I don't get what you mean.");
		return false;
	}
}