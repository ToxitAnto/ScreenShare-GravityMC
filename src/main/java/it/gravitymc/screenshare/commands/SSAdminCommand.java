package it.gravitymc.screenshare.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;

/**
 * /screenshareadmin <subcomando>
 *
 * setspawn <x> <y> <z> [yaw] [pitch]  — salva le coordinate di spawn nel config
 * info                                  — mostra spawn attuale e sessioni attive
 * reload                                — ricarica il config.yml
 *
 * Perché le coordinate si passano come argomenti:
 * Velocity è un proxy e non ha accesso alla posizione del giocatore nel mondo Paper.
 * L'admin deve posizionarsi nel punto giusto, leggere le coordinate con F3
 * e passarle al comando. In alternativa può editare il config.yml direttamente.
 */
public class SSAdminCommand implements SimpleCommand {

    private final ScreenShare plugin;

    public SSAdminCommand(ScreenShare plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player admin)) {
            invocation.source().sendMessage(plugin.colorize("&cSolo i giocatori possono usare questo comando."));
            return;
        }

        if (!admin.hasPermission("screenshare.admin")) {
            admin.sendMessage(plugin.colorize(plugin.getMsg("no-permission")));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) { sendHelp(admin); return; }

        switch (args[0].toLowerCase()) {

            // /screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]
            case "setspawn" -> {
                if (args.length < 4) {
                    admin.sendMessage(plugin.colorize("&cUso: /screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]"));
                    admin.sendMessage(plugin.colorize("&7Apri F3 sul server SS per leggere le coordinate."));
                    return;
                }

                double x, y, z;
                float yaw = 0f, pitch = 0f;
                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                    if (args.length >= 5) yaw   = Float.parseFloat(args[4]);
                    if (args.length >= 6) pitch = Float.parseFloat(args[5]);
                } catch (NumberFormatException e) {
                    admin.sendMessage(plugin.colorize("&cCoordinate non valide. Usa numeri (es. 100.5 64 -200)"));
                    return;
                }

                // Ricava il server SS dalla connessione corrente dell'admin
                String serverName = admin.getCurrentServer()
                    .map(c -> c.getServerInfo().getName())
                    .orElse(plugin.getString("spawn.server", ""));

                if (serverName.isEmpty()) {
                    admin.sendMessage(plugin.colorize("&cNon riesci a determinare il server. Connettiti al server SS prima."));
                    return;
                }

                plugin.getSpawnManager().setSpawn(serverName, x, y, z, yaw, pitch);

                admin.sendMessage(plugin.colorize(plugin.getMsg("spawn-set")));
                admin.sendMessage(plugin.colorize(String.format(
                    "&7Server: &e%s &7| Coordinate: &eX=%.2f Y=%.2f Z=%.2f &7| Direzione: &eYaw=%.1f Pitch=%.1f",
                    serverName, x, y, z, yaw, pitch
                )));
            }

            // /screenshareadmin info
            case "info" -> {
                String server = plugin.getString("spawn.server", "&cnon impostato");
                double x  = parseD(plugin.getString("spawn.x", "0"));
                double y  = parseD(plugin.getString("spawn.y", "0"));
                double z  = parseD(plugin.getString("spawn.z", "0"));
                float  yw = (float) parseD(plugin.getString("spawn.yaw", "0"));
                float  pt = (float) parseD(plugin.getString("spawn.pitch", "0"));
                int sessions = plugin.getScreenShareManager().getActiveSessionCount();

                admin.sendMessage(plugin.colorize("&8&m----&r &dInfo Spawn SS &8&m----"));
                admin.sendMessage(plugin.colorize("&7Server SS: &e" + server));
                admin.sendMessage(plugin.colorize(String.format("&7Coordinate: &eX=%.2f Y=%.2f Z=%.2f", x, y, z)));
                admin.sendMessage(plugin.colorize(String.format("&7Direzione:  &eYaw=%.1f Pitch=%.1f", yw, pt)));
                admin.sendMessage(plugin.colorize("&7Sessioni SS attive: &e" + sessions));
            }

            // /screenshareadmin reload
            case "reload" -> {
                plugin.loadConfig();
                admin.sendMessage(plugin.colorize(plugin.getMsg("prefix") + "&aConfig ricaricato!"));
            }

            default -> sendHelp(admin);
        }
    }

    private void sendHelp(Player admin) {
        admin.sendMessage(plugin.colorize("&8&m----&r &dScreenShare Admin &8&m----"));
        admin.sendMessage(plugin.colorize("&e/screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]"));
        admin.sendMessage(plugin.colorize("  &7Imposta le coordinate di spawn del server SS"));
        admin.sendMessage(plugin.colorize("&e/screenshareadmin info"));
        admin.sendMessage(plugin.colorize("  &7Mostra spawn attuale e sessioni attive"));
        admin.sendMessage(plugin.colorize("&e/screenshareadmin reload"));
        admin.sendMessage(plugin.colorize("  &7Ricarica il config.yml"));
    }

    private double parseD(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0; }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("screenshare.admin");
    }
}
