package control;

import java.io.IOException;
import java.util.List;

import model.Question;
import event.FileLoadRequest;
import event.FileLoadedEvent;
import event.NextQuestionEvent;
import fr.swampwolf.events.EventHandler;
import fr.swampwolf.events.EventManager;
import fr.swampwolf.events.interfaces.Observer;
import view.Window;

public class Controller implements Observer {

	EventManager ev_man;
	Window window;
	private List<Question> questions;

	public static void main(String[] args) {
		new Controller();
	}

	public Controller() {
		ev_man = new EventManager();
		ev_man.addObserver(this);
		window = new Window(ev_man, this);
		questions = null;
	}

	@EventHandler
	public void on(FileLoadRequest event) {
		QuizoParser qp = new QuizoParser();

		try {
			questions = qp.parse(event.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (questions != null) {
			ev_man.fire(new FileLoadedEvent(questions));
			ev_man.fire(new NextQuestionEvent(questions.get(0)));
		}
	}
}
// TODO: Put ok button under question by placing the Q pane in a vertical box,
// in the center, with the lower column containing the OK