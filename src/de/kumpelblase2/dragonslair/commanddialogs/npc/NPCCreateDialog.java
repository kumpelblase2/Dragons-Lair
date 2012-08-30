package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.*;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.conversation.*;
import de.kumpelblase2.dragonslair.utilities.*;

public class NPCCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		if(arg0.getSessionData("npc_name") == null)
		{
			return ChatColor.GREEN + "Please enter the name of the npc:";
		}
		else if(arg0.getSessionData("npc_skin") == null)
		{
			return ChatColor.GREEN + "Please insert the skin location ('-' for no custom skin):";
		}
		else if(arg0.getSessionData("npc_pos") == null)
		{
			return ChatColor.GREEN + "Please specify the location where the npc should spawn ('here' for current position):";
		}
		else if(arg0.getSessionData("npc_held_item") == null)
		{
			return ChatColor.GREEN + "Please specify the item the npc should have in his hand ('0' for nothing):";
		}
		else if(arg0.getSessionData("npc_armor") == null)
		{
			return ChatColor.GREEN + "Please specify the armor he should wear (Format: headid;chestid;pantsid;shoesid):";
		}
		else if(arg0.getSessionData("npc_invincible") == null)
		{
			return ChatColor.GREEN + "Should the NPC be invincible?";
		}
		else
		{
			return ChatColor.GREEN + "Should the npc spawn from the beginning?";
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("npc_name", null);
			arg0.setSessionData("npc_skin", null);
			arg0.setSessionData("npc_pos", null);
			arg0.setSessionData("npc_held_item", null);
			arg0.setSessionData("npc_armor", null);
			return new NPCManageDialog();
		}
		
		if(arg0.getSessionData("npc_name") == null)
		{
			if(arg1.equals("back"))
				return new NPCManageDialog();
			
			arg0.setSessionData("npc_name", arg1);
			return this;
		}
		else if(arg0.getSessionData("npc_skin") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_name", null);
				return this;
			}
			
			arg0.setSessionData("npc_skin", arg1);
			return this;
		}
		else if(arg0.getSessionData("npc_pos") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_skin", null);
				return this;
			}
			
			if(arg1.equals("here"))
			{
				arg0.setSessionData("npc_pos", ((Player)arg0.getForWhom()).getLocation());
			}
			else
			{
				arg0.setSessionData("npc_pos", WorldUtility.stringToLocation(arg1));
			}
			return this;
		}
		else if(arg0.getSessionData("npc_held_item") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_pos", null);
				return this;
			}
			
			try
			{
				int i = Integer.parseInt(arg1);
				arg0.setSessionData("npc_held_item", Material.getMaterial(i));
			}
			catch(Exception e)
			{
				arg0.setSessionData("npc_held_item", Material.getMaterial(arg1.replace(" ", "_").toUpperCase()));
			}
			return this;
		}
		else if(arg0.getSessionData("npc_armor") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_held_item", null);
				return this;
			}
			
			ItemStack[] items = InventoryUtilities.stringToItems(arg1.replace(":", ";"));
			arg0.setSessionData("npc_armor", items);
			return this;
		}
		else if(arg0.getSessionData("npc_invincible") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_armor", null);
				return this;
			}
			
			AnswerType answer = new AnswerConverter(arg1).convert();
			if(answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT)
				arg0.setSessionData("npc_invincible", true);
			else
				arg0.setSessionData("npc_invincible", false);
			return this;
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_armor", null);
				return this;
			}
			
			NPC n = new NPC();
			n.setName((String)arg0.getSessionData("npc_name"));
			String skin = (String)arg0.getSessionData("npc_skin");
			if(skin.equals("-"))
				skin = "";
			
			n.setSkin(skin);
			n.setLocation((Location)arg0.getSessionData("npc_pos"));
			n.setHeldItem((Material)arg0.getSessionData("npc_held_item"));
			n.setArmor((ItemStack[])arg0.getSessionData("npc_armor"));
			n.setInvincible((Boolean)arg0.getSessionData("npc_invincible"));
			AnswerType spawnAnswer = new AnswerConverter(arg1).convert();
			if(spawnAnswer == AnswerType.AGREEMENT || spawnAnswer == AnswerType.CONSIDERING_AGREEMENT)
				n.shouldSpawnAtBeginning(true);
			else
				n.shouldSpawnAtBeginning(false);
			
			n.save();
			DragonsLairMain.debugLog("Created NPC '" + n.getName() + "'");
			DragonsLairMain.getSettings().getNPCs().put(n.getID(), n);
			if(n.shouldSpawnAtBeginning())
				DragonsLairMain.getDungeonManager().spawnNPC(n.getID());
			
			arg0.setSessionData("npc_name", null);
			arg0.setSessionData("npc_skin", null);
			arg0.setSessionData("npc_pos", null);
			arg0.setSessionData("npc_held_item", null);
			arg0.setSessionData("npc_armor", null);
			arg0.setSessionData("npc_invincible", null);
			
			return new NPCManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		
		if(arg0.getSessionData("npc_name") == null)
		{
			/*if(DragonsLairMain.getSettings().getNPCByName(arg1) != null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "A npc with that name already exists.");
				return false;
			}*/
			return true;
		}
		else if(arg0.getSessionData("npc_skin") == null)
		{
			return true;
		}
		else if(arg0.getSessionData("npc_pos") == null)
		{
			if(arg1.equals("here"))
				return true;
			
			if(WorldUtility.stringToLocation(arg1) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Invalid location data.");
				return false;
			}
			return true;
		}
		else if(arg0.getSessionData("npc_held_item") == null)
		{
			try
			{
				int id = Integer.parseInt(arg1);
				if(Material.getMaterial(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "Invalid id.");
					return false;
				}
				return true;
			}
			catch(Exception e)
			{
				if(Material.getMaterial(arg1) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An item with that name doesn't exist.");
					return false;
				}
				return true;
			}
		}
		else if(arg0.getSessionData("npc_armor") == null)
		{
			ItemStack[] armor = InventoryUtilities.stringToItems(arg1.replace(":", ";"));
			if(armor.length != 4)
				return false;
			
			return true;
		}
		else if(arg0.getSessionData("npc_invincible") == null || arg0.getSessionData("npc_should_spawn") == null)
		{
			AnswerConverter conv = new AnswerConverter(arg1);
			AnswerType type = conv.convert();
			if(type != AnswerType.NOTHING)
				return true;
			
			return false;
		}
		return false;
	}
}
