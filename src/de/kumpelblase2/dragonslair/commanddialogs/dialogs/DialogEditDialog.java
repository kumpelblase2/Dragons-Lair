package de.kumpelblase2.dragonslair.commanddialogs.dialogs;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public class DialogEditDialog extends ValidatingPrompt
{
	private Dialog d;
	private final String[] options = new String[]{ "message", "agreement", "disagreement", "unsure", "consider agreement", "consider disagreement" };

	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(this.d == null)
			return ChatColor.GREEN + "Please enter the id of the dialog you want to edit:";
		else if(context.getSessionData("option") == null)
		{
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to edit?");
			final StringBuilder sb = new StringBuilder();
			for(final String option : this.options)
			{
				sb.append(option + ", ");
			}

			sb.substring(sb.length() - 2);
			return ChatColor.AQUA + sb.toString();
		}
		else
			return ChatColor.GREEN + "Please enter a new value:";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(input.equals("cancel"))
		{
			this.d = null;
			context.setSessionData("option", null);
			return new DialogManageDialog();
		}
		if(this.d == null)
		{
			if(input.equals("back"))
				return new DialogManageDialog();

			this.d = DragonsLairMain.getSettings().getDialogs().get(Integer.parseInt(input));
			return this;
		}
		else if(context.getSessionData("option") == null)
		{
			if(input.equals("back"))
			{
				this.d = null;
				return this;
			}

			context.setSessionData("option", input);
			return this;
		}
		else
		{
			if(input.equals("back"))
			{
				context.setSessionData("option", null);
				return this;
			}

			final String option = (String)context.getSessionData("option");
			if(option.equals("message"))
				this.d.setText(input);
			else if(option.equals("agreement"))
				this.d.setNextID(AnswerType.AGREEMENT, Integer.parseInt(input));
			else if(option.equals("disagreement"))
				this.d.setNextID(AnswerType.DISAGREEMENT, Integer.parseInt(input));
			else if(option.equals("unsure"))
				this.d.setNextID(AnswerType.CONSIDERING, Integer.parseInt(input));
			else if(option.equals("consider agreement"))
				this.d.setNextID(AnswerType.CONSIDERING_AGREEMENT, Integer.parseInt(input));
			else if(option.equals("consider disagreement"))
				this.d.setNextID(AnswerType.CONSIDERING_DISAGREEMENT, Integer.parseInt(input));

			this.d.save();
			context.setSessionData("option", null);
			return new DialogManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return true;

		if(this.d == null)
		{
			try
			{
				final Integer id = Integer.parseInt(input);
				if(DragonsLairMain.getSettings().getDialogs().get(id) == null)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A dialog with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else if(context.getSessionData("option") == null)
		{
			for(final String option : this.options)
			{
				if(option.equals(input))
					return true;
			}

			return false;
		}
		else if(context.getSessionData("option").equals("message"))
			return true;
		else
		{
			try
			{
				final Integer id = Integer.parseInt(input);
				if(DragonsLairMain.getSettings().getDialogs().get(id) == null && id != 0)
				{
					context.getForWhom().sendRawMessage(ChatColor.RED + "A dialog with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
	}
}