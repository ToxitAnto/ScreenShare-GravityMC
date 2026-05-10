package it.gravitymc.screenshare.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;

import java.util.List;

public class PlayerCommandListener {

    private final ScreenShare plugin;

    public PlayerCommandListener(ScreenShare plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player)) {
            return;
        }

        if (!plugin.getScreenShareManager().isSuspect(player)) {
            return;
        }

        String fullCommand = event.getCommand().toLowerCase();
        String commandUsed = fullCommand.split(" ")[0];

        List<String> restricted = plugin.getStringList("restricted-commands");

        for (String blocked : restricted) {
            if (commandUsed.equalsIgnoreCase(blocked)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                player.sendMessage(plugin.colorize(plugin.getMsg("no-commands")));
                return;
            }
        }
    }
}
