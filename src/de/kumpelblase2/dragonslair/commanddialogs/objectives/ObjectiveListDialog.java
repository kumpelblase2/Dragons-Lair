package de.kumpelblase2.dragonslair.commanddialogs.objectives;

import java.util.*;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Objective;

public class ObjectiveListDialog extends MessagePrompt
{
	private int page;

	public ObjectiveListDialog()
	{
		this(0);
	}

	public ObjectiveListDialog(final int page)
	{
		this.page = page;
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		Collection<Objective> var = DragonsLairMain.getSettings().getObjectives().values();
		final Objective[] objectives = var.toArray(new Objective[var.size()]);
		Arrays.sort(objectives, new Comparator<Objective>()
		{
			@Override
			public int compare(final Objective o1, final Objective o2)
			{
				if(o1.getID() > o2.getID()) return 1;
				else if(o1.getID() < o1.getID()) return -1;
				else return 0;
			}
		});
		arg0.getForWhom().sendRawMessage("There is/are " + objectives.length + " objective(s) available.");
		if(10 * this.page >= objectives.length) this.page = objectives.length / 12;
		for(int i = 12 * this.page; i < objectives.length && i < 10 * this.page + 12; i++)
		{
			arg0.getForWhom().sendRawMessage("   " + objectives[i].getID() + " - " + objectives[i].getDescription());
		}
		return "---------------- Page " + (this.page + 1) + "/" + (objectives.length / 12 + 1);
	}

	@Override
	protected Prompt getNextPrompt(final ConversationContext arg0)
	{
		return new ObjectiveManageDialog();
	}
}
