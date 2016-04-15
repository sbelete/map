package edu.brown.cs.azhang6.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;

/**
 * Reads a nonempty CSV file.
 *
 * @author aaronzhang
 */
public class CSVReader {

  /**
   * File to read.
   */
  private final String file;

  /**
   * CSV file delimiter.
   */
  private final String delimiter;

  /**
   * Current line number.
   */
  private int lineNum = 0;

  /**
   * Reads file.
   */
  private final BufferedReader reader;

  /**
   * Maps each tag in the file header to its numerical position. The first tag
   * is mapped to 0, the next tag is mapped to 1, etc.
   */
  private final HashMap<String, Integer> tags;

  /**
   * Contents of current line, split by the delimiter.
   */
  private String[] line;

  /**
   * Creates a new {@code CSVReader} that reads the file using the given
   * delimiter. The file should be nonempty, and the first line should be a
   * header containing a list of unique tags. This constructor opens the file
   * and reads the header, but no more lines are read until {@link #nextLine()}
   * is called.
   *
   * @param file name of file to read
   * @param delimiter delimiter between fields in the CSV file
   * @throws IOException if could not read file
   * @throws NullPointerException if either argument is null
   * @throws ParseException if file incorrectly formatted
   */
  public CSVReader(String file, String delimiter)
    throws IOException, ParseException {
    // Check validity of arguments
    if (file == null) {
      throw new NullPointerException("Creating CSVReader with null file");
    }
    this.file = file;
    if (delimiter == null) {
      throw new NullPointerException(
        "Creating CSVReader with null delimiter");
    }
    this.delimiter = delimiter;

    // Initialize the BufferedReader
    try {
      reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (IOException e) {
      throw new IOException(
        String.format("Could not read file %s", file));
    }

    // Read the header, throwing an exception if file is empty
    String header = reader.readLine();
    lineNum++;
    if (header == null) {
      throw new ParseException(String.format(
        "Error parsing file %s line %d: empty file", file, lineNum),
        lineNum);
    }

    // Split the header into pieces and initialize the tags map
    tags = new HashMap<>();
    String[] headerPieces = header.split(delimiter);
    for (int i = 0; i < headerPieces.length; i++) {
      String tag = headerPieces[i];
      if (tags.keySet().contains(tag)) {
        throw new ParseException(String.format(
          "Error parsing file %s line %d: duplicate tag %s",
          file, lineNum, tag), lineNum);
      }
      tags.put(tag, i);
    }
  }

  /**
   * Reads next line of file, if any. Returns {@code true} if a new line was
   * read, otherwise {@code false}. Empty lines are ignored. Closes file if end
   * of file reached.
   *
   * @return whether a new line was read
   * @throws IOException if an error occurred reading the next line
   */
  public boolean nextLine() throws IOException {
    try {
      String nextLine = reader.readLine();
      lineNum++;
      // If end of file reached
      if (nextLine == null) {
        reader.close();
        return false;
      }
      // Ignore empty lines
      if (nextLine.equals("")) {
        return nextLine();
      }
      // Split the line using the delimiter
      line = nextLine.split(delimiter);
      return true;
    } catch (IOException e) {
      throw new IOException(String.format(
        "Could not read next line from file %s", file));
    }
  }

  /**
   * Gets the value associated with the given tag on the current line.
   *
   * @param tag tag to get the value of
   * @return value of the tag on the current line
   * @throws IllegalArgumentException if invalid tag
   * @throws NullPointerException if tag is null
   * @throws ParseException if file incorrectly formatted
   */
  public String get(String tag) throws ParseException {
    // Check validity of arguments
    if (tag == null) {
      throw new NullPointerException("Calling get() with null tag");
    }

    // Get value of tag
    try {
      return line[tags.get(tag)];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new ParseException(String.format(
        "Error parsing file %s line %d: incorrectly formatted",
        file, lineNum), lineNum);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(
        String.format("Invalid tag: %s", tag));
    }
  }

  /**
   * Closes file.
   *
   * @throws IOException if could not close file
   */
  public void close() throws IOException {
    try {
      reader.close();
    } catch (IOException e) {
      throw new IOException(
        String.format("Could not close file %s", file));
    }
  }

  /**
   * @return string representation of this {@code CSVReader}
   */
  @Override
  public String toString() {
    return String.format("CSVReader reading file %s using delimiter %s",
      file, delimiter);
  }

  /**
   * @return file to read
   */
  public String getFile() {
    return file;
  }

  /**
   * @return delimiter used to read file
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * @return current line number of file
   */
  public int getLineNum() {
    return lineNum;
  }
}
