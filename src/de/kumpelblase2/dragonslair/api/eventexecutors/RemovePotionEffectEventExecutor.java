package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class RemovePotionEffectEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			PotionEffectType t = PotionEffectType.getByName(e.getOption("potiontype").toUpperCase().replace(" ", "_"));
			String scope = e.getOption("scope");
			String time = e.getOption("time");
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("player"))
			{
				PotionEffect eff = null;
				for(PotionEffect effect : p.getActivePotionEffects())
				{
					if(effect.getType() == t)
					{
						eff = new PotionEffect(t, (time != null) ? Integer.parseInt(time) - effect.getDuration() * 20 : 0, effect.getAmplifier());
					}
				}
				p.addPotionEffect(eff, true);
			}
			else
			{
				ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String member : ad.getCurrentParty().getMembers())
					{
						Player pl = Bukkit.getPlayer(member);
						PotionEffect eff = null;
						for(PotionEffect effect : pl.getActivePotionEffects())
						{
							if(effect.getType() == t)
							{
								eff = new PotionEffect(t, (time != null) ? Integer.parseInt(time) - effect.getDuration() * 20 : 0, effect.getAmplifier());
							}
						}
						pl.addPotionEffect(eff, true);
					}
				}
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return false;
	}

}
