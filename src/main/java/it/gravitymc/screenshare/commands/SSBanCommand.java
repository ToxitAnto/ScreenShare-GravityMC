package it.gravitymc.screenshare.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;

/**
 * /ssban <player> <tipo> [motivo custom...]
 * tipo: 30d | ammissione | custom
 *
 * Viene eseguito cliccando i pulsanti del pannello ban.
 */
public class SSBanCommand implements SimpleCommand {

    private final ScreenShare plugin;

    public SSBanCommand(ScreenShare plugin) {
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
        if (args.length < 2) {
            staff.sendMessage(plugin.colorize("&cUso: /ssban <player> <30d|ammissione|custom> [motivo]"));
            return;
        }

        String targetName = args[0];
        String tipo = args[1].toLowerCase();

        String banCmd;
        switch (tipo) {
            case "30d" -> {
                banCmd = plugin.getString("commands.ban-30d", "ban %player% 30d [SS] Cheating")
                    .replace("%player%", targetName);
            }
            case "ammissione" -> {
                banCmd = plugin.getString("commands.ban-ammissione", "ban %player% [SS] Ammissione hack")
                    .replace("%player%", targetName);
            }
            case "custom" -> {
                if (args.length < 3) {
                    staff.sendMessage(plugin.colorize("&cDevi specificare un motivo: /ssban " + targetName + " custom <motivo>"));
                    return;
                }
                String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                banCmd = plugin.getString("commands.ban-custom", "ban %player% [SS] %reason%")
                    .replace("%player%", targetName)
                    .replace("%reason%", reason);
            }
            default -> {
                staff.sendMessage(plugin.colorize("&cTipo non valido. Usa: 30d, ammissione, custom"));
                return;
            }
        }

        // Esegue il comando ban tramite console
        plugin.getServer().getCommandManager().executeAsync(
            plugin.getServer().getConsoleCommandSource(), banCmd
        );

        staff.sendMessage(plugin.colorize(plugin.getMsg("ban-executed")
            .replace("%player%", targetName)
            .replace("%type%", tipo)));

        // Termina la sessione SS automaticamente dopo il ban
        plugin.getScreenShareManager().endSession(staff);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("screenshare.staff");
    }
}
