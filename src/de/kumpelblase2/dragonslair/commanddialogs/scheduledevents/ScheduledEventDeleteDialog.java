package de.kumpelblase2.dragonslair.commanddialogs.scheduledevents;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class ScheduledEventDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("event_id") == null)
			return ChatColor.GREEN + "Please enter the id of the scheduled event to delete:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the scheduled event? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg0.getSessionData("event_id") == null)
		{
			if(arg1.equals("back") || arg1.equals("cancel"))
				return new ScheduledEventsManageDialog();
			arg0.setSessionData("event_id", Integer.parseInt(arg1));
			return this;
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("event_id", null);
				return this;
			}
			else if(arg1.equals("cancel") || !arg1.equals("delete"))
				arg0.setSessionData("event_id", null);
			else
			{
				final Integer id = (Integer)arg0.getSessionData("event_id");
				DragonsLairMain.debugLog("Deleted scheduled event with id '" + id + "'");
				arg0.setSessionData("event_id", null);
				DragonsLairMain.getEventScheduler().removeEvent(id);
			}
			return new ScheduledEventsManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		if(arg0.getSessionData("event_id") == null)
			try
			{
				final Integer id = Integer.parseInt(arg1);
				if(!DragonsLairMain.getEventScheduler().getEvents().containsKey(id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A scheduled event with that id doesn't exist.");
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
