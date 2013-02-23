package me.blha303;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.skills.utilities.SkillType;

public class DVassPlugin extends JavaPlugin implements Listener {
	private GroupManager groupManager;
	
	public void onEnable() {
		final PluginManager pluginManager = this.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
 
		if (GMplugin != null && GMplugin.isEnabled())
		{ groupManager = (GroupManager)GMplugin; }
		
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			player = null;
		}
		
		if (player != null) {
			if (args.length == 1) {
				setGroup(player, args[0]);
				return true;
			}
		}
		
		return false;
	}
	
	//GroupManager methods
	public String getGroup(final Player base)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
		if (handler == null)
		{
			return null;
		}
		return handler.getGroup(base.getName());
	}
 
	public boolean setGroup(final Player base, final String group)
	{
		final OverloadedWorldHolder handler = groupManager.getWorldsHolder().getWorldData(base);
		if (handler == null)
		{
			return false;
		}
		handler.getUser(base.getName()).setGroup(handler.getGroup(group));
		return true;
	}
	//End GroupManager methods
	
	//MCMMO methods
	public void onPlayerGetXP(final McMMOPlayerXpGainEvent event) {
		Player player = event.getPlayer();
		SkillType skill = event.getSkill();
		int xp = event.getXpGained();
		String group = getGroup(player);
		if (group.equalsIgnoreCase("Zeus")) {
			int newxp = xp + (5/100 * xp);
			event.setXpGained(newxp);
			return;
		} else if (group.equalsIgnoreCase("ares")) {
			switch (skill) {
			case UNARMED: case SWORDS: case ARCHERY: case AXES:
				int newxp = xp + (9/100 * xp);
				event.setXpGained(newxp);
				break;
			default:
				return;
			}	
		}
	}
}
