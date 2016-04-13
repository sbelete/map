package edu.brown.cs.azhang6.maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.azhang6.autocorrect.Autocorrect;
import edu.brown.cs.azhang6.autocorrect.Led;
public class StreetComplete extends Autocorrect {
	Autocorrect street;
	Autocorrect words;
	public StreetComplete(Collection<String> words, Collection<String> streets) {
		super(words);
		setLed(3);
		useWhitespace = true;
		useAutocomplete = true;	
	}
	
	@Override
	public List<String> suggest(String word) {
		// Comparator for how to sort suggestions
		Comparator<? super String> comp = null;
		word = word.toLowerCase().trim();

		Set<String> suggestions = new HashSet<String>();
		if (contains(word)) {
			suggestions.add(word);
		}

		
		suggestions.addAll(autocomplete(word));
	
		suggestions.addAll(suggestLed(word));
	
		suggestions.addAll(whitespace(word));

		comp = new Led(word);

		List<String> retSuggestions = new ArrayList<String>(suggestions);

		if (comp != null) {
			Collections.sort(retSuggestions, comp);
		}

		return retSuggestions;
	}

}
