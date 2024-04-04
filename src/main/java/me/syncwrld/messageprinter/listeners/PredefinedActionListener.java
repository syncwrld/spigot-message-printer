package me.syncwrld.messageprinter.listeners;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import me.syncwrld.messageprinter.api.PrinterApiHolder;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PredefinedActionListener implements Listener {

  @Getter(AccessLevel.PRIVATE)
  private final MessagePrinterEngine printerEngine;

  private final SpigotPrinterAPI api;

  @Setter(AccessLevel.MODULE)
  private String connectMessage, disconnectMessage;

  public PredefinedActionListener(MessagePrinterEngine printerEngine) {
    this.printerEngine = printerEngine;
    this.api = PrinterApiHolder.API;

    this.setConnectMessage(printerEngine.getConfig().getString("default-actions.on-connect"));
    this.setDisconnectMessage(printerEngine.getConfig().getString("default-actions.on-disconnect"));
  }

    public static void register(MessagePrinterEngine printerEngine) {
        PredefinedActionListener predefinedActionListener = new PredefinedActionListener(printerEngine);
        printerEngine.getServer().getPluginManager().registerEvents(predefinedActionListener, printerEngine);
    }

  @EventHandler
  public void whenPlayerConnect(PlayerJoinEvent event) {
    if (this.connectMessage != null) {
      api.getRegisteredById(connectMessage)
          .ifPresent(
              message -> {
                api.print(message, event.getPlayer());
              });
    }
  }

    @EventHandler
    public void whenPlayerDisconnect(PlayerQuitEvent event) {
        if (this.disconnectMessage != null) {
            api.getRegisteredById(disconnectMessage)
                    .ifPresent(
                            message -> {
                                api.print(message, event.getPlayer());
                            });
        }
    }
}
