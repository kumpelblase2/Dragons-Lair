package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;

public class ConversationNextDialogEvent extends ConversationEvent
{
	protected final int nextDialog;
	private static HandlerList handlers = new HandlerList();

	public ConversationNextDialogEvent(final Player inPlayer, final int inID, final Conversation inConv, final int next)
	{
		super(inPlayer, inID, inConv);
		this.nextDialog = next;
	}

	public int getNextDialogID()
	{
		return this.nextDialog;
	}

	public Dialog getNextDialog()
	{
		return DragonsLairMain.getSettings().getDialogs().get(this.nextDialog);
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