package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class NPCSpawnDialog extends ValidatingPrompt
{

	@Override
	public String getPromptText(ConversationContext context)
	{
		return ChatColor.GREEN + "Please enter the name of the npc you want to spawn:";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return new NPCManageDialog();
		
		DragonsLairMain.getInstance().getDungeonManager().spawnNPC(arg1);
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "NPC spawned!");
		return new NPCManageDialog();
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		
		if(DragonsLairMain.getSettings().getNPCByName(arg1) != null)
			return true;
		
		arg0.getForWhom().sendRawMessage(ChatColor.RED + "The npc doesn't exist.");
		return false;
	}

}
