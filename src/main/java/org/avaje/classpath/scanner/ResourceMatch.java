package org.avaje.classpath.scanner;

import java.io.File;

/**
 * Some common resource matching predicates.
 */
public class ResourceMatch {

  public static MatchResource byPrefixSuffix(String prefix, String suffix) {
    return new ByPrefixSuffix(prefix, suffix);
  }

  public static MatchResource bySuffix(String suffix) {
    return new BySuffix(suffix);
  }

  public static MatchResource byPrefix(String prefix) {
    return new ByPrefix(prefix);
  }

  private ResourceMatch() {
  }

  private static class ByPrefixSuffix implements MatchResource {

    final String prefix;
    final String suffix;

    ByPrefixSuffix(String prefix, String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
    }


    @Override
    public boolean isMatch(String resourceName) {

      String fileName = resourceName.substring(resourceName.lastIndexOf(File.separator) + 1);

      return fileName.startsWith(prefix) && fileName.endsWith(suffix);
    }
  }

  private static class BySuffix implements MatchResource {

    final String suffix;

    BySuffix(String suffix) {
      this.suffix = suffix;
    }

    @Override
    public boolean isMatch(String resourceName) {
      return resourceName.endsWith(suffix);
    }
  }

  private static class ByPrefix implements MatchResource {

    final String prefix;

    ByPrefix(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public boolean isMatch(String resourceName) {
      return resourceName.startsWith(prefix);
    }
  }
}
