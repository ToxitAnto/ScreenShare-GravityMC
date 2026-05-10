package it.gravitymc.screenshare.managers;

import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ScreenShareManager {

    private final ScreenShare plugin;
    // staffUUID -> targetUUID
    private final Map<UUID, UUID> activeSessions = new HashMap<>();

    public ScreenShareManager(ScreenShare plugin) {
        this.plugin = plugin;
    }

    public boolean startSession(Player staff, Player target) {
        if (isInSession(target)) {
            return false;
        }

        activeSessions.put(staff.getUniqueId(), target.getUniqueId());

        String staffRank   = plugin.getString("ranks.staff-rank", "STAFF");
        String suspectRank = plugin.getString("ranks.suspect-rank", "SOSPETTO");
        String staffColor  = getColorCode(plugin.getString("ranks.staff-helper-color", "BLUE"));
        String suspectColor = "\u00a7c";

        // Notifica il sospettato
        target.sendMessage(plugin.colorize(plugin.getMsg("ss-notify-target")));

        SpawnManager spawnManager = plugin.getSpawnManager();

        if (!spawnManager.isSpawnSet()) {
            staff.sendMessage(plugin.colorize(plugin.getMsg("spawn-not-set")));
            return false;
        }

        // FIX 1: porta il sospettato nel server SS
        spawnManager.teleportToSpawn(target);

        // FIX 2: porta anche lo STAFFER nel server SS
        spawnManager.teleportToSpawn(staff);

        // FIX 3: imposta i nomi nel tab list con i rank corretti
        setTabName(staff,  staffColor  + "[" + staffRank   + "] " + staff.getUsername());
        setTabName(target, suspectColor + "[" + suspectRank + "] " + target.getUsername());

        return true;
    }

    public boolean endSession(Player staff) {
        if (!activeSessions.containsKey(staff.getUniqueId())) {
            return false;
        }

        UUID targetUUID = activeSessions.remove(staff.getUniqueId());
        Optional<Player> targetOpt = plugin.getServer().getPlayer(targetUUID);

        resetTabName(staff);
        targetOpt.ifPresent(this::resetTabName);

        return true;
    }

    public void endAllSessions() {
        for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
            plugin.getServer().getPlayer(entry.getKey()).ifPresent(this::resetTabName);
            plugin.getServer().getPlayer(entry.getValue()).ifPresent(this::resetTabName);
        }
        activeSessions.clear();
    }

    public boolean isInSession(Player player) {
        return activeSessions.containsKey(player.getUniqueId())
                || activeSessions.containsValue(player.getUniqueId());
    }

    public boolean isSuspect(Player player) {
        return activeSessions.containsValue(player.getUniqueId());
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    // Restituisce l'UUID dello staff che ha in sessione il target dato
    public Optional<UUID> getStaffOf(Player target) {
        return activeSessions.entrySet().stream()
                .filter(e -> e.getValue().equals(target.getUniqueId()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private void setTabName(Player player, String name) {
        Component component = LegacyComponentSerializer.legacySection().deserialize(name);
        player.getTabList().getEntry(player.getUniqueId())
              .ifPresent(entry -> entry.setDisplayName(component));
    }

    private void resetTabName(Player player) {
        player.getTabList().getEntry(player.getUniqueId())
              .ifPresent(entry -> entry.setDisplayName(Component.text(player.getUsername())));
    }

    private String getColorCode(String colorName) {
        return switch (colorName.toUpperCase()) {
            case "BLUE"     -> "\u00a79";
            case "RED"      -> "\u00a7c";
            case "GREEN"    -> "\u00a7a";
            case "YELLOW"   -> "\u00a7e";
            case "GOLD"     -> "\u00a76";
            case "DARK_RED" -> "\u00a74";
            case "AQUA"     -> "\u00a7b";
            default         -> "\u00a7f";
        };
    }
}
