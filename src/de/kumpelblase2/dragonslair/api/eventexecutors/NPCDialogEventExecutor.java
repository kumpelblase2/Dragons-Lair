package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.conversation.ConversationHandler;

public class NPCDialogEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		ConversationHandler ch = DragonsLairMain.getInstance().getConversationHandler();
		String npcid = e.getOption("npc_id");
		String dialogid = e.getOption("dialog_id");
		if(npcid == null)
			return false;
		
		NPC npc = null;
		int dialog = -1;
		try
		{
			Integer id = Integer.parseInt(npcid);
			if(!DragonsLairMain.getSettings().getNPCs().containsKey(id))
				return false;
			
			npc = DragonsLairMain.getSettings().getNPCs().get(id);
			if(dialogid == null)
				return false;
			else
				dialog = Integer.parseInt(dialogid);
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to parse event.");
			ex.printStackTrace();
		}
		
		if(npc == null)
			return false;

		if(dialog == -1)
			dialog = 0;
		
		String sendTo = e.getOption("send_to");
		if(sendTo != null && (sendTo.equalsIgnoreCase("all") || sendTo.equalsIgnoreCase("party")))
		{
			ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			for(String playername : d.getCurrentParty().getMembers())
			{
				Player player = Bukkit.getPlayer(playername);
				if(ch.getConversations().containsKey(p.getName()))
				{
					ch.getConversations().get(playername).adandon();
					ch.getConversations().get(playername).getConversation().abandon();
				}

				ch.startConversation(player, npc, dialog);
			}
		}
		else if(sendTo == null || sendTo.equalsIgnoreCase("single") || sendTo.equalsIgnoreCase("interactor"))
		{
			if(ch.getConversations().containsKey(p.getName()) && ch.getConversations().get(p.getName()) != null)
			{
				ch.getConversations().get(p.getName()).adandon();
				if(ch.getConversations().get(p.getName()) != null && ch.getConversations().get(p.getName()).getConversation() != null)
					ch.getConversations().get(p.getName()).getConversation().abandon();
			}
			
			ch.startConversation(p, npc, dialog);
		}
		
		return true;
	}

}
