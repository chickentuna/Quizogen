package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import model.Question;

import control.Controller;
import event.CorrectionEvent;
import event.EndOfQuizzEvent;
import event.FileLoadRequest;
import event.FileLoadedEvent;
import event.NextQuestionEvent;
import event.NextQuestionRequest;
import fr.swampwolf.events.EventHandler;
import fr.swampwolf.events.EventManager;
import fr.swampwolf.events.interfaces.Observer;

public class Window implements Observer {

	/** Constants **/
	private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(
			0, 10, 10, 10);
	private static final String L_LABEL_TITLE = "Quizogen";
	private static final String CORRECT_COLOUR = "0AFF0A";
	private static final String INCORRECT_COLOUR = "FF0000";

	/** Components **/
	private JFrame frame;
	private JPanel panel;
	private JFileChooser fc_chooser;
	private JButton button_open;
	private JPanel panel_question;
	private JButton button_ok;
	private JLabel label_count;
	

	/** Control **/
	private EventManager ev_man;
	private boolean correction = false;
	private Question current_question;
	private LinkedList<JTextField> answers;
	private int q_count = 0;
	private int q_max = 0;

	public Window(EventManager ev_man, Controller controller) {
		this.ev_man = ev_man;
		ev_man.addObserver(this);
		answers = new LinkedList<>();
		initComponents();
		initActions();
		buildFrame();

	}

	private void buildFrame() {

		panel.setPreferredSize(new Dimension(640, 480));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BorderLayout());
		{
			// SETUP cpane:top:center_panel:fc_panel
			JPanel north_panel = new JPanel();
			north_panel.setLayout(new BoxLayout(north_panel, BoxLayout.Y_AXIS));
			{
				// SETUP cpane:top:center_panel:fc_panel:row0
				JPanel row_title = new JPanel();
				row_title.setLayout(new BoxLayout(row_title, BoxLayout.X_AXIS));
				row_title.setBorder(EMPTY_BORDER);
				{
					JComponent row = row_title;
					JLabel lbl_title = new JLabel(L_LABEL_TITLE);
					label_count = new JLabel("(0/0)");
					lbl_title.setFont(new Font("Sans serif", Font.PLAIN, 30));
					row.add(lbl_title);
				}
				north_panel.add(row_title);

				// SETUP cpane:top:center_panel:fc_panel:row1
				JPanel row_filechooser = new JPanel();
				row_filechooser.setLayout(new BoxLayout(row_filechooser,
						BoxLayout.X_AXIS));
				row_filechooser.setBorder(EMPTY_BORDER);
				{
					JComponent row = row_filechooser;
					row.add(button_open);
				}
				north_panel.add(row_filechooser);
			}
			panel.add(north_panel, BorderLayout.NORTH);
			// panel.add(question_panel, BorderLayout.CENTER);

			JPanel south_panel = new JPanel();
			south_panel.setLayout(new BoxLayout(south_panel, BoxLayout.Y_AXIS));
			south_panel.add(panel_question);
			south_panel.add(button_ok);
			panel.add(south_panel, BorderLayout.CENTER);
		}

		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Quizogen");

		UIManager.put("swing.boldMetal", Boolean.FALSE);

	}

	private void initComponents() {
		frame = new javax.swing.JFrame();
		panel = new JPanel();
		panel_question = new JPanel();
		panel_question.add(new JLabel("Questions go here"));
		button_open = new JButton("Open");
		button_ok = new JButton("OK");
		button_ok.setEnabled(false);
		button_open.setMnemonic(KeyEvent.VK_O);
		fc_chooser = new JFileChooser();
	}

	private void initActions() {

		// Open
		button_open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc_chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					ev_man.fire(new FileLoadRequest(fc_chooser
							.getSelectedFile()));
				}
			}
		});

		// OK
		button_ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!correction) {
					Iterator<JTextField> it = answers.iterator();
					LinkedList<String> guess = new LinkedList<>();
					while (it.hasNext()) {
						saveText(guess, it.next());
					}
					ev_man.fire(new GuessEvent(guess));
				} else {
					ev_man.fire(new NextQuestionRequest());
				}
			}
		});

	}

	@EventHandler
	public void on(EndOfQuizzEvent event) {
		panel_question.removeAll();
		panel_question.add(new JLabel("Fin du quizz. Score : "+event.getPoints()+" points."));
		correction = false;
		button_ok.setEnabled(false);
	}

	@EventHandler
	public void on(NextQuestionEvent event) {
		answers = new LinkedList<>();
		current_question = event.getQuestion();
		correction = false;
		UpdateQuestionPanel();
		q_count++;
		label_count.setText("("+q_count+"/"+q_max+")");
	}

	@EventHandler
	public void on(FileLoadedEvent event) {
		button_ok.setEnabled(true);
		q_max = event.getQuestions.size();
	}

	@EventHandler
	public void on(CorrectionEvent event) {
		revealCorrection(event.getCorrection());
	}

	private void revealCorrection(LinkedList<Boolean> truth) {
		Iterator<JTextField> it = answers.iterator();
		Iterator<Boolean> itt = truth.iterator();
		LinkedList<String> guess = new LinkedList<>();

		while (it.hasNext()) {
			saveText(guess, it.next());
		}
		panel_question.removeAll();
		boolean atField = false;
		Iterator<String> itq = current_question.getText().iterator();
		Iterator<String> ita = guess.iterator();
		while (itq.hasNext()) {
			String str = itq.next();
			if (!atField) {
				panel_question.add(new JLabel(str));
			} else {
				revealCorrectionOver(str, ita.next(), itt.next());
			}
			atField = !atField;
		}

		panel_question.validate();
		panel_question.repaint();
	}

	private void revealCorrectionOver(String str, String guess, boolean truth) {

		if (truth) {
			panel_question.add(new JLabel("<html><font color = #"
					+ CORRECT_COLOUR + " >" + str + "</font></html>"));
		} else {
			panel_question
					.add(new JLabel("<html><font color = #" + INCORRECT_COLOUR
							+ " ><s>" + guess + "</s></font></html>"));
			panel_question.add(new JLabel("<html><font color = #"
					+ INCORRECT_COLOUR + " >" + str + "</font></html>"));

		}

	}

	private void saveText(LinkedList<String> guess, JTextField field) {
		guess.add(field.getText());

	}

	private void UpdateQuestionPanel() {
		boolean atField = false;
		
		panel_question.removeAll();
		if (current_question != null) {
			Iterator<String> it = current_question.getText().iterator();
			while (it.hasNext()) {
				String str = it.next();
				if (!atField) {
					JLabel l = new JLabel(str);
					
					if (l.getWidth() >= panel_question.getWidth()) {
						JLabel l2 = new JLabel(str.substring(0,str.length()/2));
						l = new JLabel(str.substring(str.length()/2));
						panel_question.add(l2);
					}
					
					panel_question.add(l);
				} else {
					JTextField field = new JTextField(10);
					answers.add(field);
					panel_question.add(field);
				}
				atField = !atField;
			}
		}
		panel_question.validate();
		panel_question.repaint();
	}
	
	
}
