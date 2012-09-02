package de.kumpelblase2.dragonslair.commanddialogs.objectives;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class ObjectiveDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(context.getSessionData("id") == null)
			return "Please enter the id you want to remove:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the objective? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(context.getSessionData("id") == null)
		{
			if(input.equals("back") || input.equals("cancel"))
				return new ObjectiveManageDialog();
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
			return new ObjectiveManageDialog();
		}
		else if(input.equals("delete"))
		{
			DragonsLairMain.debugLog("Deleted objective with id '" + context.getSessionData("id") + "'");
			DragonsLairMain.getSettings().getObjectives().get(context.getSessionData("id")).remove();
			DragonsLairMain.getSettings().getObjectives().remove(context.getSessionData("id"));
			context.setSessionData("id", null);
			return new ObjectiveManageDialog();
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
				if(!DragonsLairMain.getSettings().getObjectives().containsKey((id)))
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
