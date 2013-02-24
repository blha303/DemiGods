package me.blha303;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.massivecraft.factions.event.PowerLossEvent;

public class DemiGods extends JavaPlugin implements Listener {
	private GroupManager groupManager;

	public void onEnable() {
		final PluginManager pluginManager = this.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");

		if (GMplugin != null && GMplugin.isEnabled()) {
			groupManager = (GroupManager) GMplugin;
		}
		List<String> grouplist = new ArrayList<String>();
		grouplist.add("zeus");
		grouplist.add("ares");
		grouplist.add("artemis");
		grouplist.add("athena");
		grouplist.add("apollo");
		grouplist.add("hades");
		grouplist.add("aphrodite");
		grouplist.add("poseidon");

		getConfig().addDefault("restrictNumberOfChanges", true);
		getConfig().addDefault("listOfGroups", grouplist);
		getConfig().addDefault("numberOfChanges", 7);
		getConfig().addDefault("errorOnChangeLimitExceeded",
				"You've exceeded the path change limit.");
		getConfig().addDefault("pathChangeSuccessful",
				"Your path has been changed to honour %groupname%!");
		getConfig().addDefault("invalidPath",
				"Invalid path name! Use /path to see the list.");
		getConfig().addDefault("hadesMercy",
				"Hades pitied you! You did not lose any power.");
		getConfig().addDefault("zeusBoost", "0.05");
		getConfig().addDefault("aresBoost", "0.09");
		getConfig().addDefault("hadesBoost", "0.17");
		getConfig().addDefault("aphroditeBoost", "0.17");
		getConfig().addDefault("artemisBoost", "0.15");
		getConfig().addDefault("apolloBoost", "0.15");
		getConfig().addDefault("athenaBoost", "0.15");
		getConfig().addDefault("poseidonBoost", "0.20");
		getConfig().addDefault("hadesChance", "0.50");
		getConfig().addDefault("separator", "&f, &c");
		getConfig().options().copyDefaults(true);
		saveConfig();

		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@SuppressWarnings("unchecked")
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			player = null;
		}

		if (player != null) {
			if (getConfig().getInt(player.getName() + ".changes") > 7) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						getConfig().getString("errorOnChangeLimitExceeded")));
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					String list = "";
					for (String name : (List<String>) getConfig().getList(
							"listOfGroups")) {
						if (list == "") {
							list = name;
						} else {
							list = list + getConfig().getString("separator")
									+ name;
						}
					}
					player.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', list));
					return true;
				} else {

					if (!setGroup(player, args[0])) {
						player.sendMessage(getConfig().getString("invalidPath"));
						return true;
					} else {
						getConfig().set(
								player.getName() + ".changes",
								getConfig().getInt(player.getName() + ".changes") + 1);
						saveConfig();
						player.sendMessage(getConfig().getString(
								"pathChangeSuccessful").replaceAll(
								"%groupname%", getGroup(player)));
						return true;
					}
				}
			} else {
				return false;
			}
		} else {
			sender.sendMessage("Only players can use this");
			return false;
		}
	}

	// GroupManager methods
	public String getGroup(final Player base) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(base);
		if (handler == null) {
			return null;
		}
		return handler.getGroup(base.getName());
	}

	public boolean setGroup(final Player base, final String group) {
		final OverloadedWorldHolder handler = groupManager.getWorldsHolder()
				.getWorldData(base);
		if (handler == null) {
			return false;
		}

		try {
			handler.getUser(base.getName()).setGroup(handler.getGroup(group));
		} catch (NullPointerException name) {
			return false;
		}
		return true;
	}

	// End GroupManager methods

	@EventHandler
	public void onPowerLoss(final PowerLossEvent event) {
		Player player = event.getPlayer();
		String group = getGroup(player);
		if (group.equalsIgnoreCase("hades")) {
			Random r = new Random();
  			float chance = r.nextFloat();  			

 			if (chance <= Float.parseFloat(getConfig().getString("hadesChance"))) {
   				event.setCancelled(true);
   				player.sendMessage(getConfig().getString(
								"hadesMercy"));
   			}
		}
	}
	// MCMMO methods
	@EventHandler
	public void onPlayerGetXP(final McMMOPlayerXpGainEvent event) {
		Player player = event.getPlayer();
		SkillType skill = event.getSkill();
		int xp = event.getXpGained();
		String group = getGroup(player);
		if (group.equalsIgnoreCase("zeus")) {
			float newxp = xp + (Float.parseFloat(getConfig().getString("zeusBoost")) * xp);
			event.setXpGained(Math.round(newxp));
			return;
		} else if (group.equalsIgnoreCase("ares")) {
			switch (skill) {
			case UNARMED:
			case SWORDS:
			case ARCHERY:
			case AXES:
				float newxp = xp + (Float.parseFloat(getConfig().getString("aresBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("artemis")) {
			switch (skill) {
			case TAMING:
			case FISHING:
				float newxp = xp + (Float.parseFloat(getConfig().getString("artemisBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("athena")) {
			switch (skill) {
			case ACROBATICS:
			case REPAIR:
				float newxp = xp + (Float.parseFloat(getConfig().getString("athenaBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("apollo")) {
			switch (skill) {
			case WOODCUTTING:
			case HERBALISM:
				float newxp = xp + (Float.parseFloat(getConfig().getString("apolloBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("hades")) {
			switch (skill) {
			case MINING:
				float newxp = xp + (Float.parseFloat(getConfig().getString("hadesBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("aphrodite")) {
			switch (skill) {
			case EXCAVATION:
				float newxp = xp + (Float.parseFloat(getConfig().getString("aphroditeBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else if (group.equalsIgnoreCase("poseidon")) {
			switch (skill) {
			case EXCAVATION:
				float newxp = xp + (Float.parseFloat(getConfig().getString("poseidonBoost")) * xp);
				event.setXpGained(Math.round(newxp));
				break;
			default:
				return;
			}
		} else {
			player.sendMessage("Please choose a god to honour. Use /path");
		}
	}
}
