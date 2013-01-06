package event;

import java.util.List;

import model.Question;

import fr.swampwolf.events.Event;

public class FileLoadedEvent extends Event {
	private List<Question> questions;

	public FileLoadedEvent(List<Question> questions) {
		this.questions = questions;
	}

	public List<Question> getQuestions() {
		return questions;
	}
}
