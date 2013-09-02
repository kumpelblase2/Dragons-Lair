package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.commanddialogs.GeneralConfigDialog;

public class ChapterManageDialog extends ValidatingPrompt
{
	private final String[] options = new String[]{ "create", "list", "delete", "edit", "back" };

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to do?");
		return ChatColor.AQUA + "create, list, delete, edit, back";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equalsIgnoreCase("create"))
			return new ChapterCreateDialog();
		else if(arg1.startsWith("list"))
		{
			if(arg1.contains(" "))
				return new ChapterListDialog(Integer.parseInt(arg1.split("\\ ")[1]) - 1);
			else
				return new ChapterListDialog();
		}
		else if(arg1.equalsIgnoreCase("delete"))
			return new ChapterDeleteDialog();
		else if(arg1.equalsIgnoreCase("edit"))
			return new ChapterEditDialog();
		else if(arg1.equalsIgnoreCase("back"))
			return new GeneralConfigDialog();

		return END_OF_CONVERSATION;
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		if(input.contains(" "))
		{
			final String splitt[] = input.split("\\ ");
			if(!splitt[0].equals("list"))
				return false;
			else if(splitt.length > 2)
				return false;
			else
			{
				try
				{
					Integer.parseInt(splitt[1]);
					return true;
				}
				catch(final Exception e)
				{
					return false;
				}
			}
		}
		else for(final String option : this.options)
		{
			if(option.equalsIgnoreCase(input))
				return true;
		}

		return false;
	}
}