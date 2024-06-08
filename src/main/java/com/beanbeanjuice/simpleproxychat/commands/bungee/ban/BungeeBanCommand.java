package com.beanbeanjuice.simpleproxychat.commands.bungee.ban;

import com.beanbeanjuice.simpleproxychat.SimpleProxyChatBungee;
import com.beanbeanjuice.simpleproxychat.utility.Helper;
import com.beanbeanjuice.simpleproxychat.utility.Tuple;
import com.beanbeanjuice.simpleproxychat.utility.config.Config;
import com.beanbeanjuice.simpleproxychat.utility.config.ConfigDataKey;
import com.beanbeanjuice.simpleproxychat.utility.config.Permission;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;

public class BungeeBanCommand extends Command implements TabExecutor {

    private final SimpleProxyChatBungee plugin;
    private final Config config;

    public BungeeBanCommand(final SimpleProxyChatBungee plugin) {
        super("Spc-ban", Permission.COMMAND_BAN.getPermissionNode());
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!config.getAsBoolean(ConfigDataKey.USE_SIMPLE_PROXY_CHAT_BANNING_SYSTEM)) {
            sender.sendMessage(Helper.convertToBungee("&cThe banning system is disabled..."));
            return;
        }

        if (args.length != 1) {
            String errorMessage = config.getAsString(ConfigDataKey.MINECRAFT_COMMAND_PROXY_BAN_USAGE);
            sender.sendMessage(Helper.convertToBungee(errorMessage));
            return;
        }

        String playerName = args[0];
        plugin.getBanHelper().addBan(playerName);
        plugin.getProxy().getPlayer(playerName).disconnect(Helper.convertToBungee("&cYou have been banned from the proxy."));

        String bannedMessage = config.getAsString(ConfigDataKey.MINECRAFT_COMMAND_PROXY_BAN_BANNED);
        bannedMessage = Helper.replaceKeys(
                bannedMessage,
                Tuple.of("plugin-prefix", config.getAsString(ConfigDataKey.PLUGIN_PREFIX)),
                Tuple.of("player", playerName)
        );

        sender.sendMessage(Helper.convertToBungee(bannedMessage));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getProxy().getPlayers()
                    .stream()
                    .map(CommandSender::getName)
                    .filter((bannedPlayer) -> bannedPlayer.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
