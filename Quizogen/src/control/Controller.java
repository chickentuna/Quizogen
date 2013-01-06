package control;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import model.Question;
import event.FileLoadRequest;
import event.FileLoadedEvent;
import event.NextQuestionEvent;
import fr.swampwolf.events.EventHandler;
import fr.swampwolf.events.EventManager;
import fr.swampwolf.events.interfaces.Observer;
import view.OKButtonEvent;
import view.Window;

public class Controller implements Observer {

	private EventManager ev_man;
	private Window window;
	private List<Question> questions;
	private Iterator<Question> it;
	private boolean correction; 

	public static void main(String[] args) {
		new Controller();
	}

	public Controller() {
		ev_man = new EventManager();
		ev_man.addObserver(this);
		window = new Window(ev_man, this);
		questions = null;
		correction = false;
	}

	@EventHandler
	public void on(OKButtonEvent event) {
		if (!correction) {
			
		}
	}
	
	@EventHandler
	public void on(FileLoadRequest event) {
		QuizoParser qp = new QuizoParser();

		try {
			questions = qp.parse(event.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (questions != null && questions.size()>0) {
			ev_man.fire(new FileLoadedEvent(questions));
			it = questions.iterator();
			ev_man.fire(new NextQuestionEvent(it.next()));
		}
	}
}
