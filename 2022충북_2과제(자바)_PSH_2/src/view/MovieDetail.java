package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MovieDetail extends BaseFrame {
	public MovieDetail() {
		super("", 500, 500);

		var data = getRows("select * from movie where m_no = ?", m_no).get(0);
		var area = new JTextArea(data.get(6).toString());
		var scr = new JScrollPane(area);
		var img = new JLabel(getIcon("./datafile/영화/" + data.get(1) + ".jpg", 200, 300));

		add(img, "West");
		add(c = new JPanel(new BorderLayout()));

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(scr);

		cn.add(lbl(data.get(5).toString(), 2, 20));
		cn.add(cw = new JPanel(new GridLayout(0, 1, 0, 0)), "West");
		cn.add(ce = new JPanel(new FlowLayout(2)), "East");

		var cap = "장르,평점".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(lbl(cap[i], 2));

			if (i == 0) {
				tmp.add(lblSan(
						"<html><font color='gray'>" + getOne("select g_name from genre where g_no = ?", data.get(1)), 2,
						0, 12));
			} else {
				tmp.add(lblSan("<html><font color='gray'>" + data.get(4), 2, 0, 12));
			}

			cw.add(tmp);
		}

		ce.add(btn("예약하기", a -> new Reserve().addWindowListener(new Before(this))));

		scr.setBorder(new TitledBorder(new LineBorder(Color.black), "설명", 1, 0, lbl("", 2, 20).getFont()));

		setVisible(true);
	}
}
