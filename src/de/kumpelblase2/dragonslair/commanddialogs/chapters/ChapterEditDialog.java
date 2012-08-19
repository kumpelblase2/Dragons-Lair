package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Chapter;

public class ChapterEditDialog extends ValidatingPrompt
{

	@Override
	public String getPromptText(ConversationContext context)
	{
		if(context.getSessionData("id") == null)
		{
			return "Please enter the id you want to edit:";
		}
		else
		{
			return "Please enter the new name:";
		}
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
		}
		else if(input.equals("cancel"))
		{
			context.setSessionData("id", null);
			return new ChapterManageDialog();
		}
		else
		{
			Chapter c = DragonsLairMain.getSettings().getChapters().get((Integer)context.getSessionData("id"));
			c.setName(input);
			c.save();
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
				Integer id = Integer.parseInt(input);
				if(!DragonsLairMain.getSettings().getChapters().containsKey(id))
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A chapter with that id doesn't exist.");
					return false;
				}
				return true;
			}
			catch(Exception e)
			{
				context.getSessionData(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		return true;
	}

}
