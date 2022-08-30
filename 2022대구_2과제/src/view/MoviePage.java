package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class MoviePage extends BasePage {
	JPanel main = new JPanel();
	JCheckBox chkGenre[] = getRows("select g_name from genre").stream().map(a -> new JCheckBox(a.get(0).toString()))
			.toArray(JCheckBox[]::new), chkScreen[] = { new JCheckBox("상영중"), new JCheckBox("미상영") };
	JRadioButton radOrder[] = { new JRadioButton("예매순"), new JRadioButton("별점순") };
	JTextField txt = hintField("Movie Title", 70);

	public MoviePage() {
		ui();
	}

	private void ui() {
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		add(main);

		main.add(n = new JPanel());
		main.add(c = new JPanel(new BorderLayout(5, 5)));
		n.setLayout(new BoxLayout(n, BoxLayout.Y_AXIS));

		var cap = "장르,상영,제목".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout(10, 10));
			var temp = new JPanel(new GridLayout(0, 9));

			tmp.add(lbl(cap[i], 0, 20), "West");
			tmp.add(temp);

			if (i == 0) {
				for (int j = 0; j < chkGenre.length; j++) {
					temp.add(chkGenre[j]);
				}
			} else if (i == 1) {
				for (var chk : chkScreen) {
					temp.add(chk);
				}
			} else {
				tmp.add(txt);
				tmp.add(btnRound("검색", a -> search()), "East");
			}

			tmp.setAlignmentX(LEFT_ALIGNMENT);
			n.add(tmp);
			n.add(Box.createVerticalStrut(20));
		}

		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(cc = new JPanel(new GridLayout(0, 5, 40, 40)));

		var bg = new ButtonGroup();
		for (var rad : radOrder) {
			cn.add(rad);
			bg.add(rad);
		}

		radOrder[0].setSelected(true);

		n.setAlignmentX(CENTER_ALIGNMENT);
		c.setAlignmentX(CENTER_ALIGNMENT);

		Stream.of(chkScreen).forEach(c -> c.setSelected(true));

		search();

		n.setBorder(new MatteBorder(0, 0, 2, 0, Color.black));

		opaque(main, false);
		setBackground(Color.white);
		main.setBackground(Color.white);
	}

	private void search() {
		cc.removeAll();

		var genre = "";
		var screening = "";
		var order = "order by avg desc, sub1 desc, m.m_no desc";

		if (Stream.of(chkGenre).filter(JCheckBox::isSelected).count() > 0) {
			genre = "and (" + Stream.of(chkGenre).filter(JCheckBox::isSelected)
					.map(x -> "find_in_set(" + (Arrays.asList(chkGenre).indexOf(x) + 1 + ", replace(g_no, '.',','))"))
					.collect(Collectors.joining(" or ")) + ")";
		}

		if (chkScreen[0].isSelected() && chkScreen[1].isSelected()) {
			screening = "";
		} else if (chkScreen[0].isSelected()) {
			screening = "and m.m_no in (select sc.m_no from screening sc)";
		} else if (chkScreen[1].isSelected()) {
			screening = "and m.m_no not in (select sc.m_no from screening sc)";
		}

		if (radOrder[0].isSelected()) {
			order = " order by count(*) desc, rate desc, m.m_no";
		}else {
			order = " order by rate desc, count(*) desc, m.m_no";
		}

		var sql = String.format(
				"SELECT m.*, count(*), (select ifnull(round(avg(c_rate), 1), 0) from comment c where c.m_no=m.m_no) rate FROM movie m left outer join reservation r on m.m_no=r.m_no where m.m_title like ? %s %s group by m.m_no %s",
				screening, genre, order);
		var rs = getRows(sql, "%" + txt.getText() + "%");

		if (rs.isEmpty()) {
			eMsg("검색결과가 없습니다.");
			txt.setText("");
			search();
			return;
		}

		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout(10, 10));
			var tmpS = new JPanel(new BorderLayout(5, 5));
			var lblIdx = new JLabel("No. " + (rs.indexOf(r) + 1), 0) {
				@Override
				public void setFont(Font font) {
					super.setFont(new Font("맑은 고딕", 1, 15));
				}

				@Override
				public void setOpaque(boolean isOpaque) {
					super.setOpaque(true);
				}
			};
			var lblImg = new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 150, 200));
			var lblGenre = lbl(mapToGenre(r.get(3).toString()), 2, 13);
			int avg = (int) Math.round(Double.parseDouble(r.get(8).toString()));

			tmp.add(lblIdx, "North");
			tmp.add(lblImg);
			tmp.add(tmpS, "South");

			lblImg.setLayout(new FlowLayout(0, 2, 2));
			lblImg.add(lblAgeLimit(r.get(5).toString()), "North");

			tmpS.add(sz(lbl(r.get(1).toString(), 2, 13), 150, 20), "North");
			tmpS.add(sz(lblGenre, 150, 20));
			tmpS.add(lbl("<html><font color='yellow'>" + "★".repeat(avg) + "☆".repeat(5 - avg) + "</font> (" + r.get(8)
					+ ")", 2), "South");

			lblIdx.setOpaque(true);
			lblIdx.setForeground(Color.white);
			lblIdx.setBackground(rs.indexOf(r) < 5 ? red : Color.DARK_GRAY);

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						new MovieInfoDialog(r).setVisible(true);
					}
				}
			});

			cc.add(tmp);
		}

		opaque(cc, false);

		cc.repaint();
		cc.revalidate();
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
