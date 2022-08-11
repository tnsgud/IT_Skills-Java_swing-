package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MovieInfoDialog extends BaseDialog {
	JScrollPane scr;
	ArrayList<Object> movie;

	public MovieInfoDialog(ArrayList<Object> movie) {
		super(movie.get(1).toString(), 500, 500);
		this.movie = movie;

		add(scr = scroll(c = new JPanel()));
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		movieInfo();
		synopsis();
		audience();
		review();

		setVisible(true);
	}

	private void review() {

	}

	private void audience() {
		var c = new JPanel(new BorderLayout(5, 5));
		var chart1 = new JPanel() {
		};
		var chart2 = new JPanel() {
		};
		var lblTitle = lbl("주시청층", 2, 15);
		
		c.setAlignmentX(LEFT_ALIGNMENT);
	}

	private void synopsis() {
		var c = new JPanel(new BorderLayout(5, 5));
		var lblTitle = lbl("시놉시스", 2, 15);
		var area = new JTextArea();

		area.setText(movie.get(2).toString());
		area.setLineWrap(true);
		area.setEditable(false);
		area.setBackground(Color.white);

		c.setAlignmentX(LEFT_ALIGNMENT);

		lblTitle.setBorder(new MatteBorder(0, 2, 1, 0, Color.black));

		c.add(lblTitle, "North");
		c.add(area);

		this.c.add(c);
	}

	private void movieInfo() {
		var c = sz(new JPanel(new BorderLayout(20, 5)), 450, 200);
		var cc = new JPanel();
		var img = new JLabel(getIcon("./지급자료/image/movie/" + movie.get(0) + ".jpg", 150, 200));
		var avg = (int) Math.round(Double.parseDouble(movie.get(8).toString()));
		var txt = String.format("<html>감독 : %s<br>장르 : %s / 기본 : %s, %s<br>별점 : <font color='yellow'>%s</font> %s",
				movie.get(6).toString(), mapToGenre(movie.get(3).toString()), m_age[toInt(movie.get(5))],
				movie.get(4) + "분", "★".repeat(avg) + "☆".repeat(5 - avg), "(" + movie.get(8).toString() + ")");
		var lblInfo = lbl(txt, 2);

		c.add(img, "West");
		c.add(cc);

		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cc.add(lbl(movie.get(1).toString(), 2, 20), "North");
		cc.add(lblInfo);

		c.setAlignmentX(LEFT_ALIGNMENT);
		lblInfo.setBorder(
				new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));

		this.c.add(c);
		this.c.add(Box.createVerticalStrut(20));
	}
}
