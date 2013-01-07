package control;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import model.Question;
import event.CorrectionEvent;
import event.EndOfQuizzEvent;
import event.FileLoadRequest;
import event.FileLoadedEvent;
import event.NextQuestionEvent;
import event.NextQuestionRequest;
import fr.swampwolf.events.EventHandler;
import fr.swampwolf.events.EventManager;
import fr.swampwolf.events.interfaces.Observer;
import view.GuessEvent;
import view.Window;

public class Controller implements Observer {

	private EventManager ev_man;
	private List<Question> questions;
	private Iterator<Question> it;
	private Question current_question;
	private int points;

	public static void main(String[] args) {
		new Controller();
	}

	public Controller() {
		ev_man = new EventManager();
		ev_man.addObserver(this);
		new Window(ev_man, this);
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
		if (questions != null && questions.size() > 0) {
			points = 0;
			ev_man.fire(new FileLoadedEvent(questions));
			it = questions.iterator();
			current_question = it.next();
			ev_man.fire(new NextQuestionEvent(current_question));
		}
	}

	@EventHandler
	public void on(GuessEvent event) {
		boolean atField = false;
		Iterator<String> itq = current_question.getText().iterator();
		Iterator<String> ita = event.getGuess().iterator();
		LinkedList<Boolean> correction = new LinkedList<>();
		boolean point = true;
		while (itq.hasNext()) {
			String truth = itq.next();
			if (atField) {
				boolean result = truth.equals(ita.next());
				correction.add(result);
				point &= result;
			}
			atField = !atField;
		}
		if (point)
			points++;
		ev_man.fire(new CorrectionEvent(correction));
	}

	@EventHandler
	public void on(NextQuestionRequest event) {
		if (it.hasNext()) {
			current_question = it.next();
			ev_man.fire(new NextQuestionEvent(current_question));
		} else {
			ev_man.fire(new EndOfQuizzEvent(points));
		}
	}
}
