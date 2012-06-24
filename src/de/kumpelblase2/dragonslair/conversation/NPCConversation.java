package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Conversation.ConversationState;
import org.bukkit.entity.Player;
import com.topcat.npclib.entity.HumanNPC;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCConversation
{
	private Player player;
	private NPC npc;
	private Conversation conv;
	private HumanNPC hnpc;
	
	public NPCConversation(Player p, NPC n, Conversation c)
	{
		this.player = p;
		this.npc = n;
		this.conv = c;
		this.hnpc = (HumanNPC)DragonsLairMain.getInstance().getDungeonManager().getNPCManager().getNPC(DragonsLairMain.getInstance().getDungeonManager().getSpawnedNPCIDs().get(this.npc.getName()));
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
