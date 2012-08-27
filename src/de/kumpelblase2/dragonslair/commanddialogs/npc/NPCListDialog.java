package de.kumpelblase2.dragonslair.commanddialogs.npc;

import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCListDialog extends MessagePrompt
{
	private int page;
	
	public NPCListDialog()
	{
		this(0);
	}
	
	public NPCListDialog(int page)
	{
		this.page = page;
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		NPC[] npcs = DragonsLairMain.getSettings().getNPCs().values().toArray(new NPC[0]);
		Arrays.sort(npcs, new Comparator<NPC>()
		{
			@Override
			public int compare(NPC o1, NPC o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o2.getID())
					return -1;
				return 0;
			}
		});
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "There is/are " + npcs.length + " npc(s) avaiblable.");
		if(12 * this.page > npcs.length)
		{
			this.page = (int)(npcs.length / 12);
		}
		
		for(int i = 12 * this.page; i < npcs.length && i < 12 * this.page + 12; i++)
		{
			arg0.getForWhom().sendRawMessage("   " + npcs[i].getID() + " - " + npcs[i].getName());
		}
		return "---------------- Page " + (this.page + 1) + "/" + ((int)(npcs.length / 12) + 1);
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return new NPCManageDialog();
	}

}
