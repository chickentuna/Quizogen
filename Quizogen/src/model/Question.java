package model;

import java.util.LinkedList;
import java.util.List;

public class Question {
	private LinkedList<String> text;

	public Question() {
		text = new LinkedList<>();
	}
	
	public void addText(String string) {
		text.add(string);		
	}
	
	public String toString() {
		return text.toString();
	}

	public List<String> getText() {
		return text;
	}
	
}
