package de.kumpelblase2.dragonslair.commanddialogs.npc;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.GeneralConfigDialog;

public class NPCManageDialog extends ValidatingPrompt
{
	private final String[] options = new String[]{ "create", "list", "delete", "edit", "spawn", "despawn", "back" };

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to do?");
		return ChatColor.AQUA + "create, list, delete, edit, spawn, despawn, back";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.startsWith("list"))
		{
			if(arg1.contains(" "))
				return new NPCListDialog(Integer.parseInt(arg1.split(" ")[1]) - 1);
			else
				return new NPCListDialog();
		}
		else if(arg1.equals("spawn"))
			return new NPCSpawnDialog();
		else if(arg1.equals("despawn"))
			return new NPCDespawnDialog();
		else if(arg1.equals("delete"))
			return new NPCDeleteDialog();
		else if(arg1.equals("create"))
			return new NPCCreateDialog();
		else if(arg1.equals("edit"))
			return new NPCEditDialog();
		else if(arg1.equals("back"))
			return new GeneralConfigDialog();

		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.contains(" "))
		{
			final String[] split = arg1.split(" ");
			if(split[0].equals("list") && split.length == 2)
			{
				try
				{
					Integer.parseInt(split[1]);
					return true;
				}
				catch(final Exception e)
				{
					return false;
				}
			}
			else
				return false;
		}
		else
		{
			for(final String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}

			return false;
		}
	}
}