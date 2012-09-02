package de.kumpelblase2.dragonslair.commanddialogs.event;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.GeneralConfigDialog;

public class EventManageDialog extends ValidatingPrompt
{
	private static String[] options = new String[] { "create", "delete", "edit", "list", "back" };

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to do?");
		return ChatColor.AQUA + "create, list, delete, edit, back";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(input.startsWith("list"))
		{
			if(input.contains(" "))
				return new EventListDialog(Integer.parseInt(input.split(" ")[1]) - 1);
			else
				return new EventListDialog();
		}
		else if(input.equals("delete"))
			return new EventDeleteDialog();
		else if(input.equals("create"))
			return new EventCreateDialog();
		else if(input.equals("edit"))
			return new EventEditDialog();
		else if(input.equals("back"))
			return new GeneralConfigDialog();
		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.contains(" "))
		{
			final String[] splitt = arg1.split(" ");
			if(splitt[0].equals("list") && splitt.length == 2)
				try
				{
					Integer.parseInt(splitt[1]);
					return true;
				}
				catch(final Exception e)
				{
					return false;
				}
			else
				return false;
		}
		else
		{
			for(final String option : options)
				if(option.equals(arg1))
					return true;
			return false;
		}
	}
}
