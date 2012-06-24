package de.kumpelblase2.dragonslair;

import org.bukkit.command.*;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.commanddialogs.GeneralConfigDialog;
import de.kumpelblase2.dragonslair.commanddialogs.HelpDialog;

public class DLCommandExecutor implements CommandExecutor
{	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!cmd.getName().equals("dragonslair"))
			return false;
		
		if(args.length != 1)
			return false;
		
		if(args[0].equals("config"))
		{
			if(!sender.isPermissionSet("dragonslair.config"))
			{
				if(!sender.isOp())
					return false;
			}
			else
			{
				if(!sender.hasPermission("dragonslair.config"))
					return false;
			}
			
			if(sender instanceof Player || sender instanceof ConsoleCommandSender)
			{
				ConversationFactory f = DragonsLairMain.getInstance().getDungeonManager().getConversationFactory();
				Conversation c = f.withEscapeSequence("/exit").withFirstPrompt(new GeneralConfigDialog()).buildConversation((Conversable)sender);
				c.begin();
				return true;
			}
		}
		else if(args[0].equals("reload"))
		{
			if(!sender.isPermissionSet("dragonslair.config"))
			{
				if(!sender.isOp())
					return false;
			}
			else
			{
				if(!sender.hasPermission("dragonslair.config"))
					return false;
			}
			
			DragonsLairMain.Log.info("Reloading data...");
			DragonsLairMain.Log.info("Stopping all running dungeons...");
			DragonsLairMain.getInstance().getDungeonManager().stopDungeons();
			DragonsLairMain.Log.info("Reloading from database...");
			DragonsLairMain.getSettings().loadAll();
			DragonsLairMain.getInstance().getEventHandler().reloadTriggers();
			DragonsLairMain.getInstance().setupConfig();
			DragonsLairMain.Log.info("Reload complete!");
			return true;
		}
		else if(args[0].equals("help"))
		{
			if(sender instanceof Player || sender instanceof ConsoleCommandSender)
			{
				ConversationFactory f = DragonsLairMain.getInstance().getDungeonManager().getConversationFactory();
				Conversation c = f.withEscapeSequence("/exit").withFirstPrompt(new HelpDialog()).buildConversation((Conversable)sender);
				((Conversable)sender).beginConversation(c);
				return true;
			}
		}
		else
		{
			sender.sendMessage("No command like that.");
		}
		
		return false;
	}
}
