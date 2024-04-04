package me.syncwrld.messageprinter.listeners;

import java.util.List;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import me.syncwrld.messageprinter.other.GithubReleaseUpdateChecker;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NotifyUpdateListener implements Listener {

  private final GithubReleaseUpdateChecker updateChecker;

  public NotifyUpdateListener(MessagePrinterEngine printerEngine) {
    this.updateChecker =
        new GithubReleaseUpdateChecker(
            "syncwrld", "spigot-message-printer", printerEngine.getDescription().getVersion());
  }

  public static void register(MessagePrinterEngine printerEngine) {
    NotifyUpdateListener listener = new NotifyUpdateListener(printerEngine);
    printerEngine.getServer().getPluginManager().registerEvents(listener, printerEngine);
  }

  @EventHandler
  public void whenConnect(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (!player.hasPermission("messageprinter.admin")) {
      return;
    }

    boolean updateAvailable = updateChecker.isUpdateAvailable();
    if (!updateAvailable) return;

    String latestVersion = updateChecker.getLatestVersionTag();
    List<String> changelog = updateChecker.getModifications();
    String latestVersionDownloadURL = updateChecker.getLatestVersionDownloadURL();

    StringBuilder formattedChangelog = new StringBuilder();

    changelog.forEach(
        modification -> {
          formattedChangelog.append(" §7").append(modification).append("\n");
        });

    BaseComponent[] components =
        new ComponentBuilder("\n")
            .append("§bSpigotMessagePrinter §7» §aNew update available!")
            .append("\n")
            .append("§7 ✉ Changelog")
            .append("\n")
            .append(formattedChangelog.toString())
            .append("\n")
            .append("§7❙ Click here to download the latest version!")
            .event(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    TextComponent.fromLegacyText(
                        "§7Click here to download the latest version!"
                            + "\n"
                            + "§7Version: "
                            + latestVersion)))
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, latestVersionDownloadURL))
            .append("\n")
            .create();

    player.spigot().sendMessage(components);
  }
}
