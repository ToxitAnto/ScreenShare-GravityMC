package it.gravitymc.screenshare.managers;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.gravitymc.screenshare.ScreenShare;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Gestisce lo spawn e il teleport nel server SS.
 *
 * Velocity connette il giocatore al server SS, poi invia le coordinate
 * tramite il canale BungeeCord "Forward" (built-in in Paper/Spigot).
 * Il server Paper riceve il messaggio sul canale "screenshare:teleport"
 * ed esegue il teleport alle coordinate salvate nel config.
 *
 * Le coordinate si impostano con:
 *   /screenshareadmin setspawn <x> <y> <z> [yaw] [pitch]
 * oppure direttamente nel config.yml.
 */
public class SpawnManager {

    private static final MinecraftChannelIdentifier BUNGEECORD =
        MinecraftChannelIdentifier.from("bungeecord:main");

    private final ScreenShare plugin;

    public SpawnManager(ScreenShare plugin) {
        this.plugin = plugin;
        plugin.getServer().getChannelRegistrar().register(BUNGEECORD);
    }

    /** Salva server + coordinate nel config. */
    public void setSpawn(String serverName, double x, double y, double z, float yaw, float pitch) {
        plugin.setConfigValue("spawn.server", serverName);
        plugin.setConfigValue("spawn.x", x);
        plugin.setConfigValue("spawn.y", y);
        plugin.setConfigValue("spawn.z", z);
        plugin.setConfigValue("spawn.yaw", (double) yaw);
        plugin.setConfigValue("spawn.pitch", (double) pitch);
        plugin.saveConfig();
    }

    /**
     * Connette il giocatore al server SS, poi dopo 1.5s invia
     * tramite canale BungeeCord "Forward" le coordinate di teleport.
     * Il server Paper le riceve sul canale "screenshare:teleport".
     */
    public void teleportToSpawn(Player player) {
        String serverName = plugin.getString("spawn.server", "");
        if (serverName.isEmpty()) return;

        Optional<RegisteredServer> serverOpt = plugin.getServer().getServer(serverName);
        if (serverOpt.isEmpty()) return;

        player.createConnectionRequest(serverOpt.get()).connectWithIndication()
            .thenAccept(success -> {
                if (!success) return;
                plugin.getServer().getScheduler()
                    .buildTask(plugin, () -> sendForward(player, serverName))
                    .delay(1500L, TimeUnit.MILLISECONDS)
                    .schedule();
            });
    }

    /**
     * Invia le coordinate al server Paper tramite BungeeCord "Forward".
     * Formato payload interno: playerName, x, y, z, yaw, pitch.
     */
    private void sendForward(Player player, String serverName) {
        double x     = parseD(plugin.getString("spawn.x", "0"));
        double y     = parseD(plugin.getString("spawn.y", "64"));
        double z     = parseD(plugin.getString("spawn.z", "0"));
        float  yaw   = (float) parseD(plugin.getString("spawn.yaw", "0"));
        float  pitch = (float) parseD(plugin.getString("spawn.pitch", "0"));

        try {
            ByteArrayOutputStream innerBuf = new ByteArrayOutputStream();
            DataOutputStream innerOut = new DataOutputStream(innerBuf);
            innerOut.writeUTF(player.getUsername());
            innerOut.writeDouble(x);
            innerOut.writeDouble(y);
            innerOut.writeDouble(z);
            innerOut.writeFloat(yaw);
            innerOut.writeFloat(pitch);
            byte[] innerData = innerBuf.toByteArray();

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buf);
            out.writeUTF("Forward");
            out.writeUTF(serverName);
            out.writeUTF("screenshare:teleport");
            out.writeShort(innerData.length);
            out.write(innerData);

            player.getCurrentServer().ifPresent(conn ->
                conn.sendPluginMessage(BUNGEECORD, buf.toByteArray())
            );
        } catch (Exception e) {
            plugin.getLogger().error("Errore invio teleport per " + player.getUsername() + ": " + e.getMessage());
        }
    }

    public boolean isSpawnSet() {
        String serverName = plugin.getString("spawn.server", "");
        if (serverName.isEmpty()) return false;
        return plugin.getServer().getServer(serverName).isPresent();
    }

    private double parseD(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0; }
    }
}
