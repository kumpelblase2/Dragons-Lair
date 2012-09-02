package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class ChapterDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(context.getSessionData("id") == null)
			return "Please enter the id you want to remove:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the chapter? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(context.getSessionData("id") == null)
		{
			if(input.equals("back") || input.equals("cancel"))
				return new ChapterManageDialog();
			context.setSessionData("id", Integer.parseInt(input));
		}
		else if(input.equals("back"))
		{
			context.setSessionData("id", null);
			return this;
		}
		else if(input.equals("cancel"))
		{
			context.setSessionData("id", null);
			return new ChapterManageDialog();
		}
		else if(input.equals("delete"))
		{
			DragonsLairMain.debugLog("Chapter with id '" + context.getSessionData("id") + "' created");
			DragonsLairMain.getSettings().getChapters().get(context.getSessionData("id")).remove();
			DragonsLairMain.getSettings().getChapters().remove(context.getSessionData("id"));
			context.setSessionData("id", null);
			return new ChapterManageDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return true;
		if(context.getSessionData("id") == null)
			try
			{
				final int id = Integer.parseInt(input);
				if(!DragonsLairMain.getSettings().getChapters().containsKey((id)))
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A chapter with that id doesn't exist.");
					return false;
				}
				return true;
			}
			catch(final Exception e)
			{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		else
			return true;
	}
}
