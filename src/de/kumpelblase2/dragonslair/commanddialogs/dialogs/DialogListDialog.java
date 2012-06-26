package de.kumpelblase2.dragonslair.commanddialogs.dialogs;

import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dialog;

public class DialogListDialog extends MessagePrompt
{
	private int page;
	
	public DialogListDialog()
	{
		this(0);
	}
	
	public DialogListDialog(int page)
	{
		this.page = page;
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		Dialog[] dialogs = DragonsLairMain.getSettings().getDialogs().values().toArray(new Dialog[0]);
		Arrays.sort(dialogs, new Comparator<Dialog>()
		{
			@Override
			public int compare(Dialog o1, Dialog o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o1.getID())
					return -1;
				else
					return 0;
			}
		});
		arg0.getForWhom().sendRawMessage("There is/are " + dialogs.length + " dialogs(s) avaiblable.");
		if(10 * this.page >= dialogs.length)
		{
			this.page = (int)(dialogs.length / 12);
		}
		
		for(int i = 12 * this.page; i < dialogs.length && i < 10 * this.page + 12; i++)
		{
			String info = dialogs[i].getID() + " - " + dialogs[i].getText();
			info = (info.length() > 60) ? info.substring(0, 60) : info;
			arg0.getForWhom().sendRawMessage(info);
		}
		return "---------------- Page " + (this.page + 1) + "/" + ((int)(dialogs.length / 12) + 1);
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return new DialogManageDialog();
	}
}
