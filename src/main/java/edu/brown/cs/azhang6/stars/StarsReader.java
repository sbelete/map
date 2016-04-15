package edu.brown.cs.azhang6.stars;

import edu.brown.cs.azhang6.csv.CSVReader;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Reads a CSV-formatted stars file. A properly formatted stars file must have a
 * header on the first line and five fields on each line:
 * {@value StarsReader#ID}, {@value StarsReader#NAME}, {@value StarsReader#X},
 * {@value StarsReader#Y}, and {@value StarsReader#Z} (without the quotes).
 *
 * @author aaronzhang
 */
public class StarsReader {

  /**
   * Name of star ID field.
   */
  private static final String ID = "StarID";

  /**
   * Name of star name field.
   */
  private static final String NAME = "ProperName";

  /**
   * Name of star x-coordinate field.
   */
  private static final String X = "X";

  /**
   * Name of star y-coordinate field.
   */
  private static final String Y = "Y";

  /**
   * Name of star z-coordinate field.
   */
  private static final String Z = "Z";

  /**
   * {@code CSVReader} for stars file.
   */
  private final CSVReader reader;

  /**
   * Maps a nonempty star name to the star. Useful, for example, to look up a
   * star's position by name.
   */
  private final HashMap<String, Star> starsByName = new HashMap<>();

  /**
   * Whether the stars file has been read yet.
   */
  private boolean read = false;

  /**
   * Constructs a new {@code StarsReader} to read the specified file using the
   * specified delimiter. The file header is read in this constructor, but the
   * rest of the file isn't read until {@link #readToList()} is called.
   *
   * @param file name of file to read
   * @param delimiter delimiter between fields in the CSV file
   * @throws IOException if could not read file
   * @throws NullPointerException if either argument is null
   * @throws ParseException if file incorrectly formatted
   */
  public StarsReader(String file, String delimiter)
    throws IOException, ParseException {
    this.reader = new CSVReader(file, delimiter);
  }

  /**
   * Reads the file and returns a list of the stars in it.
   *
   * @return list of stars in file
   * @throws IOException if error reading file
   * @throws ParseException if file incorrectly formatted
   */
  public List<Star> readToList() throws IOException, ParseException {
    read = true;
    try {
      ArrayList<Star> stars = new ArrayList<>();
      while (reader.nextLine()) {
        Star star = new Star(
          Integer.parseInt(reader.get(ID)),
          reader.get(NAME),
          Double.parseDouble(reader.get(X)),
          Double.parseDouble(reader.get(Y)),
          Double.parseDouble(reader.get(Z)));
        // Add star to map of stars by name if name is not empty
        if (!star.getName().equals("")) {
          starsByName.put(star.getName(), star);
        }
        stars.add(star);
      }
      return stars;
    } catch (NumberFormatException e) {
      throw new ParseException(String.format(
        "Error reading file %s line %d: invalid number format",
        reader.getFile(), reader.getLineNum()),
        reader.getLineNum());
    } catch (IllegalArgumentException e) {
      throw new ParseException(String.format(
        "File %s does not contain correct tags", reader.getFile()),
        0);
    } finally {
      reader.close();
    }
  }

  /**
   * Gets the star with the specified name. Throws an exception if no star
   * matches the name or if the stars file has not been read yet. No star
   * matches a name that is an empty string.
   *
   * @param name name to search for
   * @return star with the given name
   * @throws IllegalArgumentException if no star matches the given name
   * @throws IllegalStateException if stars file has not yet been read
   * @throws NullPointerException if name is null
   */
  public Star starWithName(String name) {
    // Check validity of call and arguments
    if (!read) {
      throw new IllegalStateException(String.format(
        "Stars file %s has not been read yet", reader.getFile()));
    }
    if (name == null) {
      throw new NullPointerException("Calling starWithName() on null");
    }
    Star star = starsByName.get(name);
    if (star == null) {
      throw new IllegalArgumentException(String.format(
        "Cannot find star with name %s in file %s",
        name, reader.getFile()));
    }
    return star;
  }

  /**
   * @return string representation of this {@code StarsReader}
   */
  @Override
  public String toString() {
    return String.format("StarsReader reading file %s using delimiter %s",
      reader.getFile(), reader.getDelimiter());
  }
}
