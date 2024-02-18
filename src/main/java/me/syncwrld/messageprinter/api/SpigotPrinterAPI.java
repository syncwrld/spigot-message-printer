package me.syncwrld.messageprinter.api;

import com.google.common.annotations.Beta;
import java.util.List;
import java.util.Optional;
import me.syncwrld.messageprinter.model.PrintableMessage;
import org.bukkit.entity.Player;

public interface SpigotPrinterAPI {
  /**
   * A method to print a list of strings with a delay.
   *
   * @param lines the list of strings to be printed
   * @param delayTicks the delay in ticks before printing each string
   * @param players the players to print the message to
   */
  @Beta
  void print(List<String> lines, int delayTicks, Player... players);

  /**
   * A method to print a printable message to a player.
   *
   * @param printableMessage the message to be printed
   * @param player the player to whom the message will be printed
   */
  void print(PrintableMessage printableMessage, Player player);

  /**
   * Print a printable message to the specified players.
   *
   * @param printableMessage the message to be printed
   * @param players the players to print the message to
   */
  void print(PrintableMessage printableMessage, Player... players);

  /**
   * Registers a printable message.
   *
   * @param printableMessage the message to be registered
   */
  void registerMessage(String id, PrintableMessage printableMessage);

  /**
   * Unregisters a message with the given id from Loader
   *
   * @param id the id of the message to unregister
   */
  void unregisterMessage(String id);

  /**
   * Checks if a message with the given id is registered
   *
   * @param id the id of the message
   * @return true if the message is registered
   */
  boolean isRegistered(String id);

  /**
   * Returns a message with the given id if present
   *
   * @param id the id of the message
   * @return the message
   */
  Optional<PrintableMessage> getRegisteredById(String id);

  /**
   * Returns the id of the given message
   *
   * @param message the message that the id should be retrieved from
   * @return the id of the message
   */
  String getIdOf(PrintableMessage message);

  /**
   * Gets all registered messages
   *
   * @return the list of messages
   */
  List<PrintableMessage> getRegistered();
}
