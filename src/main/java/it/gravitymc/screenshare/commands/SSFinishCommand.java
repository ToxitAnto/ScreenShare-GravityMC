package it.gravitymc.screenshare.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;

public class SSFinishCommand implements SimpleCommand {

    private final ScreenShare plugin;

    public SSFinishCommand(ScreenShare plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player staff)) {
            invocation.source().sendMessage(plugin.colorize("&cSolo i giocatori possono usare questo comando."));
            return;
        }

        if (!staff.hasPermission("screenshare.staff")) {
            staff.sendMessage(plugin.colorize(plugin.getMsg("no-permission")));
            return;
        }

        boolean ended = plugin.getScreenShareManager().endSession(staff);

        if (ended) {
            staff.sendMessage(plugin.colorize(plugin.getMsg("ss-finish")));
        } else {
            staff.sendMessage(plugin.colorize(plugin.getMsg("ss-no-active")));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("screenshare.staff");
    }
}
