package models;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import siena.embed.EmbeddedMap;

@EmbeddedMap
public class Phrase {
	public String phrase;
	public String meaning;
	public Set<String> otherMeanings;
	
	public Phrase(String phrase, String meaning) {
		this.phrase = phrase;
		this.meaning = meaning;
		this.otherMeanings = new HashSet<String>(); 
	}
	
	public Phrase mean(String meaning) {
		this.otherMeanings.add(meaning);
		return this;
	}
	
}
