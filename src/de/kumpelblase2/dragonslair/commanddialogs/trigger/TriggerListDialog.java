package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Trigger;

public class TriggerListDialog extends MessagePrompt
{
	private int page;

	public TriggerListDialog()
	{
		this(0);
	}

	public TriggerListDialog(final int page)
	{
		this.page = page;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		Collection<Trigger> var = DragonsLairMain.getSettings().getTriggers().values();
		final Trigger[] triggers = var.toArray(new Trigger[var.size()]);
		Arrays.sort(triggers, new Comparator<Trigger>()
		{
			@Override
			public int compare(final Trigger o1, final Trigger o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o2.getID())
					return -1;

				return 0;
			}
		});
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "There is/are " + triggers.length + " trigger(s) available.");
		if(12 * this.page > triggers.length)
			this.page = triggers.length / 12;

		for(int i = 12 * this.page; i < triggers.length && i < 12 * this.page + 12; i++)
		{
			String info = triggers[i].getID() + " - " + triggers[i].getType() + " - " + triggers[i].getOptionString();
			info = (info.length() > 60) ? info.substring(0, 60) : info;
			arg0.getForWhom().sendRawMessage(info);
		}

		return "---------------- Page " + (this.page + 1) + "/" + (triggers.length / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new TriggerManageDialog();
	}
}