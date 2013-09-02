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
	private final Map<String, NPCConversation> conversations = new HashMap<String, NPCConversation>();
	private final Set<String> safeWordConversations = new HashSet<String>();
	private final Set<String> respawnConversations = new HashSet<String>();

	public static Prompt getPromptByID(final int id, final NPC inNPC)
	{
		final Dialog d = DragonsLairMain.getSettings().getDialogs().get(id);
		if(d.getType() == DialogType.MESSAGE)
			return new StorylineMessagePrompt(d, inNPC);
		else
			return new StorylineQuestionPrompt(d, inNPC);
	}

	public Map<String, NPCConversation> getConversations()
	{
		return this.conversations;
	}

	public void startConversation(final Player p, final NPC n, final int dialogID)
	{
		if(this.isInConversation(p))
			p.abandonConversation(this.conversations.get(p.getName()).getConversation());

		final ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
		final Conversation c = f.withFirstPrompt(ConversationHandler.getPromptByID(dialogID, n)).buildConversation(p);
		final ConversationStartEvent event = new ConversationStartEvent(p, n.getID(), c, dialogID);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
		{
			c.abandon();
			return;
		}

		c.addConversationAbandonedListener(this);
		c.begin();
		if(DragonsLairMain.getSettings().getDialogs().get(dialogID).getType() != DialogType.MESSAGE)
		{
			final NPCConversation npcconv = new NPCConversation(p, n, c);
			this.conversations.put(p.getName(), npcconv);
		}
		else
			p.abandonConversation(c);
	}

	@Override
	public void conversationAbandoned(final ConversationAbandonedEvent arg0)
	{
		this.conversations.remove(((Player)arg0.getContext().getForWhom()).getName());
	}

	public boolean isInConversation(final Player p)
	{
		return this.conversations.containsKey(p.getName());
	}

	public void startSafeWordConversation(final Player p)
	{
		final ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
		f.withFirstPrompt(new SafeWordPrompt()).buildConversation(p).begin();
		this.safeWordConversations.add(p.getName());
	}

	public void removeSafeWordConversation(final Player p)
	{
		this.safeWordConversations.remove(p.getName());
	}

	public void startRespawnConversation(final Player p)
	{
		final ConversationFactory f = DragonsLairMain.getDungeonManager().getConversationFactory();
		f.withFirstPrompt(new RespawnPrompt()).buildConversation(p).begin();
		this.respawnConversations.add(p.getName());
	}

	public void removeRespawnConversation(final Player p)
	{
		this.respawnConversations.remove(p.getName());
	}

	public boolean isInRespawnConversation(final Player p)
	{
		return this.respawnConversations.contains(p.getName());
	}
}