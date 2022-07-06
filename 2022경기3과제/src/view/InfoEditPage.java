package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class InfoEditPage extends BasePage {
	JTextField txt[] = new JTextField[2];
	JRadioButton radio[] = new JRadioButton[2];
	static JLabel pr;
	JLabel ageFilter, add;
	ArrayList<String> filters = new ArrayList<String>(Arrays.asList(user.get(7).toString().split(",")));

	public InfoEditPage() {
		super("정보수정");

		add(c = sz(new JPanel(new FlowLayout(0, 5, 5)), 450, 0));
		add(e = sz(new JPanel(new FlowLayout(1, 5, 5)), 350, 200), "East");
		add(s = new JPanel(), "South");

		var cap = "프로필사진,닉네임,PW,프로필 공개 여부,보유 잔액".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 5, 5));
			var lbl = sz(lbl(cap[i], 2, 15), 150, 20);

			tmp.add(lbl);

			if (i == 0) {
				var img = new JLabel(getIcon(user.get(8), 100, 100));
				img.setBorder(new LineBorder(Color.black));
				tmp.add(img);
			} else if (i < 3) {
				tmp.add(txt[i - 1] = new JTextField(15));
				txt[i - 1].setText(user.get(4 - i).toString());
			} else if (i == 3) {
				var ca = "공개,비공개".split(",");
				var bg = new ButtonGroup();

				for (int j = 0; j < ca.length; j++) {
					radio[j] = new JRadioButton(ca[j]);
					tmp.add(radio[j]);
					bg.add(radio[j]);
				}

				radio[toInt(user.get(6))].setSelected(true);
			} else {
				tmp.add(pr = lbl(format(toInt(user.get(5))) + "원", 2, 20));
				tmp.add(btn("충전하기", a -> {
					new Charge().setVisible(true);
				}));

				pr.setForeground(Color.black);
			}

			c.add(sz(tmp, 450, i == 0 ? 120 : 40));
		}

		ageFilter = lbl("연령제한", 0, 25);
		add = new JLabel(getIcon("./datafiles/기본사진/10.png", 30, 30));
		setFilter();

		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new GenreSelect(InfoEditPage.this).setVisible(true);
			}
		});
		ageFilter.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var me = (JLabel) e.getSource();

				if (me.isEnabled()) {
					filters.set(0, "0");
					me.setEnabled(false);
				} else {
					filters.set(0, "12");
					me.setEnabled(true);
				}
			}
		});
		ageFilter.setEnabled(filters.contains("12"));

		s.add(btn("수정하기", a -> {

		}));
	}

	void setFilter() {
		e.removeAll();

		e.add(lbl("검색 제외", 0, 35));
		e.add(ageFilter);

		System.out.println(filters);

		for (var fi : filters) {
			if (fi.equals("12") || fi.equals("0"))
				continue;

			var l = lbl(g_genre[toInt(fi)], 0, 25);

			e.add(l);
		}

		e.add(add);

		for (var com : e.getComponents()) {
			sz((JComponent) com, 350, 30);
		}

		repaint();
		revalidate();
	}

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.black);
		return lbl;
	}

	public static void main(String[] args) {
		new Login();
	}
}
