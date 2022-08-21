package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class MovieList extends BaseFrame {
	JComboBox<String> com = new JComboBox<String>(("전체," + getRows("select g_name from genre").stream()
			.map(a -> a.get(0).toString()).collect(Collectors.joining(","))).split(","));
	JTextField txt = new JTextField(40);
	JLabel search = event(new JLabel(getIcon("./datafile/아이콘/Search.png", 50, 50)), e -> search());

	public MovieList() {
		super("무비리스트", 650, 700);

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(new JScrollPane(c = new JPanel()));
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		n.add(lbl("MovieList", 0, 30), "North");
		n.add(nc = new JPanel());

		nc.add(com);
		nc.add(txt);
		nc.add(search);

		search();

		setVisible(true);
	}

	private void search() {
		c.removeAll();

		System.out.println("select m_no, m_name from movie where m_name like ? and "
				+ (com.getSelectedIndex() == 0 ? "true" : "g_no=" + com.getSelectedIndex()));

		var rs = getRows(
				"select m_no, m_name from movie where m_name like ? and "
						+ (com.getSelectedIndex() == 0 ? "true" : "g_no=" + com.getSelectedIndex()),
				"%" + txt.getText() + "%");
		var tmp = new JPanel(new FlowLayout(0, 0, 0));
		
		if(rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			txt.setText("");
			com.setSelectedIndex(0);
			search();
			return;
		}
		for (var r : rs) {
			var i = rs.indexOf(r);
			var temp = new JPanel(new BorderLayout());
			var img = event(new JLabel(getIcon("./datafile/영화/" + r.get(1) + ".jpg", 150, 250)), e -> {
				if (e.getClickCount() != 2)
					return;

				m_no = toInt(r.get(0));
				new MovieDetail().addWindowListener(new Before(this));
			});

			img.setBorder(new LineBorder(Color.black));

			temp.add(img);
			temp.add(lbl(r.get(1).toString(), 0), "South");

			temp.setBorder(new LineBorder(Color.black));

			tmp.add(sz(temp, 150, 270));

			if (i % 4 < 3) {
				tmp.add(Box.createHorizontalStrut(5));
			}

			if (i % 4 == 0) {
				c.add(tmp);
				c.add(Box.createVerticalStrut(5));
			} else if (i % 4 == 3) {
				tmp = new JPanel(new FlowLayout(0, 0, 0));
			}
		}

		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new MovieList();
	}
}
