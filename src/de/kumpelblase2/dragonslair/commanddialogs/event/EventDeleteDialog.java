package de.kumpelblase2.dragonslair.commanddialogs.event;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Trigger;

public class EventDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("event_id") == null)
			return ChatColor.GREEN + "Please enter the id of the event you want to delete:";

		return ChatColor.YELLOW + "Are you sure you want to delete this event? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("event_id", null);
			return new EventManageDialog();
		}

		if(arg0.getSessionData("event_id") == null)
		{
			if(arg1.equals("back"))
				return new EventManageDialog();

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

			if(arg1.equals("delete"))
			{
				final Integer id = (Integer)arg0.getSessionData("event_id");
				DragonsLairMain.debugLog("Deleted event with id '" + id + "'");
				DragonsLairMain.getSettings().getEvents().get(id).remove();
				DragonsLairMain.getSettings().getEvents().remove(id);
				boolean changed = false;
				for(final Trigger t : DragonsLairMain.getSettings().getTriggers().values())
				{
					if(t.getEventIDs().contains(id))
					{
						t.getEventIDs().remove(id);
						t.save();
						changed = true;
					}
				}

				arg0.setSessionData("event_id", null);
				if(changed)
					DragonsLairMain.getInstance().getEventHandler().reloadTriggers();
			}

			return new EventManageDialog();
		}
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;

		if(arg0.getSessionData("event_id") == null)
		{
			try
			{
				final int id = Integer.parseInt(arg1);
				if(!DragonsLairMain.getSettings().getEvents().containsKey(id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
					return false;
				}

				return true;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}

		return true;
	}
}