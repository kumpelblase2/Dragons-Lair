package de.kumpelblase2.dragonslair.commanddialogs.scheduledevents;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ScheduledEvent;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public class ScheduledEventEditDialog extends ValidatingPrompt
{
	private ScheduledEvent event;
	private final String[] options = new String[]{ "add eventid", "delete eventid", "init delay", "repeat", "repeat delay", "auto start" };

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(this.event == null)
			return ChatColor.GREEN + "Please enter the id of the event you want to edit:";
		else if(arg0.getSessionData("edit_option") == null)
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to edit?");
			return "";
		}
		else
			return ChatColor.GREEN + "Please enter a new value for this option:";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("edit_option", null);
			return new ScheduledEventsManageDialog();
		}

		if(this.event == null)
		{
			if(arg1.equals("back"))
				return new ScheduledEventsManageDialog();

			this.event = DragonsLairMain.getEventScheduler().getEventByID(Integer.parseInt(arg1));
		}
		else if(arg0.getSessionData("edit_option") == null)
		{
			if(arg1.equals("back"))
			{
				this.event = null;
				return this;
			}

			arg0.setSessionData("edit_option", arg1);
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("edit_option", null);
				return this;
			}

			final String option = (String)arg0.getSessionData("edit_option");
			if(option.equals("add eventid"))
				this.event.addEventID(Integer.parseInt(arg1));
			else if(option.equals("delete eventid"))
				this.event.removeEventID(Integer.parseInt(arg1));
			else if(option.equals("init delay"))
				this.event.setInitDelay(Integer.parseInt(arg1));
			else if(option.equals("repeat delay"))
				this.event.setRepeatDelay(Integer.parseInt(arg1));
			else if(option.equals("repeat"))
			{
				final AnswerType answer = new AnswerConverter(arg1).convert();
				this.event.setRepeat((answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT));
			}
			else if(option.equals("auto start"))
			{
				final AnswerType answer = new AnswerConverter(arg1).convert();
				this.event.setAutoStart((answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT));
			}

			arg0.setSessionData("edit_option", null);
			return new ScheduledEventsManageDialog();
		}

		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel") || arg1.equals("back"))
			return true;
		if(this.event == null)
		{
			try
			{
				final Integer id = Integer.parseInt(arg1);
				if(DragonsLairMain.getEventScheduler().getEventByID(id) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
					return false;
				}

				return false;
			}
			catch(final Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else if(arg0.getSessionData("edit_option") == null)
		{
			for(final String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}

			arg0.getForWhom().sendRawMessage(ChatColor.RED + "There's no such option.");
			return false;
		}
		else
		{
			final String option = (String)arg0.getSessionData("edit_option");
			if(option.equals("add eventid") || option.equals("delete eventid"))
			{
				try
				{
					final Integer id = Integer.parseInt(arg1);
					if(!DragonsLairMain.getSettings().getEvents().containsKey(id))
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "Such an event doesn't exist.");
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
			else if(option.equals("init delay") || option.equals("repeat delay"))
			{
				try
				{
					Integer.parseInt(arg1);
					return true;
				}
				catch(final Exception e)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
					return false;
				}
			}
			else
			{
				final AnswerType answer = new AnswerConverter(arg1).convert();
				return !(answer == AnswerType.NOTHING || answer == AnswerType.CONSIDERING);
			}
		}
	}
}