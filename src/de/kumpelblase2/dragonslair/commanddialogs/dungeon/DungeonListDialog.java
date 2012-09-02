package de.kumpelblase2.dragonslair.commanddialogs.dungeon;

import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Dungeon;

public class DungeonListDialog extends MessagePrompt
{
	private int page;

	public DungeonListDialog()
	{
		this(0);
	}

	public DungeonListDialog(final int page)
	{
		this.page = page;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		final Dungeon[] dungeons = DragonsLairMain.getSettings().getDungeons().values().toArray(new Dungeon[0]);
		Arrays.sort(dungeons, new Comparator<Dungeon>()
		{
			@Override
			public int compare(final Dungeon o1, final Dungeon o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o1.getID())
					return -1;
				else
					return 0;
			}
		});
		arg0.getForWhom().sendRawMessage("There is/are " + dungeons.length + " dungeon(s) avaiblable.");
		if(10 * this.page >= dungeons.length)
			this.page = dungeons.length / 12;
		for(int i = 12 * this.page; i < dungeons.length && i < 10 * this.page + 12; i++)
			arg0.getForWhom().sendRawMessage("   " + dungeons[i].getID() + " - " + dungeons[i].getName());
		return "---------------- Page " + (this.page + 1) + "/" + (dungeons.length / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new DungeonManageDialog();
	}
}
