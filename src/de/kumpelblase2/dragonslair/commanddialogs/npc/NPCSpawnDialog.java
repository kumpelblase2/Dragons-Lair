package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCSpawnDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		return ChatColor.GREEN + "Please enter the name of the npc you want to spawn:";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return new NPCManageDialog();
		try
		{
			final Integer id = Integer.parseInt(arg1);
			DragonsLairMain.getDungeonManager().spawnNPC(id);
		}
		catch(final Exception e)
		{
			DragonsLairMain.getDungeonManager().spawnNPC(arg1);
		}
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "NPC spawned!");
		return new NPCManageDialog();
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		try
		{
			final Integer id = Integer.parseInt(arg1);
			if(DragonsLairMain.getSettings().getNPCs().containsKey(id))
			{
				if(DragonsLairMain.getDungeonManager().getSpawnedNPCIDs().containsKey(id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The npc is already spawned.");
					return false;
				}
				else
					return true;
			}
			else
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "The npc doesn't exist.");
				return false;
			}
		}
		catch(final Exception e)
		{
			final NPC n = DragonsLairMain.getSettings().getNPCByName(arg1);
			if(n != null)
			{
				if(DragonsLairMain.getDungeonManager().getSpawnedNPCIDs().containsKey(n.getID()))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The npc is already spawned.");
					return false;
				}
				else
					return true;
			}
			else
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "The npc doesn't exist.");
				return false;
			}
		}
	}
}
