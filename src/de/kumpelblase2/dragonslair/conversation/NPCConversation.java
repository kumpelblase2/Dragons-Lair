package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Conversation.ConversationState;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class NPCConversation
{
	private final Player player;
	private final NPC npc;
	private final Conversation conv;
	private final HumanNPC hnpc;
	
	public NPCConversation(Player p, NPC n, Conversation c)
	{
		this.player = p;
		this.npc = n;
		this.conv = c;
		this.hnpc = DragonsLairMain.getDungeonManager().getNPCByID(this.npc.getID());
	}
	
	public void adandon()
	{
		this.conv.abandon();
	}
	
	public boolean isAdandoned()
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
	
	public HumanNPC getNPCEntity()
	{
		return this.hnpc;
	}
}
