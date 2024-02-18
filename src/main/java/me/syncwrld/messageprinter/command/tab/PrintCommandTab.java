package me.syncwrld.messageprinter.command.tab;

import java.util.ArrayList;
import java.util.List;
import me.syncwrld.messageprinter.api.PrinterApiHolder;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;
import me.syncwrld.messageprinter.model.PrintableMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PrintCommandTab implements TabCompleter {
  @Override
  public List<String> onTabComplete(
      CommandSender commandSender, Command command, String s, String[] args) {
    List<String> strings = new ArrayList<>();

    if (args.length == 1) {
      SpigotPrinterAPI api = PrinterApiHolder.API;
      if (api != null) {
        for (PrintableMessage printableMessage : api.getRegistered()) {
          strings.add(api.getIdOf(printableMessage));
        }
      }
    }

    return strings;
  }
}
