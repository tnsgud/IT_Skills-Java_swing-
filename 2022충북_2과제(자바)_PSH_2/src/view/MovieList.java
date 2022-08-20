package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class MovieList extends BaseFrame {
	JComboBox<String> com = new JComboBox<>(
			getRows("select g_name from genre").stream().map(a -> a.get(0).toString()).toArray(String[]::new));
	JTextField txt = new JTextField(20);

	public static void main(String[] args) {
		new MovieList();
	}

	public MovieList() {
		super("무비리스트", 650, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(c = new JPanel()));
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		n.add(lbl("MovieList", 0, 40));
		n.add(ns = new JPanel(new FlowLayout(1)), "South");

		ns.add(com);
		ns.add(txt);
		ns.add(event(new JLabel(getIcon("./datafile/아이콘/Search.png", 30, 30)), e -> searh()));

		com.insertItemAt("전체", 0);
		com.setSelectedIndex(0);
		
		searh();
		
		setVisible(true);
	}

	private void searh() {
		c.removeAll();

		var tmp = new JPanel(new FlowLayout(0, 0, 0));
		var rs = getRows("select m_no, m_name from movie where g_no like ? and m_name like ?",   7);
		if(rs.isEmpty() ) {
			eMsg("검색 결과가 없습니다.");
			txt.setText("");
			com.setSelectedIndex(0);
			searh();
			return;
		}
		for (var r : rs) {
			int i = rs.indexOf(r);
			var temp = new JPanel(new BorderLayout());
			var img = event(new JLabel(getIcon("./datafile/영화/" + r.get(1) + ".jpg", 150, 220)), e -> {
				if (e.getClickCount() != 2)
					return;

				m_no = toInt(r.get(0));
				new MovieDetail().addWindowListener(new Before(this));
			});

			temp.add(img);
			temp.add(lbl(r.get(1).toString(), 0, 12), "South");

			temp.setBorder(new LineBorder(Color.black));

			tmp.add(sz(temp, 150, 230));

			if (i % 4 < 3) {
				tmp.add(Box.createHorizontalStrut(5));
			}

			if (i % 4 == 0) {
				c.add(tmp);
			} else if (i % 4 == 3) {
				c.add(Box.createVerticalStrut(5));
				tmp = new JPanel(new FlowLayout(0, 0, 0));
			}
		}

		c.repaint();
		c.revalidate();
	}
}
