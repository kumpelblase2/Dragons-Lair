package de.kumpelblase2.dragonslair.commanddialogs.npc;

import java.util.*;
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

	public NPCListDialog(final int page)
	{
		this.page = page;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		Collection<NPC> var = DragonsLairMain.getSettings().getNPCs().values();
		final NPC[] npcs = var.toArray(new NPC[var.size()]);
		Arrays.sort(npcs, new Comparator<NPC>()
		{
			@Override
			public int compare(final NPC o1, final NPC o2)
			{
				if(o1.getID() > o2.getID()) return 1;
				else if(o1.getID() < o2.getID()) return -1;
				return 0;
			}
		});
		arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "There is/are " + npcs.length + " npc(s) available.");
		if(12 * this.page > npcs.length)
			this.page = npcs.length / 12;

		for(int i = 12 * this.page; i < npcs.length && i < 12 * this.page + 12; i++)
		{
			arg0.getForWhom().sendRawMessage("   " + npcs[i].getID() + " - " + npcs[i].getName());
		}

		return "---------------- Page " + (this.page + 1) + "/" + (npcs.length / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new NPCManageDialog();
	}
}