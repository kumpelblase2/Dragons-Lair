package de.kumpelblase2.dragonslair.commanddialogs.dialogs;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public class DialogCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("message") == null)
			return ChatColor.GREEN + "Please enter the message of the dialog:";
		else if(arg0.getSessionData("agree") == null)
			return ChatColor.GREEN + "Please enter the id of a dialog when the player agrees:";
		else if(arg0.getSessionData("disagree") == null)
			return ChatColor.GREEN + "Please enter the id of a dialog when the player disagrees:";
		else if(arg0.getSessionData("consider") == null)
			return ChatColor.GREEN + "Please enter the id of a dialog when the player is unsure:";
		else if(arg0.getSessionData("consider_agree") == null)
			return ChatColor.GREEN + "Please enter the id of a dialog when the player tends to agreement:";
		else
			return ChatColor.GREEN + "Please enter the id of a dialog when the player tends to disagreement:";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("message", null);
			arg0.setSessionData("agree", null);
			arg0.setSessionData("disagree", null);
			arg0.setSessionData("consider", null);
			arg0.setSessionData("consider_agree", null);
			return new DialogManageDialog();
		}
		if(arg0.getSessionData("message") == null)
		{
			if(arg1.equals("back"))
				return new DialogManageDialog();
			arg0.setSessionData("message", arg1);
		}
		else if(arg0.getSessionData("agree") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("message", null);
				return this;
			}
			arg0.setSessionData("agree", Integer.parseInt(arg1));
		}
		else if(arg0.getSessionData("disagree") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("agree", null);
				return this;
			}
			arg0.setSessionData("disagree", Integer.parseInt(arg1));
		}
		else if(arg0.getSessionData("consider") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("disagree", null);
				return this;
			}
			arg0.setSessionData("consider", Integer.parseInt(arg1));
		}
		else if(arg0.getSessionData("consider_agree") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("consider", null);
				return this;
			}
			arg0.setSessionData("consider_agree", Integer.parseInt(arg1));
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("consider_agree", null);
				return this;
			}
			final Map<AnswerType, Integer> answers = new HashMap<AnswerType, Integer>();
			answers.put(AnswerType.AGREEMENT, (Integer)arg0.getSessionData("agree"));
			answers.put(AnswerType.DISAGREEMENT, (Integer)arg0.getSessionData("disagree"));
			answers.put(AnswerType.CONSIDERING, (Integer)arg0.getSessionData("consider"));
			answers.put(AnswerType.CONSIDERING_AGREEMENT, (Integer)arg0.getSessionData("consider_agree"));
			answers.put(AnswerType.CONSIDERING_DISAGREEMENT, Integer.parseInt(arg1));
			final Dialog d = new Dialog();
			d.setText((String)arg0.getSessionData("message"));
			d.setNextIDs(answers);
			d.save();
			DragonsLairMain.debugLog("Created dialog with id '" + d.getID() + "'");
			DragonsLairMain.getSettings().getDialogs().put(d.getID(), d);
			arg0.setSessionData("message", null);
			arg0.setSessionData("agree", null);
			arg0.setSessionData("disagree", null);
			arg0.setSessionData("consider", null);
			arg0.setSessionData("consider_agree", null);
			return new DialogManageDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		if(arg0.getSessionData("message") == null)
			return true;
		else
		{
			try
			{
				final Integer id = Integer.parseInt(arg1);
				if(DragonsLairMain.getSettings().getDialogs().get(id) == null && id != 0)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dialog with that id doesn't exist.");
					return false;
				}
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
			return true;
		}
	}
}
