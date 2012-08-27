package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.DeathLocation;

public class RespawnPrompt extends ValidatingPrompt
{
	private final String[] options = new String[] { "respawn", "resurrect" };
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		if(arg0.getSessionData("selected_option") == null)
		{
			return ChatColor.GREEN +"You arrived you death point. Do you want to respawn or get resurrected?";
		}
		else
		{
			if(arg0.getSessionData("selected_option").equals("respawn"))
			{
				arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Do you really want to respawn? When you do this, you won't get back your items.");
				return ChatColor.YELLOW + "If you are sure, type 'yes' in the chat.";
			}
			else
			{
				arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Do you really whish to get ressurrected? This will cost you " + DragonsLairMain.getInstance().getConfig().getInt("resurrect") + " but you'll get your items back.");
				return ChatColor.YELLOW + "If you are sure, type 'yes' in the chat.";
			}
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg0.getSessionData("selected_option") == null)
		{
			arg0.setSessionData("selected_option", arg1);
		}
		else
		{
			if(arg1.replace("'", "").equals("yes"))
			{
				Player p = (Player)arg0.getForWhom();
				DragonsLairMain.getInstance().getConversationHandler().removeRespawnConversation(p);
				ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad == null)
					return END_OF_CONVERSATION;
				
				for(String member : ad.getCurrentParty().getMembers())
				{
					if(member.equals(p.getName()))
						continue;
					
					Bukkit.getPlayer(member).showPlayer(p);
				}
				
				if(((String)arg0.getSessionData("selected_option")).equals("resurrect"))
				{
					DeathLocation dloc = ad.getDeathLocationForPlayer(p.getName());
					p.getInventory().setArmorContents(dloc.getArmor());
					p.getInventory().setContents(dloc.getInventory());
				}
				else
				{
					ad.giveMap(p);
				}
				ad.removeDeathLocation(p.getName());
				DragonsLairMain.getInstance().getEventHandler().removePlayerFromDeathObserving(p.getName());
				return END_OF_CONVERSATION;
			}
			else
			{
				arg0.setSessionData("selected_option", null);
			}
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg0.getSessionData("selected_option") == null)
		{
			for(String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}
			return false;
		}
		return true;
	}

}
