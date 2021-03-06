package com.faithfulmc.hardcorefactions.faction.argument;

import com.faithfulmc.hardcorefactions.ConfigurationService;
import com.faithfulmc.hardcorefactions.HCF;
import com.faithfulmc.hardcorefactions.faction.type.Faction;
import com.faithfulmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionShowArgument extends CommandArgument {
    private final HCF plugin;

    public FactionShowArgument(final HCF plugin) {
        super("show", "Get details about a faction.", new String[]{"i", "info", "who"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName|factionName]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ConfigurationService.RED + "Usage: " + getUsage(label));
                return true;
            }
            else {
                namedFaction = plugin.getFactionManager().getPlayerFaction(((Player) sender).getUniqueId());
                if (namedFaction == null) {
                    sender.sendMessage(ConfigurationService.RED + "You are not in a faction.");
                    return true;
                }
            }
        } else {
            namedFaction = plugin.getFactionManager().getFaction(args[1]);
            playerFaction = plugin.getFactionManager().getFaction(args[1]);
            UUID targetUUID = plugin.getUserManager().fetchUUID(args[1]);
            if(targetUUID != null){
                playerFaction = plugin.getFactionManager().getPlayerFaction(targetUUID);
            }
            if (namedFaction == null && playerFaction == null) {
                sender.sendMessage(ConfigurationService.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
        }
        if (namedFaction != null) {
            namedFaction.printDetails(sender);
        }
        if (playerFaction != null && (namedFaction == null || !namedFaction.equals(playerFaction))) {
            playerFaction.printDetails(sender);
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        Player player = (Player) sender;
        List<String> results = new ArrayList<String>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                results.add(target.getName());
            }
        }
        return results;
    }
}