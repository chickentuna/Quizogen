package event;

import java.util.LinkedList;

import fr.swampwolf.events.Event;

public class CorrectionEvent extends Event {

	private LinkedList<Boolean> correction;

	public CorrectionEvent(LinkedList<Boolean> correction) {
		this.correction = correction;
	}

	public LinkedList<Boolean> getCorrection() {
		return correction;
	}
	
}
