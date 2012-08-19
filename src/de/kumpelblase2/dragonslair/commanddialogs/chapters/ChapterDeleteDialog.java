package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class ChapterDeleteDialog extends ValidatingPrompt
{

	@Override
	public String getPromptText(ConversationContext context)
	{
		if(context.getSessionData("id") == null)
			return "Please enter the id you want to remove:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the chapter? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
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
			DragonsLairMain.getSettings().getChapters().get((Integer)context.getSessionData("id")).remove();
			DragonsLairMain.getSettings().getChapters().remove((Integer)context.getSessionData("id"));
			context.setSessionData("id", null);
			return new ChapterManageDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return true;
		
		if(context.getSessionData("id") == null)
		{
			try
			{
				int id = Integer.parseInt(input);
				if(!DragonsLairMain.getSettings().getChapters().containsKey(((Integer)id)))
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A chapter with that id doesn't exist.");
					return false;
				}
				return true;
			}
			catch(Exception e)
			{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else
		{
			return true;
		}
	}

}
