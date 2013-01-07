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
import event.FileLoadRequest;
import event.NextQuestionEvent;
import fr.swampwolf.events.EventHandler;
import fr.swampwolf.events.EventManager;
import fr.swampwolf.events.interfaces.Observer;

public class Window implements Observer {

	/** Constants **/
	private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(
			0, 10, 10, 10);
	private static final String L_LABEL_TITLE = "Quizogen";

	/** Components **/
	private JFrame frame;
	private JPanel panel;
	private JFileChooser fc_chooser;
	private JButton button_open;
	private JPanel question_panel;
	private JButton button_ok;

	/** Control **/
	private EventManager ev_man;
	private Controller controller;
	private boolean correction = false;
	private Question current_question;
	private LinkedList<JTextField> answers;

	public Window(EventManager ev_man, Controller controller) {
		this.ev_man = ev_man;
		ev_man.addObserver(this);
		this.controller = controller;
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
			south_panel.add(question_panel);
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
		question_panel = new JPanel();
		question_panel.add(new JLabel("Questions go here"));
		button_open = new JButton("Open");
		button_ok = new JButton("OK");
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
				ev_man.fire(new OKButtonEvent());
				if (!correction) {
					revealCorrection();
				}
			}
		});

	}

	@EventHandler
	public void on(NextQuestionEvent event) {
		answers = new LinkedList<>();
		
		Question q = event.getQuestion();
		UpdateQuestionPanel(q);
		current_question = q;
	}

	private void revealCorrection() {
		Iterator<JTextField> it = answers.iterator();
		LinkedList<String> guess = new LinkedList<>();
		while (it.hasNext()) {
			saveText(guess,it.next());
		}
		question_panel.removeAll();
		boolean atField = false;
		Iterator<String> itq = current_question.getText().iterator();
		Iterator<String> ita = guess.iterator();
		while (itq.hasNext()) {
			String str = itq.next();
			if (!atField) {
				question_panel.add(new JLabel(str));
			} else {
				revealCorrectionOver(str,ita.next());
			}
			atField = !atField;
		}

		question_panel.validate();
		question_panel.repaint();
	}

	private void revealCorrectionOver(String str, String guess) {
		if (str.equals(guess)) {
			question_panel.add(new JLabel("<html><font color = #00FF00 >"+str+"</font></html>"));
			//00FF00=Green
		} else {
			question_panel.add(new JLabel("<html><font color = #FF0000 ><s>"+guess+"</s></font></html>"));
			//FF0000=Red
		}
		
	}

	private void saveText(LinkedList<String> guess, JTextField field) {
		guess.add(field.getText());
		
	}

	private void UpdateQuestionPanel(Question question) {
		boolean atField = false;
		question_panel.removeAll();
		if (question != null) {
			Iterator<String> it = question.getText().iterator();
			while (it.hasNext()) {
				String str = it.next();
				if (!atField) {
					question_panel.add(new JLabel(str));
				} else {
					JTextField field = new JTextField(10);
					answers.add(field);
					question_panel.add(field);
				}
				atField = !atField;
			}
		}
		question_panel.validate();
		question_panel.repaint();
	}
}
