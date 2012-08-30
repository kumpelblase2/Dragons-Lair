package de.kumpelblase2.dragonslair.commanddialogs.objectives;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Objective;

public class ObjectiveEditDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(context.getSessionData("id") == null)
			return "Please enter the id you want to edit:";
		else
			return "Please enter the new description:";
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
			context.setSessionData("id", null);
		else if(input.equals("cancel"))
		{
			context.setSessionData("id", null);
			return new ObjectiveManageDialog();
		}
		else
		{
			final Objective o = DragonsLairMain.getSettings().getObjectives().get(context.getSessionData("id"));
			o.setDescription(input);
			o.save();
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
				final Integer id = Integer.parseInt(input);
				if(!DragonsLairMain.getSettings().getObjectives().containsKey(id))
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A chapter with that id doesn't exist.");
					return false;
				}
				return true;
			}
			catch(final Exception e)
			{
				context.getSessionData(ChatColor.RED + "Not a valid number.");
				return false;
			}
		return true;
	}
}
