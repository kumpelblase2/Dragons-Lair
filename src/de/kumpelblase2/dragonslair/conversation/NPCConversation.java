package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Conversation.ConversationState;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.remoteentities.api.RemoteEntity;

public class NPCConversation
{
	private final Player player;
	private final NPC npc;
	private final Conversation conv;
	private final RemoteEntity hnpc;

	public NPCConversation(final Player p, final NPC n, final Conversation c)
	{
		this.player = p;
		this.npc = n;
		this.conv = c;
		this.hnpc = DragonsLairMain.getDungeonManager().getNPCManager().getByDatabaseID(n.getID());
	}

	public void abandon()
	{
		this.conv.abandon();
	}

	public boolean isAbandoned()
	{
		return this.conv.getState() == ConversationState.ABANDONED;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public NPC getNPC()
	{
		return this.npc;
	}

	public Conversation getConversation()
	{
		return this.conv;
	}

	public RemoteEntity getNPCEntity()
	{
		return this.hnpc;
	}
}