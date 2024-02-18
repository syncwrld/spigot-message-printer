package me.syncwrld.messageprinter.registry;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import me.syncwrld.messageprinter.MessagePrinterEngine;
import me.syncwrld.messageprinter.model.PrintableMessage;

public class PrintableMessageLoader implements Loader<PrintableMessage> {
  private final List<PrintableMessage> messages;
  private final Map<String, PrintableMessage> messagesById;
  private final MessagePrinterEngine printer;

  public PrintableMessageLoader(MessagePrinterEngine printer) {
    this.printer = printer;
    this.messages = new ArrayList<>();
    this.messagesById = Maps.newConcurrentMap();
  }

  @Override
  public void reset() {
    this.messages.clear();
    this.messagesById.clear();
  }

  @Override
  public void load() {
    this.reset();

    val config = this.printer.getConfig();
    val section = config.getConfigurationSection("messages");

    if (section == null) {
      this.printer.log("&cConfiguration error! Could not find 'messages' section in config.yml");
      return;
    }

    for (val entry : section.getValues(false).entrySet()) {
      val messageSection = section.getConfigurationSection(entry.getKey());

      PrintableMessage printableMessage = PrintableMessage.withConfig(messageSection);
      this.messages.add(printableMessage);
      this.messagesById.put(entry.getKey(), printableMessage);
    }
  }

  @Override
  public List<PrintableMessage> getRegistered() {
    return this.messages;
  }

  @Override
  public Optional<PrintableMessage> getRegisteredById(String id) {
    return this.messagesById.entrySet().stream()
        .filter(e -> e.getKey().equals(id))
        .findFirst()
        .map(Map.Entry::getValue);
  }

  public void unregister(PrintableMessage message) {
    this.messages.remove(message);
  }

  public void unregisterById(String id) {
    this.messagesById.remove(id);
  }

  public void register(String id, PrintableMessage message) {
    this.messages.add(message);
    this.messagesById.put(id, message);
  }

  public String getIdOf(PrintableMessage message) {
    for (val entry : this.messagesById.entrySet()) {
      if (entry.getValue().equals(message)) {
        return entry.getKey();
      }
    }
    return null;
  }

}
