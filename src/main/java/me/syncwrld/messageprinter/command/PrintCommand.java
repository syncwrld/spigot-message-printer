package me.syncwrld.messageprinter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Stopwatch;
import lombok.val;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import org.bukkit.command.CommandSender;

@CommandAlias("printer|spigotprinter")
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
          "§7 ▸ /printer print <message-id>",
          "§7 ▸ /printer print <message-id> <player>",
          "§7 ▸ /printer reload",
          "§7 ▸ /printer info",
          ""
        });
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
}
