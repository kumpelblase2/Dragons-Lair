package de.kumpelblase2.dragonslair.commanddialogs.chapters;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Chapter;

public class ChapterCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext context)
	{
		return ChatColor.AQUA + "Please enter a name for the chapter:";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return new ChapterManageDialog();

		final Chapter c = new Chapter();
		c.setName(input);
		c.save();
		DragonsLairMain.getSettings().getChapters().put(c.getID(), c);
		DragonsLairMain.debugLog("Chapter '" + c.getName() + "' created");
		return new ChapterManageDialog();
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		return true;
	}
}