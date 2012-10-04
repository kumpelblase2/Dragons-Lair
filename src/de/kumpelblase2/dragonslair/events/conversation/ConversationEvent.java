package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.events.BaseEvent;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class ConversationEvent extends BaseEvent
{
	protected final NPC npc;
	protected final HumanNPC hnpc;
	protected final Conversation conv;
	protected final Player player;
	private static HandlerList handlers = new HandlerList();

	public ConversationEvent(final Player inPlayer, final int inID, final Conversation inConv)
	{
		this.npc = DragonsLairMain.getSettings().getNPCs().get(inID);
		this.hnpc = DragonsLairMain.getDungeonManager().getNPCByID(inID);
		this.conv = inConv;
		this.player = inPlayer;
	}

	public NPC getNPC()
	{
		return this.npc;
	}

	public HumanNPC getNPCEntity()
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
