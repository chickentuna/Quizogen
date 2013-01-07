package event;

import fr.swampwolf.events.Event;

public class EndOfQuizzEvent extends Event {

	private int points;
	
	public EndOfQuizzEvent(int points) {
		this.points = points;
	}
	
	public String getPoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
