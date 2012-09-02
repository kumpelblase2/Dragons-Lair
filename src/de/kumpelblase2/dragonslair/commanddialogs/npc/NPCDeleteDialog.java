package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(context.getSessionData("npc_name") == null)
			return ChatColor.GREEN + "Please enter the name or id of the npc to delete:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete this npc? Type 'delete' to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(input.equals("cancel"))
		{
			context.setSessionData("npc_name", null);
			return new NPCManageDialog();
		}
		if(context.getSessionData("npc_name") == null)
		{
			if(input.equals("back"))
				return new NPCManageDialog();
			Integer id = 0;
			try
			{
				id = Integer.parseInt(input);
			}
			catch(final Exception e)
			{
				final NPC n = DragonsLairMain.getSettings().getNPCByName(input);
				if(n == null)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "Something bad happened. Please try again with the id of the npc instead of the name.");
					return this;
				}
				id = n.getID();
			}
			context.setSessionData("npc_name", id);
			return this;
		}
		else
		{
			if(input.equals("back"))
			{
				context.setSessionData("npc_name", null);
				return this;
			}
			if(input.equals("delete"))
			{
				final Integer id = (Integer)context.getSessionData("npc_name");
				DragonsLairMain.getDungeonManager().despawnNPC(id);
				final NPC npc = DragonsLairMain.getSettings().getNPCs().get(id);
				DragonsLairMain.debugLog("Deleted NPC '" + npc.getName() + "'");
				npc.remove();
				DragonsLairMain.getSettings().getNPCs().remove(npc.getID());
			}
			context.setSessionData("npc_name", null);
			return new NPCManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return true;
		if(context.getSessionData("npc_name") == null)
			try
			{
				final int id = Integer.parseInt(input);
				if(DragonsLairMain.getSettings().getNPCs().get(id) == null)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "The npc doesn't exist.");
					return false;
				}
			}
			catch(final Exception e)
			{
				if(DragonsLairMain.getSettings().getNPCByName(input) == null)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "The npc doesn't exist.");
					return false;
				}
			}
		return true;
	}
}
