package de.kumpelblase2.dragonslair.commanddialogs.dialogs;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;

public class DialogDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(ConversationContext context)
	{
		if(context.getSessionData("id") == null)
			return ChatColor.GREEN + "Please enter the id you want to remove:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the chapter? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
	{
		if(input.equals("back") || input.equals("cancel"))
		{
			context.setSessionData("id", null);
			return new DialogManageDialog();
		}
		
		if(context.getSessionData("id") == null)
		{
			context.setSessionData("id", Integer.parseInt(input));
			return this;
		}
		else
		{
			if(!input.equals("delete"))
			{
				context.setSessionData("id", null);
				return new DialogManageDialog();
			}
			
			Dialog d = DragonsLairMain.getSettings().getDialogs().get((Integer)context.getSessionData("id"));
			DragonsLairMain.debugLog("Delete dialog with id '" + d.getID() + "'");
			d.remove();
			DragonsLairMain.getSettings().getDialogs().remove((Integer)d.getID());
		}
		context.setSessionData("id", null);
		return new DialogManageDialog();
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
				if(DragonsLairMain.getSettings().getDialogs().get(id) == null)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A dialog with that id doesn't exist.");
					return false;
				}
			}
			catch(Exception e)
			{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		return true;
	}

}
