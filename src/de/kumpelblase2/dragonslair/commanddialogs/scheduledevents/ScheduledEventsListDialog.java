package de.kumpelblase2.dragonslair.commanddialogs.scheduledevents;

import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ScheduledEvent;

public class ScheduledEventsListDialog extends MessagePrompt
{
	private int page;

	public ScheduledEventsListDialog(final int page)
	{
		this.page = page;
	}

	public ScheduledEventsListDialog()
	{
		this.page = 0;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		final ScheduledEvent[] events = DragonsLairMain.getEventScheduler().getEvents().values().toArray(new ScheduledEvent[0]);
		Arrays.sort(events, new Comparator<ScheduledEvent>()
		{
			@Override
			public int compare(final ScheduledEvent arg0, final ScheduledEvent arg1)
			{
				if(arg0.getID() > arg1.getID()) return 1;
				else if(arg0.getID() < arg1.getID()) return -1;
				else return 0;
			}
		});
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "There is/are " + events.length + " scheduled event(s) available:");
		if(12 * this.page > events.length)
			this.page = events.length / 12;

		for(int i = 12 * this.page; i < events.length && i < 12 * this.page + 12; i++)
		{
			arg0.getForWhom().sendRawMessage("   " + events[i].getID() + " - event ids:" + events[i].getEventIDString());
		}

		return "---------------- Page " + (this.page + 1) + "/" + (events.length / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new ScheduledEventsManageDialog();
	}
}