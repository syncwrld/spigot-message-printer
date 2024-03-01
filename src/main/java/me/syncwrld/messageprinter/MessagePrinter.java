package me.syncwrld.messageprinter;

import me.syncwrld.messageprinter.api.PrinterApiHolder;
import me.syncwrld.messageprinter.api.SpigotPrinterAPI;

public class MessagePrinter {

  public static SpigotPrinterAPI getAPI() {
    SpigotPrinterAPI api = PrinterApiHolder.API;
    if (api == null) {
      throw new IllegalStateException(
          "SpigotPrinterAPI is not loaded! - wait initialization to get api instance");
    }
    return api;
  }

  public MessagePrinterEngine getEngine() {
    return (MessagePrinterEngine) getAPI().getRegistor();
  }

}
