package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import model.Question;

public class QuizoParser {

	BufferedReader input;
	private static final String SEPARATOR = "Q";
	private static final char BLOCK_START = '{';
	private static final char BLOCK_END = '}';
	
	
	
	public ArrayList<Question> parse(File file) throws IOException {
		input = new BufferedReader(new FileReader(file));
		ArrayList<Question> questions = new ArrayList<Question>();
		Question q = getNextQuestion();
		while (q != null) {
			questions.add(q);
			q = getNextQuestion();
		}

		return questions;
	}

	private Question getNextQuestion() throws IOException {
		Question q = new Question();
		String s = "";
		StringBuilder sb = new StringBuilder();
		while (!s.equals(SEPARATOR)) {
			s = input.readLine();
			if (s==null)
				return null;
		}
		s = input.readLine();
		StringReader line = new StringReader(s);
		
		char c = (char)line.read();
		while (c != (char)-1) {
			if (c!=BLOCK_START) {
				sb.append(c);
			} else {
				q.addText(sb.toString());
				sb = new StringBuilder();
				c = (char)line.read();
				while (c != BLOCK_END) {
					sb.append(c);
					c = (char)line.read();
				}
				q.addText(sb.toString());
				sb = new StringBuilder();
			}
			c = (char)line.read();
		}
		if (sb.length()>0) {
			q.addText(sb.toString());
		}
		
		return q;
	}
}
