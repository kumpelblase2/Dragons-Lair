package de.kumpelblase2.dragonslair.conversation;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.conversation.ConversationStartEvent;

public class ConversationHandler implements ConversationAbandonedListener
{
	private Map<String, NPCConversation> conversations = new HashMap<String, NPCConversation>();
	private Set<String> safeWordConversations = new HashSet<String>();
	private Set<String> respawnConversations = new HashSet<String>();
	
    public static Prompt getPromptByID(int id, String npc)
    {
        Dialog d = DragonsLairMain.getSettings().getDialogs().get(id);
        if(d.getType() == DialogType.MESSAGE)
                return new StorylineMessagePrompt(d, npc);
        else
                return new StorylineQuestionPromt(d, npc);
    }
    
    public Map<String, NPCConversation> getConversations()
    {
    	return this.conversations;
    }
    
    public void startConversation(Player p, NPC n, int dialogID)
    {
    	if(this.isInConversation(p))
    		p.abandonConversation(this.conversations.get(p.getName()).getConversation());
    	
    	ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
    	Conversation c = f.withFirstPrompt(ConversationHandler.getPromptByID(dialogID, n.getName())).buildConversation(p);
    	ConversationStartEvent event = new ConversationStartEvent(n.getName(), c, dialogID);
    	Bukkit.getPluginManager().callEvent(event);
    	if(event.isCancelled())
    	{
    		c.abandon();
    		return;
    	}
    	
    	c.addConversationAbandonedListener(this);
    	c.begin();
    	
		if(DragonsLairMain.getSettings().getDialogs().get((Integer)dialogID).getType() != DialogType.MESSAGE)
		{
			NPCConversation npcconv = new NPCConversation(p, n, c);
			this.conversations.put(p.getName(), npcconv);
		}
		else
		{
			p.abandonConversation(c);
		}
    }

	@Override
	public void conversationAbandoned(ConversationAbandonedEvent arg0)
	{
		this.conversations.remove(((Player)arg0.getContext().getForWhom()).getName());
	}
	
	public boolean isInConversation(Player p)
	{
		return this.conversations.containsKey(p.getName());
	}
	
	public void startSafeWordConversation(Player p)
	{
		ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
    	f.withFirstPrompt(new SafeWordPrompt()).buildConversation(p).begin();
    	this.safeWordConversations.add(p.getName());
	}
	
	public void removeSafeWordConversation(Player p)
	{
		this.safeWordConversations.remove(p.getName());
	}
	
	public void startRespawnConversation(Player p)
	{
		ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
		f.withFirstPrompt(new RespawnPrompt()).buildConversation(p).begin();
		this.respawnConversations.add(p.getName());
	}
	
	public void removeRespawnConversation(Player p)
	{
		this.respawnConversations.remove(p.getName());
	}
	
	public boolean isInRespawnConversation(Player p)
	{
		return this.respawnConversations.contains(p.getName());
	}
}