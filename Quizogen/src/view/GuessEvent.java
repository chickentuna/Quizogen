package view;

import java.util.LinkedList;

import fr.swampwolf.events.Event;

public class GuessEvent extends Event {

	private LinkedList<String> guess;

	public GuessEvent(LinkedList<String> guess) {
		this.guess = guess;
	}

	public LinkedList<String> getGuess() {
		return guess;
	}

}
