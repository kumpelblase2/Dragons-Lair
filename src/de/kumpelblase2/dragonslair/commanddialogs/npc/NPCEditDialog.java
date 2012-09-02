package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class NPCEditDialog extends ValidatingPrompt
{
	private final String[] options = new String[] { "name", "skin", "position", "held item", "armor", "fist spawn", "invincibility" };

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("npc_name") == null)
			return ChatColor.GREEN + "Please enter the name of the npc to edit:";
		else if(arg0.getSessionData("edit_option") == null)
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to edit?");
			final StringBuilder sb = new StringBuilder();
			for(int i = 0; i < this.options.length; i++)
			{
				sb.append(this.options[i]);
				if(i != this.options.length - 1)
					sb.append(", ");
			}
			return ChatColor.AQUA + sb.toString();
		}
		else
		{
			final String option = (String)arg0.getSessionData("edit_option");
			if(option.equals("name"))
				return ChatColor.GREEN + "Please enter the new name:";
			else if(option.equals("skin"))
				return ChatColor.GREEN + "Please enter the new skin location:";
			else if(option.equals("position"))
				return ChatColor.GREEN + "Please specify the new position:";
			else if(option.equals("held item"))
				return ChatColor.GREEN + "Please enter the new item for the npc's hand:";
			else if(option.equals("armor"))
				return ChatColor.GREEN + "Please enter the new armor (Format: headid;chestid;pantsid;shoesid)";
			else if(this.options.equals("invincibility"))
				return ChatColor.GREEN + "Should the NPC be invincible?";
			else
				return ChatColor.GREEN + "Should the npc spawn from beginning?";
		}
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("npc_name", null);
			arg0.setSessionData("edit_option", null);
			return new NPCManageDialog();
		}
		if(arg0.getSessionData("npc_name") == null)
		{
			if(arg1.equals("back"))
				return new NPCManageDialog();
			arg0.setSessionData("npc_name", arg1);
		}
		else if(arg0.getSessionData("edit_option") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("npc_name", null);
				return this;
			}
			arg0.setSessionData("edit_option", arg1);
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("edit_option", null);
				return this;
			}
			final String option = (String)arg0.getSessionData("edit_option");
			final NPC n = DragonsLairMain.getSettings().getNPCByName((String)arg0.getSessionData("npc_name"));
			final boolean respawn = DragonsLairMain.getDungeonManager().despawnNPC(n);
			if(option.equals("name"))
				n.setName(arg1);
			else if(option.equals("skin"))
			{
				if(arg1.equals("-"))
					n.setSkin("");
				else
					n.setSkin(arg1);
			}
			else if(option.equals("position"))
			{
				if(arg1.equals("here"))
					n.setLocation(((Player)arg0.getForWhom()).getLocation());
				else
					n.setLocation(WorldUtility.stringToLocation(arg1));
			}
			else if(option.equals("held item"))
				try
				{
					final int id = Integer.parseInt(arg1);
					n.setHeldItem(Material.getMaterial(id));
				}
				catch(final Exception e)
				{
					n.setHeldItem(Material.getMaterial(arg1));
				}
			else if(option.equals("armor"))
			{
				final ItemStack[] armor = InventoryUtilities.stringToItems(arg1);
				n.setArmor(armor);
			}
			else if(option.equals("invincibility"))
			{
				final AnswerType answer = new AnswerConverter(arg1).convert();
				if(answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT)
					n.setInvincible(true);
				else
					n.setInvincible(false);
			}
			else
			{
				final AnswerType type = new AnswerConverter(arg1).convert();
				if(type == AnswerType.AGREEMENT || type == AnswerType.CONSIDERING_AGREEMENT)
					n.shouldSpawnAtBeginning(true);
				else
					n.shouldSpawnAtBeginning(false);
			}
			n.save();
			if(respawn && n.shouldSpawnAtBeginning())
				DragonsLairMain.getDungeonManager().spawnNPC(n);
			arg0.setSessionData("npc_name", null);
			arg0.setSessionData("edit_option", null);
			return new NPCManageDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		if(arg0.getSessionData("npc_name") == null)
		{
			if(DragonsLairMain.getSettings().getNPCByName(arg1) == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "A npc with that name doesn't exist.");
				return false;
			}
			return true;
		}
		else if(arg0.getSessionData("edit_option") == null)
		{
			for(final String option : this.options)
				if(option.equals(arg1))
					return true;
			return false;
		}
		else
		{
			final String option = (String)arg0.getSessionData("edit_option");
			if(option.equals("name"))
			{
				if(DragonsLairMain.getSettings().getNPCByName(arg1) != null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A npc with that name already exist.");
					return false;
				}
				return true;
			}
			else if(option.equals("skin"))
				return true;
			else if(option.equals("position"))
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
			else if(option.equals("held item"))
				try
				{
					final int id = Integer.parseInt(arg1);
					if(Material.getMaterial(id) == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "Invalid id.");
						return false;
					}
					return true;
				}
				catch(final Exception e)
				{
					if(Material.getMaterial(arg1) == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "An item with that name doesn't exist.");
						return false;
					}
					return true;
				}
			else if(option.equals("armor"))
			{
				final ItemStack[] armor = InventoryUtilities.stringToItems(arg1);
				if(armor.length != 4)
					return false;
				return true;
			}
			else
				return new AnswerConverter(arg1).convert() != AnswerType.NOTHING;
		}
	}
}
