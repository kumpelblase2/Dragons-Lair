package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ConversationStartEvent extends ConversationNextDialogEvent
{
	private static HandlerList handlers = new HandlerList();

	public ConversationStartEvent(final Player inPlayer, final int inID, final Conversation inConv, final int next)
	{
		super(inPlayer, inID, inConv, next);
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