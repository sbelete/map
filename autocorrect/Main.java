package autocorrect;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		BufferedReader buf = null;
		
		ArrayList<String> good_words = new ArrayList<String>();
		ArrayList<String> bad_words = new ArrayList<String>();
		
		boolean useAutocomplete = false;
		boolean useWhitespace = false;
		int led = 0;
		
		int idx = 0;
		for (String a : args) {
			if (a.equals("--led"))
				try{
					  led = Integer.parseInt(args[idx + 1]);
					} catch (NumberFormatException e) {
					  // Not an int
					}
			if (a.equals("--prefix"))
				useAutocomplete = true;
			if (a.equals("--whitespace"))
				useWhitespace = true;
			if (a.equals("--smart"))
				System.out.println("Not smart");
			if (a.equals("--gui"))
				System.out.println("No gui");
			
			

			try {
				buf = new BufferedReader(new FileReader(a));
			} catch (FileNotFoundException e) {
				// Keep checking args
			}


			String word;
			boolean keep = true;

			while (buf.ready()) {
				word = buf.readLine();
				for (char l : word.toCharArray()) {
					Character.toLowerCase(l);
					if (!Character.isLowerCase(l))
						keep = false;
				}

				if (keep)
					good_words.add(word);
				else
					bad_words.add(word);
				
				keep = true;
			}

			buf.close();
			
			idx++;
		}

		Autocorrect prog = new Autocorrect(good_words);
		
		prog.useAutocomplete = useAutocomplete;
		prog.useWhitespace = useWhitespace;
		prog.setLed(led);
		
		System.out.println(good_words.size());
		
		System.out.println("Ready");
		String fix;
		
		Scanner sc = new Scanner(System.in);
		fix = sc.nextLine();
		
		List<String> suggestions;
		
		while (fix != null) {
			suggestions = prog.suggest(fix);
			
			if(suggestions != null && !suggestions.isEmpty())
				for (int i = 0; i < suggestions.size() && i < 5; i++)
					System.out.println(suggestions.get(i));

			suggestions.clear();
			fix = sc.nextLine();
		}

		sc.close();
	}
}
