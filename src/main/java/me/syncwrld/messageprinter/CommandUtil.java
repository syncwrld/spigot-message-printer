package me.syncwrld.messageprinter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtil {

  public static boolean hasPermission(CommandSender sender, String permission) {
    return sender.hasPermission(permission) || sender.isOp();
  }

  public static boolean isPlayer(CommandSender sender) {
    return sender instanceof Player;
  }
  
}
