package de.kumpelblase2.dragonslair.commanddialogs.scheduledevents;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ScheduledEvent;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;

public class ScheduledEventCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("event_ids") == null)
			return ChatColor.GREEN + "Please enter the IDs that should be fired (separated by a comma):";
		else if(arg0.getSessionData("init_delay") == null)
			return ChatColor.GREEN + "Please enter the delay before the events get executed:";
		else if(arg0.getSessionData("repeating") == null)
			return ChatColor.GREEN + "Should it repeat?";
		else if((Boolean)arg0.getSessionData("repeating") && arg0.getSessionData("repeat_delay") == null)
			return ChatColor.GREEN + "Please enter the delay between repetitions:";
		else
			return ChatColor.GREEN + "Should it automatically start at the start of the plugin?";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("event_ids", null);
			arg0.setSessionData("init_delay", null);
			arg0.setSessionData("repeating", null);
			arg0.setSessionData("repeat_delay", null);
			return new ScheduledEventsManageDialog();
		}

		if(arg0.getSessionData("event_ids") == null)
		{
			if(arg1.equals("back"))
				return new ScheduledEventsManageDialog();

			final List<Integer> scheduleIDs = new ArrayList<Integer>();
			for(final String id : arg1.split(","))
			{
				scheduleIDs.add(Integer.parseInt(id));
			}

			arg0.setSessionData("event_ids", scheduleIDs);
		}
		else if(arg0.getSessionData("init_delay") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("event_ids", null);
				return this;
			}

			arg0.setSessionData("init_delay", Integer.parseInt(arg1));
		}
		else if(arg0.getSessionData("repeating") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("init_delay", null);
				return this;
			}

			final AnswerType answer = new AnswerConverter(arg1).convert();
			if(answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT)
				arg0.setSessionData("repeating", true);
			else
				arg0.setSessionData("repeating", false);
		}
		else if((Boolean)arg0.getSessionData("repeating") && arg0.getSessionData("repeat_delay") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("repeating", null);
				return this;
			}

			arg0.setSessionData("repeat_delay", Integer.parseInt(arg1));
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("repeat_delay", null);
				return this;
			}

			final AnswerType answer = new AnswerConverter(arg1).convert();
			final boolean autoStart = (answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT);
			final int repeat_delay = (arg0.getSessionData("repeat_delay") == null ? 1 : (Integer)arg0.getSessionData("repeat_delay"));
			final boolean repeat = (Boolean)arg0.getSessionData("repeating");
			final int init_delay = (Integer)arg0.getSessionData("init_delay");
			@SuppressWarnings("unchecked")
			final List<Integer> ids = (List<Integer>)arg0.getSessionData("event_ids");
			final ScheduledEvent sevent = new ScheduledEvent();
			sevent.setAutoStart(autoStart);
			sevent.addEventIDs(ids);
			sevent.setInitDelay(init_delay);
			sevent.setRepeat(repeat);
			sevent.setRepeatDelay(repeat_delay);
			sevent.save();
			DragonsLairMain.debugLog("Created scheduled event with id '" + sevent.getID() + "'");
			DragonsLairMain.getEventScheduler().addEvent(sevent);
			arg0.setSessionData("event_ids", null);
			arg0.setSessionData("init_delay", null);
			arg0.setSessionData("repeating", null);
			arg0.setSessionData("repeat_delay", null);
			return new ScheduledEventsManageDialog();
		}

		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel") || arg1.equals("back"))
			return true;

		if(arg0.getSessionData("event_ids") == null)
		{
			if(arg1.contains(","))
			{
				final String[] ids = arg1.split(",");
				for(final String s : ids)
				{
					try
					{
						final Integer id = Integer.parseInt(s);
						if(!DragonsLairMain.getSettings().getEvents().containsKey(id))
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
							return false;
						}
					}
					catch(final Exception e)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "That is not a valid number.");
						return false;
					}
				}
			}
			else
			{
				try
				{
					final Integer id = Integer.parseInt(arg1);
					if(!DragonsLairMain.getSettings().getEvents().containsKey(id))
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
						return false;
					}
				}
				catch(final Exception e)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "That is not a valid number.");
					return false;
				}
			}

			return true;
		}
		else if(arg0.getSessionData("init_delay") == null)
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
		else if(arg0.getSessionData("repeating") == null)
		{
			final AnswerType answer = new AnswerConverter(arg1).convert();
			return answer != AnswerType.NOTHING;
		}
		else if((Boolean)arg0.getSessionData("repeating") && arg0.getSessionData("repeat_delay") == null)
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
			return answer != AnswerType.NOTHING;
		}
	}
}