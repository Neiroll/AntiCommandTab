package com.sgtcaze.AntiCommandTab;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.FieldAccessException;

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
		return true;

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().hasPermission("lib.commandtab.bypass")) {
		} else {
			if (event.getMessage().equalsIgnoreCase("/plugins")
					|| event.getMessage().startsWith("/?")
					|| event.getMessage().equalsIgnoreCase("/pl")
					|| event.getMessage().equalsIgnoreCase("/about")
					|| event.getMessage().equalsIgnoreCase("/version")
					|| event.getMessage().equalsIgnoreCase("/ver")) {
				event.setCancelled(true);
				Player player = event.getPlayer();
				String DenyMessage = getConfig().getString("DenyMessage");
				DenyMessage = DenyMessage.replaceAll("&", "§").replaceAll(
						"%player", player.getPlayerListName());
				event.getPlayer().sendMessage(DenyMessage);
			}
		}
	}
}
