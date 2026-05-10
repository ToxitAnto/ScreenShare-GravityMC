package it.gravitymc.screenshare.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;

import java.util.Optional;

public class SSCommand implements SimpleCommand {

    private final ScreenShare plugin;

    public SSCommand(ScreenShare plugin) {
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

        String[] args = invocation.arguments();

        if (args.length < 1) {
            staff.sendMessage(plugin.colorize(plugin.getMsg("prefix") + "\u00a7eUso: /ss <giocatore>"));
            return;
        }

        Optional<Player> targetOpt = plugin.getServer().getPlayer(args[0]);

        if (targetOpt.isEmpty()) {
            staff.sendMessage(plugin.colorize(plugin.getMsg("ss-no-player")));
            return;
        }

        Player target = targetOpt.get();

        if (plugin.getScreenShareManager().isInSession(target)) {
            staff.sendMessage(plugin.colorize(plugin.getMsgWithPlayer("ss-already-in", target.getUsername())));
            return;
        }

        boolean started = plugin.getScreenShareManager().startSession(staff, target);

        if (started) {
            staff.sendMessage(plugin.colorize(plugin.getMsgWithPlayer("ss-start", target.getUsername())));
            // Apri il pannello ban dopo aver avviato la sessione
            plugin.getBanPanelManager().openPanel(staff, target.getUsername());
        } else {
            staff.sendMessage(plugin.colorize(plugin.getMsgWithPlayer("ss-already-in", target.getUsername())));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("screenshare.staff");
    }
}
