package event;

import model.Question;
import fr.swampwolf.events.Event;

public class NextQuestionEvent extends Event {

	private Question q;
	
	public NextQuestionEvent(Question q) {
		this.q = q;
	}

	public Question getQuestion() {
		return q;
	}
	
}
