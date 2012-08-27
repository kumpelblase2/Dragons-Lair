package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.event.HandlerList;

public class ConversationStartEvent extends ConversationNextDialogEvent
{

	public ConversationStartEvent(String inName, Conversation inConv, int next)
	{
		super(inName, inConv, 0);
		this.nextDialog = next;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

}
