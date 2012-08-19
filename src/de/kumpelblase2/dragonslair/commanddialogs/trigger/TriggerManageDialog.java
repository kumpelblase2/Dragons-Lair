package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.GeneralConfigDialog;

public class TriggerManageDialog extends ValidatingPrompt
{
	private final String[] options = new String[] { "create", "delete", "edit", "list", "back" };
	
	@Override
	public String getPromptText(ConversationContext context)
	{
		context.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to do?");
		return ChatColor.AQUA + "create, list, delete, edit, back";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
	{
		if(input.startsWith("list"))
		{
			if(input.contains(" "))
			{
				return new TriggerListDialog(Integer.parseInt(input.split(" ")[1]) - 1);
			}
			else
				return new TriggerListDialog();
		}
		else if(input.equals("delete"))
		{
			return new TriggerDeleteDialog();
		}
		else if(input.equals("create"))
		{
			return new TriggerCreateDialog();
		}
		else if(input.equals("edit"))
		{
			return new TriggerEditDialog();
		}
		else if(input.equals("back"))
		{
			return new GeneralConfigDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg1.contains(" "))
		{
			String[] splitt = arg1.split(" ");
			if(splitt[0].equals("list") && splitt.length == 2)
			{
				try
				{
					Integer.parseInt(splitt[1]);
					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			}
			else
				return false;
		}
		else
		{
			for(String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}
			return false;
		}
	}

}
