package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MovieList extends BaseFrame {
	JComboBox<String> com = new JComboBox<String>(("전체," + getRows("select g_name from genre").stream()
			.map(a -> a.get(0).toString()).collect(Collectors.joining(","))).split(","));
	JTextField txt = new JTextField(40);
	JLabel img = new JLabel(getIcon("./datafile/아이콘/Search.png", 30, 30));

	public MovieList() {
		super("무비리스트", 700, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 4, 5, 5))));

		n.add(lbl("MovieList", 0, 30));
		n.add(ns = new JPanel(), "South");

		ns.add(com);
		ns.add(txt);
		ns.add(img);

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				search();
			}
		});

		search();
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5,5,5,5));

		setVisible(true);
	}

	private void search() {
		c.removeAll();

		for (var rs : getRows("select m_no, m_name from movie where g_no like ? and m_name like ?",
				"%" + (com.getSelectedIndex() == 0 ? "" : com.getSelectedItem()) + "%", "%" + txt.getText() + "%")) {
			var tmp = new JPanel(new BorderLayout());
			var img = new JLabel(getIcon("./datafile/영화/" + rs.get(1) + ".jpg", 150, 220));

			tmp.add(img);
			tmp.add(lbl(rs.get(1).toString(), 0, 12), "South");

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						new MovieDetail(toInt(rs.get(0))).addWindowListener(new Before(MovieList.this));
					}
				}
			});

			tmp.setBorder(new LineBorder(Color.black));

			c.add(sz(tmp, 150, 230));
		}

		if (c.getComponentCount() == 0) {
			eMsg("검색결과가 없습니다.");
			com.setSelectedIndex(0);
			txt.setText("");
			search();
			return;
		}


		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new MovieList();
	}
}
