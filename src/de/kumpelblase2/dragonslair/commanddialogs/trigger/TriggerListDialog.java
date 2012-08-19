package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import java.util.Arrays;
import java.util.Comparator;
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
	
	public TriggerListDialog(int page)
	{
		this.page = page;
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		Trigger[] triggers = DragonsLairMain.getSettings().getTriggers().values().toArray(new Trigger[0]);
		Arrays.sort(triggers, new Comparator<Trigger>()
		{

			@Override
			public int compare(Trigger o1, Trigger o2)
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
		{
			this.page = (int)(triggers.length / 12);
		}
		
		for(int i = 12 * this.page; i < triggers.length && i < 12 * this.page + 12; i++)
		{
			String info = triggers[i].getID() + " - " + triggers[i].getType() + " - " + triggers[i].getOptionString();
			info = (info.length() > 60) ? info.substring(0, 60) : info;
			arg0.getForWhom().sendRawMessage(info);
		}
		return "---------------- Page " + (this.page + 1) + "/" + ((int)(triggers.length / 12) + 1);
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return new TriggerManageDialog();
	}

}
