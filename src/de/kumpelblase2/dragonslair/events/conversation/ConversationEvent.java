package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.events.BaseEvent;
import de.kumpelblase2.remoteentities.api.RemoteEntity;

public class ConversationEvent extends BaseEvent
{
	private final NPC npc;
	private final RemoteEntity hnpc;
	private final Conversation conv;
	private final Player player;
	private static final HandlerList handlers = new HandlerList();

	public ConversationEvent(final Player inPlayer, final int inID, final Conversation inConv)
	{
		this.npc = DragonsLairMain.getSettings().getNPCs().get(inID);
		this.hnpc = DragonsLairMain.getDungeonManager().getNPCManager().getByDatabaseID(inID);
		this.conv = inConv;
		this.player = inPlayer;
	}

	public NPC getNPC()
	{
		return this.npc;
	}

	public RemoteEntity getNPCEntity()
	{
		return this.hnpc;
	}

	public Conversation getConversation()
	{
		return this.conv;
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

	public Player getPlayer()
	{
		return this.player;
	}
}