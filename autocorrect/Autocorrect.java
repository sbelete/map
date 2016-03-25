package autocorrect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Autocorrect extends Trie {
	private static int led = 0; // Default LED

	/**
	 * Constructor for Autocorrect uses Trie constructor
	 * 
	 * @param words
	 *            - a collection of words to be saved
	 */
	public Autocorrect(Collection<String> words) {
		super(words);
	}

	/**
	 * @exception IllegalArgumentException
	 *                - led > 0
	 * @param led
	 *            - is the distance we are willing to search
	 */
	public void setLed(int led) {
		if (led < 0) {
			throw new IllegalArgumentException("Can't set LED to < 0");
		}

		Autocorrect.led = led;
	}

	/**
	 * Makes led accessible
	 * 
	 * @return - returns the led
	 */
	public int getLed() {
		return led;
	}

	/**
	 * Creates a list of words in within set led
	 * 
	 * @param word
	 *            - word to be compared to
	 * @return - a list of words within set led to word
	 */
	public List<String> suggestLed(String word) {
		List<String> suggestions = new ArrayList<>();

		// Returns only exact word if led is zero
		if (led == 0) {
			if (contains(word)) {
				suggestions.add(word);
			}

			return suggestions;
		}

		char[] letters = (" " + word).toCharArray();
		int[][] ledValue = new int[letters.length + led][letters.length];

		for (int y = 1; y < letters.length; y++)
			ledValue[0][y] = y;

		StringBuilder sb = new StringBuilder(" ");

		getBase().entrySet().forEach(i -> {
			sb.append(i.getKey());

			// calls a helper
			suggestLedHelper(i.getValue(), letters, ledValue, sb, suggestions);

			sb.deleteCharAt(sb.length() - 1); // removes the last character
		});

		return suggestions;
	}

	// Helper function for suggestLedHelper
	private static void suggestLedHelper(TrieNode node, char[] letters, int[][] ledValue, StringBuilder sb,
			List<String> suggestions) {
		int sL = sb.length() - 1;
		// base for the recursive loop
		if (sL + 1 > ledValue.length) {
			return;
		}

		int length = letters.length;

		ledValue[sL][0] = sL;
		int min = sL;

		for (int k = 1; k < length; k++) {
			ledValue[sL][k] = ledValue[sL - 1][k - 1];

			if (sb.charAt(sL) != letters[k]) {
				ledValue[sL][k] = Math.min(Math.min(
						ledValue[sL][k], ledValue[sL - 1][k]),
						ledValue[sL][k - 1]) + 1;
			}

			// setting the new min
			if (ledValue[sL][k] < min) {
				min = ledValue[sL][k];
			}
		}

		// If word is in the led then add it to suggestions
		if (ledValue[sL][length - 1] <= led && node.validWord()) {
			suggestions.add(sb.toString().substring(1));
		}

		// Check to see if we are at max distance allowed if not go further
		if (min <= led) {
			for (Entry<Character, TrieNode> e : node.entrySet()) {
				sb.append(e.getKey());

				// Recursive call
				suggestLedHelper(e.getValue(), letters, ledValue, sb, suggestions);

				sb.deleteCharAt(sb.length() - 1);
			}
		}
	}

	/**
	 * Finds if a word could use a space
	 * 
	 * @param s
	 *            - a word that could have a space
	 * @return - a list of two words
	 */
	public List<String> whitespace(String s) {
		List<String> retSug = new ArrayList<String>();

		// Checks all possible subsets of words
		for (int i = 0; i < s.length(); i++) {
			String w1 = s.substring(0, i);
			String w2 = s.substring(i);

			if (contains(w1) && contains(w2)) {
				retSug.add(w1 + " " + w2);
			}
		}

		return retSug;
	}

	/**
	 * Creates a list of suggested words through auto complete
	 * 
	 * @param s
	 *            - string that is prefix of the word
	 * @return - a list of strings that start with the string given
	 */
	public List<String> autocomplete(String s) {
		List<String> suggestions = new ArrayList<String>();
		TrieNode node = getNode(s); // finds string in node

		if (node == null) {
			return suggestions;
		}

		Iterator<String> iter = new TrieIterator(node, s);
		while (iter.hasNext()) {
			suggestions.add(iter.next()); // add following nodes from current
		}

		return suggestions;
	}

	public boolean useWhitespace = false;
	public boolean useAutocomplete = false;

	public List<String> suggest(String word) {
		// Comparator for how to sort suggestions
		Comparator<? super String> comp = null;
		word = word.toLowerCase().trim();

		Set<String> suggestions = new HashSet<String>();
		if (contains(word)) {
			suggestions.add(word);
		}

		if (useAutocomplete)
			suggestions.addAll(autocomplete(word));
		if (led > 0)
			suggestions.addAll(suggestLed(word));
		if (useWhitespace)
			suggestions.addAll(whitespace(word));

		comp = new Led(word);

		List<String> retSuggestions = new ArrayList<String>(suggestions);

		if (comp != null) {
			Collections.sort(retSuggestions, comp);
		}

		return retSuggestions;
	}
}
