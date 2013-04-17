package event;

import fr.swampwolf.events.Event;

public class EndOfQuizzEvent extends Event {

	private int points;
	
	public EndOfQuizzEvent(int points) {
		this.points = points;
	}
	
	public int getPoints() {
		return points;
	}

}
