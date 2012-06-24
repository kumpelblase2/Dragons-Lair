package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;

public class ConversationNextDialogEvent extends ConversationEvent
{
	protected int nextDialog;
	
	public ConversationNextDialogEvent(String inName, Conversation inConv, int next)
	{
		super(inName, inConv);
		this.npc = DragonsLairMain.getSettings().getNPCByName(inName);
		this.hnpc = DragonsLairMain.getInstance().getDungeonManager().getNPCByName(inName);
		this.conv = inConv;
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
}
