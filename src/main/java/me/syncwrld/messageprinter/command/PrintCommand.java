package me.syncwrld.messageprinter.command;

import me.syncwrld.messageprinter.CommandUtil;
import me.syncwrld.messageprinter.api.PrinterApiHolder;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrintCommand implements CommandExecutor {
  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String s, String[] strings) {
    if (!CommandUtil.isPlayer(commandSender)) {
      commandSender.sendMessage("You must be a player to use this command!");
      return false;
    }

    if (!CommandUtil.hasPermission(commandSender, "messageprinter.print")) {
      commandSender.sendMessage("You don't have permission to use this command!");
      return false;
    }

    if (strings.length == 0) {
      commandSender.sendMessage("Usage: /print <message-id>");
      return false;
    }

    SpigotPrinterAPI api = PrinterApiHolder.API;

    if (api == null) {
      commandSender.sendMessage("MessagePrinter is not enabled!");
      return false;
    }

    String id = strings[0];

    if (!(api.isRegistered(id))) {
      commandSender.sendMessage("Message with ID '" + id + "' does not exist!");
      return false;
    }

    api.getRegisteredById(id)
        .ifPresent(
            printableMessage -> {
              api.print(printableMessage, (Player) commandSender);
            });
    return false;
  }
}
