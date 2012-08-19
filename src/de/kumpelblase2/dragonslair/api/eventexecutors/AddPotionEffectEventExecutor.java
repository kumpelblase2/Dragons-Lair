package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class AddPotionEffectEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			PotionEffectType t = PotionEffectType.getByName(e.getOption("potiontype").toUpperCase().replace(" ", "_"));
			String scope = e.getOption("scope");
			int duration = Integer.parseInt(e.getOption("duration"));
			int amplifier = Integer.parseInt(e.getOption("amplifier"));
			PotionEffect pe = new PotionEffect(t, duration, amplifier);
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("player"))
			{
				p.addPotionEffect(pe, true);
			}
			else
			{
				ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String member : ad.getCurrentParty().getMembers())
					{
						Bukkit.getPlayerExact(member).addPotionEffect(pe, true);
					}
				}
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return true;
	}

}
