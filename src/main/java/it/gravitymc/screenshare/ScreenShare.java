package it.gravitymc.screenshare;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.gravitymc.screenshare.commands.SSAdminCommand;
import it.gravitymc.screenshare.commands.SSBanCommand;
import it.gravitymc.screenshare.commands.SSCommand;
import it.gravitymc.screenshare.commands.SSFinishCommand;
import it.gravitymc.screenshare.listeners.PlayerCommandListener;
import it.gravitymc.screenshare.managers.BanPanelManager;
import it.gravitymc.screenshare.managers.ScreenShareManager;
import it.gravitymc.screenshare.managers.SpawnManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Plugin(
    id = "screenshare",
    name = "ScreenShare",
    version = "2.0.0",
    description = "ScreenShare plugin per GravityMC",
    authors = {"ckanto"}
)
public class ScreenShare {

    private static ScreenShare instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private ScreenShareManager screenShareManager;
    private SpawnManager spawnManager;
    private BanPanelManager banPanelManager;
    private Map<String, Object> config;

    @Inject
    public ScreenShare(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        saveDefaultConfig();
        loadConfig();

        spawnManager       = new SpawnManager(this);
        screenShareManager = new ScreenShareManager(this);
        banPanelManager    = new BanPanelManager(this);

        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("ss").plugin(this).build(),
            new SSCommand(this));
        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("ssfinish").plugin(this).build(),
            new SSFinishCommand(this));
        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("screenshareadmin").plugin(this).build(),
            new SSAdminCommand(this));
        server.getCommandManager().register(
            server.getCommandManager().metaBuilder("ssban").plugin(this).build(),
            new SSBanCommand(this));

        server.getEventManager().register(this, new PlayerCommandListener(this));

        logger.info("ScreenShare plugin avviato v2.0.0 - GravityMC");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        screenShareManager.endAllSessions();
        logger.info("ScreenShare plugin disabilitato.");
    }

    private void saveDefaultConfig() {
        if (!Files.exists(dataDirectory)) {
            try { Files.createDirectories(dataDirectory); }
            catch (IOException e) { logger.error("Impossibile creare la cartella del plugin", e); return; }
        }
        Path configPath = dataDirectory.resolve("config.yml");
        if (!Files.exists(configPath)) {
            try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                if (in != null) Files.copy(in, configPath);
            } catch (IOException e) { logger.error("Impossibile copiare il config.yml", e); }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        Path configPath = dataDirectory.resolve("config.yml");
        try (InputStream in = Files.newInputStream(configPath)) {
            config = new Yaml().load(in);
        } catch (IOException e) {
            logger.error("Impossibile caricare il config.yml", e);
            config = Map.of();
        }
    }

    public void saveConfig() {
        Path configPath = dataDirectory.resolve("config.yml");
        try { new Yaml().dump(config, Files.newBufferedWriter(configPath)); }
        catch (IOException e) { logger.error("Impossibile salvare il config.yml", e); }
    }

    @SuppressWarnings("unchecked")
    public String getString(String path, String defaultValue) {
        String[] parts = path.split("\\.");
        Object current = config;
        for (String part : parts) {
            if (current instanceof Map) current = ((Map<String, Object>) current).get(part);
            else return defaultValue;
        }
        return current != null ? current.toString() : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path) {
        String[] parts = path.split("\\.");
        Object current = config;
        for (String part : parts) {
            if (current instanceof Map) current = ((Map<String, Object>) current).get(part);
            else return List.of();
        }
        return current instanceof List ? (List<String>) current : List.of();
    }

    @SuppressWarnings("unchecked")
    public void setConfigValue(String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = config;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                Map<String, Object> newMap = new java.util.LinkedHashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            } else {
                current = (Map<String, Object>) next;
            }
        }
        current.put(parts[parts.length - 1], value);
    }

    public String getMsg(String key) {
        return getString("messages." + key, "&cMessaggio non trovato: " + key).replace("&", "\u00a7");
    }

    public String getMsgWithPlayer(String key, String player) {
        return getMsg(key).replace("%player%", player);
    }

    public net.kyori.adventure.text.Component colorize(String msg) {
        return LegacyComponentSerializer.legacySection().deserialize(msg);
    }

    public static ScreenShare getInstance()           { return instance; }
    public ProxyServer getServer()                    { return server; }
    public Logger getLogger()                         { return logger; }
    public Path getDataDirectory()                    { return dataDirectory; }
    public ScreenShareManager getScreenShareManager() { return screenShareManager; }
    public SpawnManager getSpawnManager()             { return spawnManager; }
    public BanPanelManager getBanPanelManager()       { return banPanelManager; }
}
