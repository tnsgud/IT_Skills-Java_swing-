package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MovieDetail extends BaseFrame {
	ArrayList<Object> data = getRows("select * from movie where m_no = ?", m_no).get(0);
	JTextArea area = new JTextArea(data.get(6).toString());
	JScrollPane scr;

	public MovieDetail() {
		super("영화상세정보", 600, 300);
		setLayout(new BorderLayout(5, 5));

		add(new JLabel(getIcon("./datafile/영화/" + data.get(5) + ".jpg", 200, 300)), "West");
		add(c = new JPanel(new BorderLayout()));

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(scr = new JScrollPane(area));

		var cap = "장르,평점".split(",");
		var txt = "<html>";
		for (int i = 0; i < cap.length; i++) {
			txt += "<font color='black'>" + cap[i] + "</font><font color='gray'>"
					+ (i == 0 ? getOne("select g_name from genre where g_no = ?", data.get(1)) : data.get(4)) + "<br>";
		}

		cn.add(lblSerif(data.get(5).toString(), 2, 1, 20), "North");
		cn.add(lbl(txt, 2), "West");
		cn.add(ce = new JPanel(), "East");

		ce.add(btn("예매하기", a -> new Reserve().addWindowListener(new Before(this))));

		scr.setBorder(new TitledBorder(new LineBorder(Color.black), "설명", 1, 0, lblHY("", 0, 0, 15).getFont()));
		area.setEditable(false);
		area.setLineWrap(true);

		setVisible(true);
	}
}
