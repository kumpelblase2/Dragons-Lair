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
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final PotionEffectType t = PotionEffectType.getByName(e.getOption("potiontype").toUpperCase().replace(" ", "_"));
			final String scope = e.getOption("scope");
			final int duration = Integer.parseInt(e.getOption("duration"));
			final int amplifier = Integer.parseInt(e.getOption("amplifier"));
			final PotionEffect pe = new PotionEffect(t, duration, amplifier);
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("player"))
				p.addPotionEffect(pe, true);
			else
			{
				final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
					for(final String member : ad.getCurrentParty().getMembers())
						Bukkit.getPlayerExact(member).addPotionEffect(pe, true);
			}
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return true;
	}
}
