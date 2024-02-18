package me.syncwrld.messageprinter.registry;

import java.util.List;
import java.util.Optional;

public interface Loader<T> {
  void reset();

  void load();

  List<T> getRegistered();

  Optional<T> getRegisteredById(String id);
}
