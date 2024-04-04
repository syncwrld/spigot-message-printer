package me.syncwrld.messageprinter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Stopwatch;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.val;
import me.syncwrld.messageprinter.MessagePrinter;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;
import me.syncwrld.messageprinter.model.PrintableMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("printer|spigotprinter|syncprinter|reader|spigotreader")
public class PrintCommand extends BaseCommand {

  private final MessagePrinterEngine printerEngine;

  public PrintCommand(MessagePrinterEngine printerEngine) {
    this.printerEngine = printerEngine;
  }

  @Default
  private void handleDefault(CommandSender commandSender) {
    val description = this.printerEngine.getDescription();
    commandSender.sendMessage(
        "§eSpigotMessagePrinter v" + description.getVersion() + " by " + description.getAuthors());

    if (commandSender.hasPermission("messageprinter.admin")) {
      commandSender.sendMessage(
          "§7Loaded " + this.printerEngine.getMessageLoader().getRegistered().size() + " messages");
      commandSender.sendMessage("§7(?) Type /printer help for more info!");
    }
  }

  @HelpCommand
  @Subcommand("help")
  @CommandPermission("messageprinter.help")
  private void handleHelp(CommandSender commandSender) {
    commandSender.sendMessage(
        new String[] {
          "",
          "§7Printer Help",
          "§7 ▸ /printer print <message-id> (prints message to all online players)",
          "§7 ▸ /printer print <message-id> <player> (prints message to the specified player)",
          "§7 ▸ /printer reload (reload messages)",
          "§7 ▸ /printer info (shows printer plugin info)",
          ""
        });
  }

  @Subcommand("print")
  @CommandPermission("messageprinter.print")
  @Syntax("/spigotprinter print <message-id> [player (optional)]")
  private void handlePrint(CommandSender commandSender, String[] args) {
    if (args.length < 1) {
      commandSender.sendMessage("§cUsage: /printer print <message-id> [player (optional)]");
      return;
    }

    String messageId = args[0];
    final SpigotPrinterAPI api = MessagePrinter.getAPI();

    Optional<PrintableMessage> registeredById = api.getRegisteredById(messageId);

    if (!registeredById.isPresent()) {
      commandSender.sendMessage("§cMessage '" + messageId + "' not found");
      return;
    }

    PrintableMessage printableMessage = registeredById.get();
    if (args.length == 1) {
      final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
      api.print(printableMessage, players.toArray(new Player[0]));
      commandSender.sendMessage("§7Printed '" + messageId + "' to " + players.size() + " players");
    } else {
      String playerName = args[1];
      Player player = Bukkit.getPlayer(playerName);

      if (player != null) {
        api.print(printableMessage, player);
        commandSender.sendMessage("§7Printed " + messageId + " to " + playerName);
      } else {
        commandSender.sendMessage("§cPlayer '" + playerName + "' not found");
      }
    }
  }

  @Subcommand("reload")
  @CommandPermission("messageprinter.reload")
  private void handleReload(CommandSender commandSender) {
    Stopwatch reloadWatcher = Stopwatch.createStarted();
    this.printerEngine.getMessageLoader().load();

    commandSender.sendMessage(
        "§7Loaded "
            + this.printerEngine.getMessageLoader().getRegistered().size()
            + " messages, took "
            + reloadWatcher.stop()
            + " to complete");
  }

  @Subcommand("info")
  private void handleInfo(CommandSender commandSender) {
    val description = this.printerEngine.getDescription();
    List<String> authors = description.getAuthors();
    authors.replaceAll(text -> text.replace("[", "").replace("]", ""));
    commandSender.sendMessage(
        "§eSpigotMessagePrinter v" + description.getVersion() + " by " + authors);
  }
}
