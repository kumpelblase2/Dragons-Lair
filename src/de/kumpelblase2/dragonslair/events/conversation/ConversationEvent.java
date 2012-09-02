package de.kumpelblase2.dragonslair.events.conversation;

import org.bukkit.conversations.Conversation;
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
	private static HandlerList handlers = new HandlerList();

	public ConversationEvent(final String inName, final Conversation inConv)
	{
		this.npc = DragonsLairMain.getSettings().getNPCByName(inName);
		this.hnpc = DragonsLairMain.getDungeonManager().getNPCByName(inName);
		this.conv = inConv;
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
}
