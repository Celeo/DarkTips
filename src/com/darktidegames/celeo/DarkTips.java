package com.darktidegames.celeo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Celeo
 */
public class DarkTips extends JavaPlugin
{

	private long interval = 3600L;
	private List<String> tips = new ArrayList<String>();

	@Override
	public void onDisable()
	{
		save();
		log("Disabled");
	}

	@Override
	public void onEnable()
	{
		getDataFolder().mkdirs();
		if (new File(getDataFolder(), "config.yml").exists())
			load();
		else
			saveDefaultConfig();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				if (getServer().getOnlinePlayers().length > 0)
					broadcastRandomTip();
			}
		}, 3600L, interval);
		getCommand("darktips").setExecutor(this);
		log("Enabled");
	}

	public void load()
	{
		reloadConfig();
		interval = getConfig().getLong("interval", 3600);
		tips.clear();
		tips = getConfig().getStringList("tips");
		if (tips == null)
			tips = new ArrayList<String>();
	}

	@SuppressWarnings("boxing")
	public void save()
	{
		getConfig().set("interval", interval);
		getConfig().set("tips", tips);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		if (!player.hasPermission("sudo.admin"))
		{
			player.sendMessage("§cNo.");
			return true;
		}
		if (args == null || args.length == 0)
		{
			player.sendMessage("§c/tipme new|list|save|reload");
			return true;
		}
		String param = args[0].toLowerCase();
		if (param.equals("new"))
		{
			if (args.length == 1)
			{
				player.sendMessage("§c/tipme new [message ...]");
				return true;
			}
			String tip = "";
			for (int i = 1; i < args.length; i++)
			{
				if (tip.equals(""))
					tip = args[i];
				else
					tip += " " + args[i];
			}
			tips.add(tip);
			player.sendMessage("§aTip: §7" + tip + " §aadded");
			return true;
		}
		if (param.equals("list"))
		{
			player.sendMessage("§9Tips:");
			for (String tip : tips)
				player.sendMessage(tip);
			return true;
		}
		if (param.equals("reload"))
		{
			load();
			player.sendMessage("§aReloaded");
			return true;
		}
		if (param.equals("save"))
		{
			save();
			player.sendMessage("§aSaved");
			return true;
		}
		if (param.equals("now"))
		{
			broadcastRandomTip();
			return true;
		}
		player.sendMessage("§c/tipme new|list|save|reload");
		return false;
	}

	public void broadcastRandomTip()
	{
		if (tips == null || tips.size() == 0)
			return;
		int i = new Random().nextInt(tips.size());
		getServer().broadcastMessage("§8[§6Tip§8] §f"
				+ tips.get(i).replaceAll("&", "§"));
	}

	public void log(String message)
	{
		getLogger().info("[DarkTips] " + message);
	}

}