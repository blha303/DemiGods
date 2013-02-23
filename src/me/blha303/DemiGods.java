package me.blha303;

import java.util.ArrayList;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.skills.utilities.SkillType;

public class DemiGods extends JavaPlugin implements Listener {
	private GroupManager groupManager;
	
	public void onEnable() {
		final PluginManager pluginManager = this.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
 
		if (GMplugin != null && GMplugin.isEnabled())
		{ groupManager = (GroupManager)GMplugin; }
		List<String> grouplist = new ArrayList<String>();
		grouplist.add("zeus");
		grouplist.add("ares");
		grouplist.add("artemis");
		grouplist.add("athena");
		grouplist.add("apollo");
		grouplist.add("hades");
		grouplist.add("aphrodite");
		
		getConfig().addDefault("restrictNumberOfChanges", true);
		getConfig().addDefault("listOfGroups", grouplist);
		getConfig().addDefault("numberOfChanges", 7);
		getConfig().addDefault("errorOnChangeLimitExceeded", "You've exceeded the path change limit.");
		getConfig().addDefault("pathChangeSuccessful", "Your path has been changed to honour %groupname%!");
		getConfig().addDefault("invalidPath", "Invalid path name! Use /path to see the list.");
		getConfig().addDefault("separator", "&f, &c");
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@SuppressWarnings("unchecked")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			player = null;
		}
		
		if (player != null) {
			if (getConfig().getInt(player.getName() + ".changes") > 7) {
				player.sendMessage(ChatColor.translateAlternateColorCodes
						('&', getConfig().getString("errorOnChangeLimitExceeded")));
				return true;
			}
			if (args.length == 1) {
				if (args[0] == "list") {
					String list = "";
					for (String name : (List<String>)getConfig().getList("listOfGroups")) {
						if (list == "") {
							list = name;
						} else {
							list = list + getConfig().getString("separator") + name;
						}
					}
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', list)); 
					return true; 
				}
				if (!setGroup(player, args[0])) {
					player.sendMessage(getConfig().getString("invalidPath"));
					return true;
				} else {
					getConfig().set(player.getName() + ".changes", getConfig().getInt(player.getName() + ".changes") + 1);
					saveConfig();
					player.sendMessage(getConfig().getString("pathChangeSuccessful").replaceAll("%groupname%", getGroup(player)));
					return true;
				}
			} else {
				return false;
			}
		} else {
			sender.sendMessage("Only players can use this");
			return false;
		}
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
		if (group.equalsIgnoreCase("zeus")) {
			float newxp = xp + (0.05f * xp);
			event.setXpGained(Math.round(newxp));
			return;
		} else if (group.equalsIgnoreCase("ares")) {
			switch (skill) {
			case UNARMED: case SWORDS: case ARCHERY: case AXES:
				float newxp = xp + (0.09f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		} else if (group.equalsIgnoreCase("artemis")) {
			switch (skill) {
			case TAMING: case FISHING:
				float newxp = xp + (0.15f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		} else if (group.equalsIgnoreCase("athena")) {
			switch (skill) {
			case ACROBATICS: case REPAIR:
				float newxp = xp + (0.15f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		} else if (group.equalsIgnoreCase("apollo")) {
			switch (skill) {
			case WOODCUTTING: case HERBALISM:
				float newxp = xp + (0.15f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		} else if (group.equalsIgnoreCase("hades")) {
			switch (skill) {
			case MINING:
				float newxp = xp + (0.17f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		} else if (group.equalsIgnoreCase("aphrodite")) {
			switch (skill) {
			case EXCAVATION:
				float newxp = xp + (0.17f * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}	
		}
	}
}
