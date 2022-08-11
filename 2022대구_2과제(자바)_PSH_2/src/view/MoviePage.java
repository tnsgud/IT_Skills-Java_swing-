package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
			.toArray(JCheckBox[]::new),
			chkScreen[] = Stream.of("상영중,비상영".split(",")).map(JCheckBox::new).toArray(JCheckBox[]::new);
	JTextField txt = txt("Movie Title", 70);
	JRadioButton radOrder[] = Stream.of("예매순,별점순".split(",")).map(JRadioButton::new).toArray(JRadioButton[]::new);

	public MoviePage() {
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		add(main);

		main.add(n = new JPanel());
		main.add(c = new JPanel(new BorderLayout(5, 5)));
		n.setLayout(new BoxLayout(n, BoxLayout.Y_AXIS));

		var cap = "장르,설명,제목".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp1 = new JPanel(new BorderLayout(10, 10));
			var tmp2 = new JPanel(new GridLayout(0, 9));

			tmp1.add(lbl(cap[i], 0, 20), "West");
			tmp1.add(tmp2);

			if (i == 0) {
				Stream.of(chkGenre).forEach(tmp2::add);
			} else if (i == 1) {
				Stream.of(chkScreen).forEach(tmp2::add);
			} else {
				tmp1.add(txt);
				tmp1.add(btnRound("검색", a -> search()), "East");
			}

			tmp1.setAlignmentX(LEFT_ALIGNMENT);

			n.add(tmp1);
			n.add(Box.createVerticalStrut(20));
		}

		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(cc = new JPanel(new GridLayout(0, 5, 40, 40)));

		var bg = new ButtonGroup();
		Stream.of(radOrder).forEach(r -> {
			bg.add(r);
			cn.add(r);
		});
		
		radOrder[0].setSelected(true);

		n.setBorder(new MatteBorder(0, 0, 2, 0, Color.black));
		n.setAlignmentX(CENTER_ALIGNMENT);
		c.setAlignmentX(CENTER_ALIGNMENT);

		Stream.of(chkScreen).forEach(c -> c.setSelected(true));

		search();
	}

	void search() {
		cc.removeAll();
		var genre = "";
		var screening = "";
		var order = "order by avg desc, sub desc, m.m_no desc";

		if (Stream.of(chkGenre).filter(JCheckBox::isSelected).count() > 0) {
			genre = "and (" + Stream.of(chkGenre).filter(JCheckBox::isSelected)
					.map(x -> "find_in_set(" + (Arrays.asList(chkGenre).indexOf(x) + 1 + ", replace(g_no,'.', ','))"))
					.collect(Collectors.joining(","));
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
		} else {
			order = " order by rate desc, count(*) desc, m.m_no";
		}

		var sql = String.format(
				"select m.*, count(*), (select ifnull(round(avg(c_rate), 1), 0) from comment c where c.m_no = m.m_no) rate from movie m left outer join reservation r on m.m_no = r.m_no where m_title like ? %s %s group by m.m_no %s",
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

		cc.repaint();
		cc.revalidate();
	}
}
