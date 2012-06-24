package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Chapter;

public class ChapterCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(ConversationContext context)
	{
		return ChatColor.AQUA + "Please enter a name for the chapter:";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return new ChapterManageDialog();
		
		Chapter c = new Chapter();
		c.setName(input);
		c.save();
		DragonsLairMain.getSettings().getChapters().put(c.getID(), c);
		return new ChapterManageDialog();
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		return true;
	}
}
