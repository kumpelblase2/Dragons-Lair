package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.events.conversation.ConversationNextDialogEvent;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;


public class StorylineQuestionPromt extends ValidatingPrompt
{
	private Dialog dialog;
	private String npcname;
	
	public StorylineQuestionPromt(Dialog d, String name)
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
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		AnswerType type = new AnswerConverter(arg1).convert();
		ConversationNextDialogEvent event = new ConversationNextDialogEvent(this.npcname, DragonsLairMain.getInstance().getConversationHandler().getConversations().get(((Player)arg0.getForWhom()).getName()).getConversation(), this.dialog.getNextID(type));
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
			return this;
		
		Integer next = this.dialog.getNextID(type);
		if(next == 0)
			return END_OF_CONVERSATION;
		
		return ConversationHandler.getPromptByID(next, this.npcname);
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		AnswerType type = new AnswerConverter(arg1).convert();
		if(type != AnswerType.NOTHING)
			return true;
		
		arg0.getForWhom().sendRawMessage("<" + this.npcname + ">" + "I don't get what you mean.");
		return false;
	}
}
