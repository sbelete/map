package edu.brown.cs.azhang6.csv;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link CSVReader}.
 *
 * @author aaronzhang
 */
public class CSVReaderTest {

  /**
   * Unit tests for constructor {@link CSVReader#CSVReader(String, String)}.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void unitConstructor() throws Exception {
    // Valid CSVReader
    CSVReader instance1 = new CSVReader("files/stardata.csv", ",");
    assertTrue(instance1.toString().equals(
        "CSVReader reading file files/stardata.csv using delimiter ,"));
    instance1.close();

    // Valid CSVReader with a different delimiter
    CSVReader instance2 = new CSVReader("files/space-delimited.csv", " ");
    assertTrue(instance2.toString().equals(
        "CSVReader reading file files/space-delimited.csv using "
        + "delimiter  "));
    instance2.close();

    // Nonexistent file
    boolean caught3 = false;
    try {
      CSVReader instance3 = new CSVReader("files/stardata", ",");
    } catch (IOException e) {
      caught3 = true;
    }
    assertTrue(caught3);

    // Empty file
    boolean caught4 = false;
    try {
      CSVReader instance4 = new CSVReader("files/empty.csv", ",");
    } catch (ParseException e) {
      caught4 = true;
    }
    assertTrue(caught4);

    // Header with duplicate tags
    boolean caught5 = false;
    try {
      CSVReader instance5
          = new CSVReader("files/duplicate-tags.csv", ",");
    } catch (ParseException e) {
      caught5 = true;
    }
    assertTrue(caught5);

    // Null arguments
    boolean caught6 = false;
    try {
      CSVReader instance6 = new CSVReader(null, ",");
    } catch (NullPointerException e) {
      caught6 = true;
    }
    assertTrue(caught6);
    boolean caught7 = false;
    try {
      CSVReader instance7 = new CSVReader("files/stardata.csv", null);
    } catch (NullPointerException e) {
      caught7 = true;
    }
    assertTrue(caught7);
  }

  /**
   * Unit tests for {@link CSVReader#nextLine()}.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void unitNextLine() throws Exception {
    CSVReader instance1 = new CSVReader("files/stardata-small.csv", ",");
    // File has 9 lines besides header
    for (int i = 0; i < 9; i++) {
      assertTrue(instance1.nextLine());
    }
    assertFalse(instance1.nextLine());

    // Reading past the end of file
    boolean caught1 = false;
    try {
      instance1.nextLine();
    } catch (IOException e) {
      caught1 = true;
    }
    assertTrue(caught1);
  }

  /**
   * Unit tests for {@link CSVReader#get(String)}.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void unitGet() throws Exception {
    // Properly formatted file, valid tag
    CSVReader instance1 = new CSVReader("files/stardata-small.csv", ",");
    instance1.nextLine();
    assertTrue(instance1.get("ProperName").equals("Sol"));
    instance1.close();

    // Incorrectly formatted file
    CSVReader instance2 = new CSVReader("files/wrong-format.csv", ",");
    for (int i = 0; i < 4; i++) {
      instance2.nextLine();
    }
    boolean caught2 = false;
    try {
      instance2.get("Z");
    } catch (ParseException e) {
      caught2 = true;
    }
    assertTrue(caught2);
    instance2.close();

    // Invalid tag
    CSVReader instance3 = new CSVReader("files/stardata-small.csv", ",");
    instance3.nextLine();
    boolean caught3 = false;
    try {
      instance3.get("z");
    } catch (IllegalArgumentException e) {
      caught3 = true;
    }
    assertTrue(caught3);
    instance3.close();

    // Null tag
    CSVReader instance4 = new CSVReader("files/stardata-small.csv", ",");
    instance4.nextLine();
    boolean caught4 = false;
    try {
      instance4.get(null);
    } catch (NullPointerException e) {
      caught4 = true;
    }
    assertTrue(caught4);
    instance4.close();
  }

  /**
   * Unit tests for {@link CSVReader#close()}.
   *
   * @throws Exception should not be thrown
   */
  @Test
  public void unitClose() throws Exception {
    CSVReader instance1 = new CSVReader("files/stardata-small.csv", ",");
    instance1.close();
  }
}
