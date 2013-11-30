package com.sgtcaze.AntiCommandTab;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.FieldAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	// ProtocolLib Hook
	ProtocolManager protocolManager;

	public void onEnable() {
		saveDefaultConfig();

		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.protocolManager.addPacketListener(new PacketAdapter(this,
				ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL,
				new Integer[] { Integer.valueOf(203) }) {
			public void onPacketReceiving(PacketEvent event) {
				if (event.getPacketID() == 203)
					try {
						if (event.getPlayer().hasPermission(
								"lib.commandtab.bypass"))
							return;
						PacketContainer packet = event.getPacket();
						String message = (String) packet.getSpecificModifier(
								String.class).read(0);
						if ((message.startsWith("/"))
								&& (!message.contains(" "))
								|| ((message.startsWith("/ver")) && (!message
										.contains("  ")))
								|| ((message.startsWith("/version")) && (!message
										.contains("  ")))
								|| ((message.startsWith("/?")) && (!message
										.contains("  ")))
								|| ((message.startsWith("/about")) && (!message
										.contains("  ")))
								|| ((message.startsWith("/help")) && (!message
										.contains("  "))))
							event.setCancelled(true);
					} catch (FieldAccessException e) {
						Main.this.getLogger().log(Level.SEVERE,
								"Couldn't access field.", e);
					}
			}

		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();
		String[] msg = event.getMessage().split(" ");

		List<String> plugins = new ArrayList<String>();
		plugins.add("pl");
		plugins.add("plugins");

		List<String> version = new ArrayList<String>();
		version.add("ver");
		version.add("version");

		List<String> about = new ArrayList<String>();
		about.add("about");

		List<String> question = new ArrayList<String>();
		question.add("?");

		if (!event.getPlayer().hasPermission("lib.commandtab.bypass")) {

			if (getConfig().getBoolean("BlockPlugins")) {
				for (String Loop : plugins) {
					if (msg[0].equalsIgnoreCase("/" + Loop)) {
						String Plugins = getConfig()
								.getString("Plugins")
								.replaceAll("&", "§")
								.replaceAll("%player",
										event.getPlayer().getPlayerListName());
						event.getPlayer().sendMessage(Plugins);
						event.setCancelled(true);
					}
				}
			}

			if (getConfig().getBoolean("BlockVersion")) {
				for (String Loop : version) {
					if (msg[0].equalsIgnoreCase("/" + Loop)) {
						String Version = getConfig()
								.getString("Version")
								.replaceAll("&", "§")
								.replaceAll("%player",
										event.getPlayer().getPlayerListName());
						player.sendMessage(Version);
						event.setCancelled(true);
					}
				}
			}
			if (getConfig().getBoolean("BlockAbout")) {
				for (String Loop : about) {
					if (msg[0].equalsIgnoreCase("/" + Loop)) {
						String About = getConfig()
								.getString("About")
								.replaceAll("&", "§")
								.replaceAll("%player",
										event.getPlayer().getPlayerListName());
						player.sendMessage(About);
						event.setCancelled(true);
					}
				}
			}

			if (getConfig().getBoolean("BlockQuestionMark")) {
				for (String Loop : question) {
					if (msg[0].equalsIgnoreCase("/" + Loop)) {
						String QuestionMark = getConfig()
								.getString("QuestionMark")
								.replaceAll("&", "§")
								.replaceAll("%player",
										event.getPlayer().getPlayerListName());
						player.sendMessage(QuestionMark);
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("act")) {
			if (sender.hasPermission("act.reload")) {
				sender.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA
						+ "AntiCommandTab" + ChatColor.DARK_RED + "]"
						+ ChatColor.RED + " Reloaded configuration file.");
				reloadConfig();
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA
						+ "AntiCommandTab" + ChatColor.DARK_RED + "]"
						+ ChatColor.RED + " You do not have permission.");
			}
		}
		return false;
	}
}