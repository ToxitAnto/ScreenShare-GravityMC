package it.gravitymc.screenshare.managers;

import com.velocitypowered.api.proxy.Player;
import it.gravitymc.screenshare.ScreenShare;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Pannello ban inviato in chat allo staffer con pulsanti cliccabili (Adventure ClickEvent).
 * Velocity è un proxy e non ha inventory GUI lato server, quindi usiamo componenti chat.
 *
 * Pulsanti:
 *   [BAN 30G]  → esegue /ssban <player> 30d
 *   [AMMISSIONE] → esegue /ssban <player> ammissione
 *   [BAN CUSTOM] → suggerisce /ssban <player> custom <motivo>
 *   [LIBERA]   → esegue /ssfinish
 */
public class BanPanelManager {

    private final ScreenShare plugin;

    public BanPanelManager(ScreenShare plugin) {
        this.plugin = plugin;
    }

    public void openPanel(Player staff, String targetName) {
        String ban30dCmd    = plugin.getString("commands.ban-30d", "ban %player% 30d [SS] Cheating").replace("%player%", targetName);
        String banAdmCmd    = plugin.getString("commands.ban-ammissione", "ban %player% [SS] Ammissione hack").replace("%player%", targetName);
        String banCustomSug = "/ssban " + targetName + " custom ";
        String unfreezeCmd  = "ssfinish";

        Component header = LegacyComponentSerializer.legacySection().deserialize(
            "\u00a78\u00a7m--------------------\u00a7r \u00a7c\u00a7lPANNELLO SS \u00a78\u00a7m--------------------"
        );

        Component targetLine = LegacyComponentSerializer.legacySection().deserialize(
            "\u00a77Target: \u00a7c\u00a7l" + targetName
        );

        Component footer = LegacyComponentSerializer.legacySection().deserialize(
            "\u00a78\u00a7m-------------------------------------------"
        );

        // --- Pulsante BAN 30 GIORNI ---
        Component btn30d = Component.text("[BAN 30G]")
            .color(NamedTextColor.RED)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand("/ssban " + targetName + " 30d"))
            .hoverEvent(HoverEvent.showText(
                Component.text("Banna " + targetName + " per 30 giorni\n")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Comando: /" + ban30dCmd).color(NamedTextColor.YELLOW))
            ));

        // --- Pulsante BAN AMMISSIONE ---
        Component btnAdm = Component.text("  [AMMISSIONE]")
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand("/ssban " + targetName + " ammissione"))
            .hoverEvent(HoverEvent.showText(
                Component.text("Banna " + targetName + " per ammissione\n")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Comando: /" + banAdmCmd).color(NamedTextColor.YELLOW))
            ));

        // --- Pulsante BAN CUSTOM ---
        Component btnCustom = Component.text("  [BAN CUSTOM]")
            .color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.suggestCommand(banCustomSug))
            .hoverEvent(HoverEvent.showText(
                Component.text("Inserisci un motivo personalizzato\n")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Scrivi il motivo dopo il comando").color(NamedTextColor.YELLOW))
            ));

        // --- Pulsante LIBERA ---
        Component btnFree = Component.text("  [LIBERA \u2714]")
            .color(NamedTextColor.GREEN)
            .decorate(TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand("/" + unfreezeCmd))
            .hoverEvent(HoverEvent.showText(
                Component.text(targetName + " \u00e8 pulito, termina la sessione SS")
                    .color(NamedTextColor.GRAY)
            ));

        Component buttons = Component.empty()
            .append(btn30d)
            .append(btnAdm)
            .append(btnCustom)
            .append(btnFree);

        staff.sendMessage(header);
        staff.sendMessage(targetLine);
        staff.sendMessage(Component.empty());
        staff.sendMessage(buttons);
        staff.sendMessage(Component.empty());
        staff.sendMessage(footer);
    }
}
