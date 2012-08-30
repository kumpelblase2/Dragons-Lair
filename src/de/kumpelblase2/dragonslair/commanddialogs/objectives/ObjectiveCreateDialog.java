package de.kumpelblase2.dragonslair.commanddialogs.objectives;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Objective;

public class ObjectiveCreateDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(ConversationContext context)
	{
		return ChatColor.AQUA + "Please enter a description of the objective:";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input)
	{
		if(input.equals("back") || input.equals("cancel"))
			return new ObjectiveManageDialog();
		
		Objective o = new Objective();
		o.setDescription(input);
		o.save();
		DragonsLairMain.debugLog("Created objective '" + o.getDescription() + "'");
		DragonsLairMain.getSettings().getObjectives().put(o.getID(), o);
		return new ObjectiveManageDialog();
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		return true;
	}
}
