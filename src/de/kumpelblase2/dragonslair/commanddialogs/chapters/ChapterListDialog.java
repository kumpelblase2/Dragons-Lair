package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import java.util.Arrays;
import java.util.Comparator;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Chapter;

public class ChapterListDialog extends MessagePrompt
{
	private int page;
	
	public ChapterListDialog()
	{
		this(0);
	}
	
	public ChapterListDialog(int page)
	{
		this.page = page;
	}
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		Chapter[] chapters = DragonsLairMain.getSettings().getChapters().values().toArray(new Chapter[0]);
		Arrays.sort(chapters, new Comparator<Chapter>()
		{
			@Override
			public int compare(Chapter o1, Chapter o2)
			{
				if(o1.getID() > o2.getID())
					return 1;
				else if(o1.getID() < o1.getID())
					return -1;
				else
					return 0;
			}
		});
		arg0.getForWhom().sendRawMessage("There is/are " + chapters.length + " chapter(s) avaiblable.");
		if(10 * this.page >= chapters.length)
		{
			this.page = (int)(chapters.length / 12);
		}
		
		for(int i = 12 * this.page; i < chapters.length && i < 10 * this.page + 12; i++)
		{
			arg0.getForWhom().sendRawMessage("   " + chapters[i].getID() + " - " + chapters[i].getName());
		}
		return "---------------- Page " + (this.page + 1) + "/" + ((int)(chapters.length / 12) + 1);
	}

	@Override
	protected Prompt getNextPrompt(ConversationContext arg0)
	{
		return new ChapterManageDialog();
	}
}
