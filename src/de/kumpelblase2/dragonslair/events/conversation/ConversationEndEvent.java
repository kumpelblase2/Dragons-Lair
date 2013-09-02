package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ConversationEndEvent extends ConversationEvent
{
	private static final HandlerList handlers = new HandlerList();

	public ConversationEndEvent(Player inPlayer, int inID, Conversation inConv)
	{
		super(inPlayer, inID, inConv);
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}