package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCDeleteDialog extends ValidatingPrompt
{

	@Override
	public String getPromptText(ConversationContext context)
	{
		if(context.getSessionData("npc_name") == null)
		{
			return ChatColor.GREEN + "Please enter the name of the npc to delete:";
		}
		else
		{
			return ChatColor.YELLOW + "Are you sure you want to delete this npc? Type 'delete' to confirm.";
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
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
			
			context.setSessionData("npc_name", input);
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
				String name = (String)context.getSessionData("npc_name");
				DragonsLairMain.getDungeonManager().despawnNPC(name);
				NPC npc = DragonsLairMain.getSettings().getNPCByName(name);
				npc.remove();
				DragonsLairMain.getSettings().getNPCs().remove(DragonsLairMain.getSettings().getNPCByName(name).getID());
			}
			context.setSessionData("npc_name", null);
			return new NPCManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return true;
		
		if(context.getSessionData("npc_name") == null)
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
