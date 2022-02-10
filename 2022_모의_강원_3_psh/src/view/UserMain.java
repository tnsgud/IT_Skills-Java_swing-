package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class UserMain extends BaseFrame {
	String[] h = "출발지,도착지,날짜".split(",");
	JTextField[] txt = new JTextField[h.length];
	JPopupMenu pop = new JPopupMenu();
	LocalDate date = LocalDate.of(2021, 10, 6);
	boolean click = false;

	public UserMain() {
		super(1300, 650);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		for (var t : txt) {
			t.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						var idx = toInt(((JTextField) e.getSource()).getName());
						(idx < 2 ? showLocaltion(t, 0) : pop).show(t, e.getX(), e.getY());
						click = idx == 2;
					}
				}
			});
		}

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (click) {
					txt[2].setText(date + "");
				}
			}
		});
	}

	private void ui() {
		setLayout(new GridLayout(0, 1));
		{
			var root = img("main.jpg");
			var n = new JPanel(new FlowLayout(4));
			var c_c = new JPanel(new BorderLayout(20, 20));
			var c_c_c = new JPanel(new FlowLayout());

			root.setLayout(new BorderLayout());

			add(root);
			root.add(n, "North");
			root.add(c = new JPanel(new GridBagLayout()));
			c.add(c_c);

			for (var cap : "테마,계정,예매,로그아웃".split(",")) {
				n.add(cap.equals("테마") ? themeBtn(UserMain.this) : btn(cap, a -> {
					if (a.getActionCommand().equals("계정")) {
						var an = JOptionPane.showInputDialog(null, "비밀번호를 입력하세요.", "입력", JOptionPane.QUESTION_MESSAGE);
						if (getOne("select pwd from user where no=?", uno).equals(an)) {
							new Account();
						}
					} else if (a.getActionCommand().equals("예매")) {
						new LookUp();
					} else {
						uno = 0;
						dispose();
					}
				}));
			}

			c_c.add(lbl("예매", 2, 35), "North");
			c_c.add(c_c_c);

			for (int i = 0; i < h.length; i++) {
				c_c_c.add(txt[i] = new JHintField(15, h[i]));
				txt[i].setName(i + "");

				if (i == 0) {
					c_c_c.add(btn("<html>←<br/>→</html>", a -> {
						var tmp = txt[0].getText();
						txt[0].setText(txt[1].getText());
						txt[1].setText(tmp);
					}));
				}
			}

			c_c_c.add(sz(btn("조회", a -> {
				for (var t : txt) {
					if(t.getText().isEmpty()) {
						eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
						return;
					}
				}
				
				var sno = new ArrayList<Integer>();
				var data = new ArrayList<String>();
				for (int i = 0; i < 2; i++) {
					Arrays.stream(txt[i].getText().split(" ")).forEach(s->data.add(s));
				}
				var rs = rs("select * from v1 where l11name=? and l21name=? and l12name=? and l22name=?", data.toArray());
				try {
					while(rs.next()) {
						sno.add(rs.getInt(1));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(sno.size() == 0) {
					eMsg("예매할 수 없는 일정입니다.");
					return; 
				}
				
				new Reserve(sno);
			}), 150, 50));

			n.setOpaque(false);
			c.setOpaque(false);
		}

		{
			var root = new JPanel(new GridBagLayout());
			var c = new JPanel(new BorderLayout(50, 50));
			var c_c = new JPanel(new FlowLayout(1, 80, 0));

			add(root);
			root.add(c);
			c.add(lbl("추천 여행지", 2, 25), "North");
			c.add(c_c);

			var rs = rs(
					"select l.no, l.name, ri.img from location l, recommend_info ri, recommend r where l.no = r.location_no and ri.recommend_no = r.no group by l.name");
			try {
				while (rs.next()) {
					var p = new JPanel();
					var lbl = img(rs.getBlob(3).getBinaryStream().readAllBytes(), 130, 130);
					var m = new JPopupMenu();

					m.setName(rs.getString(1));

					for (var cap : "상세설명,예매".split(",")) {
						var i = new JMenuItem(cap);
						i.addActionListener(a -> {
						});

						m.add(i);
					}

					p.add(lbl);
					c_c.add(p);

					lbl.setComponentPopupMenu(m);

					var t = rs.getRow() % 2 == 0 ? 25 : 0;
					var b = rs.getRow() % 2 == 0 ? 0 : 25;

					p.setBorder(new CompoundBorder(new EmptyBorder(t, 0, b, 0),
							new TitledBorder(new LineBorder(Color.black), rs.getString(2))));
				}
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		{
			pop.setLayout(new GridLayout(1, 0));
			var p = new JPanel[3];
			var lbl = new JLabel[3];
			var up = new JButton[3];
			var down = new JButton[3];

			for (int j = 0; j < p.length; j++) {
				pop.add(p[j] = new JPanel(new BorderLayout()));
				p[j].add(up[j] = btn("▲", a -> {
					var idx = toInt(((JButton) a.getSource()).getName());

					switch (idx) {
					case 0:
						date = date.plusYears(1);
						break;
					case 1:
						date = date.plusMonths(1);
						break;
					case 2:
						date = date.plusDays(1);
						break;
					}

					for (int k = 0; k < lbl.length; k++) {
						var t = (k == 0 ? date.getYear() : k == 1 ? date.getMonthValue() : date.getDayOfMonth());
						lbl[k].setText(t + "");
					}

					if (idx < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				}), "North");
				p[j].add(lbl[j] = lbl(
						(j == 0 ? date.getYear() : j == 1 ? date.getMonthValue() : date.getDayOfMonth()) + "", 0));
				p[j].add(down[j] = btn("▼", a -> {
					var idx = toInt(((JButton) a.getSource()).getName());

					switch (idx) {
					case 0:
						date = date.minusYears(1);
						break;
					case 1:
						date = date.minusMonths(1);
						break;
					case 2:
						date = date.minusDays(1);
						break;
					}

					for (int k = 0; k < lbl.length; k++) {
						var t = (k == 0 ? date.getYear() : k == 1 ? date.getMonthValue() : date.getDayOfMonth());
						lbl[k].setText(t + "");
					}

					if (idx < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				}), "South");

				up[j].setName(j + "");
				down[j].setName(j + "");
			}
		}
	}

	public static void main(String[] args) {
		uno = 1;
		new Login();
	}
}
