package me.syncwrld.messageprinter.api.impl;

import com.cryptomorin.xseries.XSound;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.Getter;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;
import me.syncwrld.messageprinter.model.PrintableMessage;
import me.syncwrld.messageprinter.registry.PrintableMessageLoader;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotPrinterApiImpl implements SpigotPrinterAPI {

  @Getter(AccessLevel.PRIVATE)
  private final MessagePrinterEngine printerEngine;

  @Getter(AccessLevel.PUBLIC)
  private final PrintableMessageLoader messageLoader;

  public SpigotPrinterApiImpl(
      MessagePrinterEngine printerEngine, PrintableMessageLoader messageLoader) {
    this.printerEngine = printerEngine;
    this.messageLoader = messageLoader;
  }

  /**
   * A method to print a list of strings with a delay to specified players.
   *
   * @param lines the list of strings to be printed
   * @param delayTicks the delay in ticks before printing
   * @param players the players to whom the lines will be printed
   */
  @Override
  public void print(List<String> lines, int delayTicks, Player... players) {
    PrintableMessage printableMessage = PrintableMessage.createTest(lines, delayTicks);
    this.print(printableMessage, players);
  }

  /**
   * A method to print a printable message for a specific player.
   *
   * @param printableMessage the message to be printed
   * @param player the player to whom the message will be printed
   */
  @Override
  public void print(PrintableMessage printableMessage, Player player) {
    this.print(printableMessage, new Player[] {player});
  }

  /**
   * Print the given printable message to the specified players with optional delay.
   *
   * @param printableMessage the message to be printed
   * @param players the players to receive the message
   */
  @Override
  public void print(PrintableMessage printableMessage, Player... players) {
    final List<String> lines = printableMessage.getLines();
    final long delaySeconds = (long) (printableMessage.getDelayBetween() * 20);

    if (printableMessage.isSendToAllPlayers()) {
      players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
    }

    if (delaySeconds <= 0) {
      for (Player player : players) {
        for (String line : lines) {
          player.sendMessage(this.format(line, player));
        }
      }
      return;
    }

    final AtomicInteger index = new AtomicInteger();
    final BukkitTask[] task = new BukkitTask[1];
    final Player[] finalPlayers = players;

    task[0] =
        Bukkit.getScheduler()
            .runTaskTimer(
                this.printerEngine,
                () -> {
                  if (index.get() < lines.size()) {
                    String line = lines.get(index.getAndIncrement());
                    for (Player player : finalPlayers) {
                      player.sendMessage(this.format(line, player));

                      if (printableMessage.getSound() == null) {
                        continue;
                      }

                      Sound sound = getSound(printableMessage.getSound());
                      if (sound == null) {
                        continue;
                      }

                      if (printableMessage.isPlaySoundInEveryLine() || (index.get() == 0)) {
                        player.playSound(player.getLocation(), sound, 1, 1);
                      }
                    }
                  } else {
                    task[0].cancel();
                  }
                },
                0L,
                delaySeconds);
  }

  /**
   * Registers a message with the given ID and printable message.
   *
   * @param id the ID of the message to register
   * @param printableMessage the printable message to register
   */
  @Override
  public void registerMessage(String id, PrintableMessage printableMessage) {
    this.messageLoader.register(id, printableMessage);
  }

  /**
   * Unregisters a message by its ID.
   *
   * @param id the ID of the message to unregister
   */
  @Override
  public void unregisterMessage(String id) {
    this.messageLoader.unregisterById(id);
  }

  /**
   * Check if the given ID is registered.
   *
   * @param id the ID to check
   * @return true if the ID is registered, false otherwise
   */
  @Override
  public boolean isRegistered(String id) {
    return this.messageLoader.getRegisteredById(id).isPresent();
  }

  /**
   * Retrieves a registered PrintableMessage by its ID.
   *
   * @param id the ID of the PrintableMessage to retrieve
   * @return an Optional containing the retrieved PrintableMessage, or empty if not found
   */
  @Override
  public Optional<PrintableMessage> getRegisteredById(String id) {
    return this.messageLoader.getRegisteredById(id);
  }

  @Override
  public String getIdOf(PrintableMessage message) {
    return this.messageLoader.getIdOf(message);
  }

  /**
   * Retrieves the list of registered printable messages.
   *
   * @return the list of registered printable messages
   */
  @Override
  public List<PrintableMessage> getRegistered() {
    return this.messageLoader.getRegistered();
  }

  @Override
  public JavaPlugin getRegistor() {
    return printerEngine;
  }

  /**
   * Replaces special characters in the message with player-specific information.
   *
   * @param message the message to be formatted
   * @param player the player whose information will be used for formatting
   * @return the formatted message with player-specific information
   */
  private String format(String message, Player player) {
    return IridiumColorAPI.process(
        message
            .replace("&", "§")
            .replace("$player", player.getName())
            .replace("$name", player.getName())
            .replace("$displayName", player.getDisplayName())
            .replace("$ip", player.getAddress().getAddress().getHostAddress()));
  }

  private Sound getSound(String id) {
    Optional<XSound> xSound = XSound.matchXSound(id);
    return xSound.map(XSound::parseSound).orElse(null);
  }
}
