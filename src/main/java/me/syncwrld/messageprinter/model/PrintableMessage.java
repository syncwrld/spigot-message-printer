package me.syncwrld.messageprinter.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter(AccessLevel.PUBLIC)
public class PrintableMessage {
  private final List<String> lines;
  private final String sound;
  private final boolean playSoundInEveryLine;
  private final double delayBetween;
  private final boolean sendToAllPlayers;

  private PrintableMessage(
      List<String> lines,
      String sound,
      boolean playSoundInEveryLine,
      double delayBetween,
      boolean sendToAllPlayers) {
    this.lines = lines;
    this.sound = sound;
    this.playSoundInEveryLine = playSoundInEveryLine;
    this.delayBetween = delayBetween;
    this.sendToAllPlayers = sendToAllPlayers;
  }

  public static PrintableMessage withConfig(ConfigurationSection configurationSection) {
    return new PrintableMessage(
        configurationSection.getStringList("lines"),
        configurationSection.getString("sound"),
        configurationSection.getBoolean("play-sound-in-every-line"),
        configurationSection.getDouble("delay-between"),
        configurationSection.getBoolean("send-to-all-players"));
  }

  public static PrintableMessage createTest(List<String> lines, double delayBetween) {
    return new PrintableMessage(lines, null, false, delayBetween, false);
  }
}
