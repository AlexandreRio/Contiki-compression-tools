import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

/**
 * Compute the keyword frequency of JSON models.
 */
public class Main {

  private static final Comparator<Integer> BY_ASCENDING_ORDER = Integer::compare;
  private static final Comparator<Integer> BY_DESCENDING_ORDER = BY_ASCENDING_ORDER.reversed();

  /** Folder containing JSON files. */
  private static final String PATH = "models/";
  /** Map a keyword to its number of occurrences. */
  private static Map<String, Integer> occurrences = new LinkedHashMap<String, Integer>();
  private static final int STAT_DIGIT = 5;

  private static final float THRESHOLD_FREQUENCY = 0.00f;
  private static final float THRESHOLD_LENGTH    = 1;

  public static void main (String[] args) {
    File models = new File(PATH);
    for (File f : models.listFiles())
      parseFile(f);


    Map<String, Integer> sorted = new LinkedHashMap<String, Integer>();
    occurrences.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(BY_DESCENDING_ORDER))
      .forEachOrdered(e -> sorted.put(e.getKey(), e.getValue()));
    occurrences = sorted;

    if (args.length > 0) {
      if (args[0].equals("--stat"))
        displayResults();
      else if (args[0].equals("--table"))
        produceCTableAssociation();
      else
        System.out.println("Options are --stat and --table");
    } else
      System.out.println("Options are --stat and --table");
  }

  /**
   * Count the keyword of the given file and add the stats to the global
   * {@link occurrences}.
   *
   * @param name file model in JSON format
   */
  private static void parseFile(File name) {
    try {
      Scanner s = new Scanner(name);
      String pattern = "\"([a-zA-Z.\\/\\[\\]_\\-0-9]+)\"";
      Pattern p = Pattern.compile(pattern);

      while (s.hasNextLine()) {
        Matcher m = p.matcher(s.nextLine());
        while (m.find()) {
          String key = m.group(1);
          if (occurrences.containsKey(key)) {
            occurrences.put(key, occurrences.get(key) + 1);
          } else {
            occurrences.put(key, 1);
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("No file found " + e.getMessage());
    }
  }

  /**
   * Compute the frequencies of the occurrences and display it
   *
   * @see occurrences
   */
  private static void displayResults() {
    int totalOccurences = 0;
    for (String o : occurrences.keySet())
      totalOccurences += occurrences.get(o);

    double freq;
    String freqPrint;
    int attrLength;
    for (String o : occurrences.keySet()) {
      freq =(double)occurrences.get(o)/totalOccurences; 
      freqPrint = ("" + freq*100).substring(0, STAT_DIGIT);
      attrLength = o.length();

      if (freq >= THRESHOLD_FREQUENCY && attrLength >= THRESHOLD_LENGTH )
        System.err.println(occurrences.get(o) + "\t" + freqPrint + "%  " + o);
    }
  }

  //TODO merge conditions to print
  private static void produceCTableAssociation() {
    String alpha = "[]()abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    int totalOccurences = 0;
    for (String o : occurrences.keySet())
      totalOccurences += occurrences.get(o);

    List<String> set = new ArrayList<String>();
    GenerateUniquesSmaller(set, "", alpha, 2);

    Comparator<String> lengthComparator = new Comparator<String>(){
      public int compare(String s1, String s2) {
        return s1.length() - s2.length();
      }
    };

    Collections.sort(set, lengthComparator);

    int i=0;

    double freq;
    String freqPrint;
    int attrLength;
    for (String o : occurrences.keySet()) {
      freq =(double)occurrences.get(o)/totalOccurences; 
      freqPrint = ("" + freq*100).substring(0, STAT_DIGIT);
      attrLength = o.length();

      //print on err output to avoid buffer errors, order is important
      if (freq >= THRESHOLD_FREQUENCY && attrLength >= THRESHOLD_LENGTH )
        System.err.println("{\"" + o + "\",\"" + set.get(i++) + "\"},");
    }
  }

  private static void GenerateUniquesSmaller(List<String> coll, String prefix, String chars, int depth) {
    if (depth-- == 0)
      return;
    for (int j=0;j<chars.length(); j++) {
      coll.add(prefix + chars.charAt(j));
      GenerateUniquesSmaller(coll, prefix + chars.charAt(j), chars, depth);
    }
  }


}
