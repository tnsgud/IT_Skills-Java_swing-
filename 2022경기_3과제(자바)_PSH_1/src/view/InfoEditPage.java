package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class InfoEditPage extends BasePage {
	JTextField txt[] = new JTextField[2];
	JRadioButton rad[] = new JRadioButton[2];
	static JLabel pr;
	JLabel ageFilter;
	ArrayList<String> filters = new ArrayList<>(Arrays.asList(user.get(7).toString().split(",")));

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.black);
		l.setBackground(Color.white);
		return l;
	}

	@Override
	public JButton btn(String c, ActionListener a) {
		var b = super.btn(c, a);
		b.setBackground(null);
		b.setForeground(Color.black);
		b.setFont(new Font("맑은 고딕", 1, 15));
		return b;
	}

	public static void main(String[] args) {
		new LoginFrame();
	}

	public InfoEditPage() {
		super("정보수정");

		setLayout(new BorderLayout(50, 50));

		add(c = new JPanel());
		add(e = new JPanel(), "East");
		add(s = new JPanel(), "South");
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		e.setLayout(new BoxLayout(e, BoxLayout.Y_AXIS));

		var cap = "프로필사진,닉네임,PW,프로필 공개 여부, 보유 잔액".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2, 15), 150, 20));

			if (i == 0) {
				var img = new JLabel(getIcon(user.get(8), 100, 100));
				img.setBorder(new LineBorder(Color.black));
				tmp.add(img);
			} else if (i < 3) {
				tmp.add(txt[i - 1] = new JTextField(15));
				txt[i - 1].setText(user.get(4 - i).toString());
			} else if (i == 3) {
				var bg = new ButtonGroup();
				var ca = "공개,비공개".split(",");

				for (int j = 0; j < ca.length; j++) {
					rad[j] = new JRadioButton(ca[j]);
					tmp.add(rad[j]);
					bg.add(rad[j]);
				}

				rad[toInt(user.get(6))].setSelected(true);
			} else {
				tmp.add(pr = lbl(format(toInt(user.get(5))) + "원", 2, 15));
				tmp.add(btn("충전하기", a -> new ChargeDialog().setVisible(true)));
			}

			c.add(tmp);
		}

		ageFilter = lbl("연령제한", 2, 1, 25, e -> {
			var me = (JLabel) e.getSource();

			if (me.isEnabled())
				filters.remove("12");
			else
				filters.add(0, "12");

			me.setEnabled(!me.isEnabled());
		});

		ageFilter.setEnabled(filters.contains("12"));

		setFilter();

		s.add(btn("수정하기", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!getOne("select * from user where u_name = ? and u_no <> ?", txt[0].getText(), user.get(0)).isEmpty()) {
				eMsg("중복된 닉네임입니다.");
				return;
			}

			iMsg("수정이 완료되었습니다.");
			execute("update user set u_name=?,u_pw=?,u_ox=?,u_filter=?", txt[0].getText(), txt[1].getText(),
					rad[0].isSelected() ? 0 : 1,
					filters.size() == 0 ? "0" : filters.stream().collect(Collectors.joining(",")));
		}));

		mf.repaint();
		setBackground(Color.white);
		setOpaque(true);
	}

	void setFilter() {
		e.removeAll();

		e.add(sz(lbl("검색 제외", 0, 35), 300, 0));
		e.add(ageFilter);

		filters.stream().filter(f -> !(f.equals("12") || f.equals("0"))).map(f -> lbl(g_genre[toInt(f)], 0, 25))
				.forEach(e::add);

		e.add(lblAdd(filters));

		for (var com : e.getComponents()) {
			((JComponent) com).setAlignmentX(Component.CENTER_ALIGNMENT);
		}

		repaint();
		revalidate();
	}
}
