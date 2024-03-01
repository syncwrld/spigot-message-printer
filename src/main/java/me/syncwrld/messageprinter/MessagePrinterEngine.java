package me.syncwrld.messageprinter;

import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Stopwatch;
import lombok.AccessLevel;
import lombok.Getter;
import me.syncwrld.messageprinter.api.PrinterApiHolder;
import me.syncwrld.messageprinter.api.impl.SpigotPrinterApiImpl;
import me.syncwrld.messageprinter.command.PrintCommand;
import me.syncwrld.messageprinter.command.tab.PrintCommandTab;
import me.syncwrld.messageprinter.listeners.PredefinedActionListener;
import me.syncwrld.messageprinter.registry.PrintableMessageLoader;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@Getter(AccessLevel.PUBLIC)
public final class MessagePrinterEngine extends JavaPlugin {

  private PrintableMessageLoader messageLoader;

  @Override
  public void onEnable() {
    Stopwatch enableWatcher = Stopwatch.createStarted();
    this.saveDefaultConfig();

    this.messageLoader = new PrintableMessageLoader(this);
    this.messageLoader.load();

    PrinterApiHolder.API = new SpigotPrinterApiImpl(this, this.messageLoader);

    PredefinedActionListener.register(this);
    PaperCommandManager paperCommandManager = new PaperCommandManager(this);
    paperCommandManager.registerCommand(new PrintCommand(this));

    this.log("&aTotally started, enable phase took " + enableWatcher.stop() + " to complete.");
  }

  @Override
  public void onDisable() {}

  public void log(String message) {
    this.getServer()
        .getConsoleSender()
        .sendMessage("§e[SpigotMessagePrinter] " + message.replace("&", "§"));
  }
}
