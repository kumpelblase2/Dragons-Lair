package de.kumpelblase2.dragonslair.commanddialogs.event;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class EventListDialog extends MessagePrompt
{
	private int page;

	public EventListDialog()
	{
		this(0);
	}

	public EventListDialog(final int page)
	{
		this.page = page;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		final List<Event> events = GeneralUtilities.getOrderedValues(DragonsLairMain.getSettings().getEvents());
		Collections.sort(events, new Comparator<Event>()
		{
			@Override
			public int compare(final Event o1, final Event o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o2.getID())
					return -1;
				return 0;
			}
		});
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "There is/are " + events.size() + " event(s) available.");
		if(12 * this.page > events.size())
			this.page = events.size() / 12;
		for(int i = 12 * this.page; i < events.size() && i < 12 * this.page + 12; i++)
		{
			String info = events.get(i).getID() + " - " + events.get(i).getActionType() + " - " + events.get(i).getOptionString();
			info = (info.length() > 60) ? info.substring(0, 60) : info;
			arg0.getForWhom().sendRawMessage(info);
		}
		return "---------------- Page " + (this.page + 1) + "/" + (events.size() / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new EventManageDialog();
	}
}
