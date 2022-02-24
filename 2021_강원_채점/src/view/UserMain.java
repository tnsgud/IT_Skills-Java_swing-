package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class UserMain extends BaseFrame {
	JLabel n = img("main.jpg", 1000, 300);
	String[] cap = "계정,예매,로그아웃".split(","), h = "출발지,도착지,날짜".split(",");
	JTextField txt[] = new JTextField[3];
	JPopupMenu pop = new JPopupMenu();
	LocalDate now = LocalDate.parse("2021-10-06");
	JButton[] up = new JButton[3], down = new JButton[3];
	JLabel lbl[] = new JLabel[3];
	boolean isClick = false;

	public UserMain() {
		super(1000, 600);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		for (var t : txt) {
			t.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var com = ((JTextField) e.getSource());

					if (com == txt[2] && e.getButton() == 3) {
						pop.show(com, e.getX(), e.getY());
						isClick = true;
					} else if (e.getButton() == 3) {
						pop(com, 0).show(com, e.getX(), e.getY());
					}
				}
			});
		}

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (isClick && !pop.isShowing()) {
					txt[2].setText(now + "");
					isClick = false;
				}
			}
		});
	}

	private void ui() {
		var n_n = new JPanel(new FlowLayout(4));
		var n_c = new JPanel(new GridBagLayout());
		var n_c_c = new JPanel(new BorderLayout(10, 10));
		var n_c_c_c = new JPanel(new FlowLayout());

		var c_c = new JPanel(new FlowLayout(0, 50, 50));

		add(n, "North");
		add(c = new JPanel(new BorderLayout()));
		n.setLayout(new BorderLayout());
		n.add(n_n, "North");
		n.add(n_c);
		n_c.add(n_c_c);
		n_c_c.add(lbl("예매", 2, 25), "North");
		n_c_c.add(n_c_c_c);

		for (int i = 0; i < h.length; i++) {
			n_c_c_c.add(txt[i] = new JHintField(h[i], 15));
			if (i == 0) {
				n_c_c_c.add(btn("<html>←<br/>→</html>", a -> {
					if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
						return;
					}

					var tmp = txt[0].getText();
					txt[0].setText(txt[1].getText());
					txt[1].setText(tmp);
				}));
			}
		}

		n_c_c_c.add(btn("조회", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
					return;
				}
			}
			var sno = toInt(getOne(
					"SELECT * from v1, schedule s where l11name = ? and l21name = ? and l12name = ? and l22name = ? and v1.sno = s.no and s.date >= ?",
					txt[0].getText().split(" ")[0], txt[0].getText().split(" ")[1], txt[1].getText().split(" ")[0],
					txt[1].getText().split(" ")[1], txt[2].getText()));
			if (sno == -1) {
				eMsg("예매 가능한 일정이 없습니다.");
				return;
			}

			var array = new ArrayList<Integer>();
			var rs = rs(
					"SELECT * from v1, schedule s where l11name = ? and l21name = ? and l12name = ? and l22name = ? and v1.sno = s.no and s.date >= ?",
					txt[0].getText().split(" ")[0], txt[0].getText().split(" ")[1], txt[1].getText().split(" ")[0],
					txt[1].getText().split(" ")[1], txt[2].getText());
			try {
				while(rs.next()) {
					array.add(rs.getInt(1));
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Reserve(array).addWindowListener(new Before(UserMain.this));
		}));

		n_n.setOpaque(false);
		n_c.setOpaque(false);
		n_n.add(themeBtn(this));
		for (var c : cap) {
			n_n.add(btn(c, a -> {
				if (c.equals("계정")) {
					var an = JOptionPane.showInputDialog(null, "비밀번호를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);
					if (!getOne("select * from user where pwd=?", an).isEmpty()) {
						new Account();
					}
				} else if (c.equals("예매")) {
					new LookUp();
				} else {
					uno = 0;
					dispose();
				}
			}));
		}

		c.add(lbl("추천 여행지", 2, 35), "North");
		c.add(c_c);
		var rs = rs(
				"select l.name, ri.img from recommend r, location l, recommend_info ri where l.no = r.location_no and ri.recommend_no = r.no group by r.no ");
		try {
			while (rs.next()) {
				var p = new JPanel(new BorderLayout());
				p.add(img(rs.getBlob(2).getBinaryStream().readAllBytes(), 120, 120));
				p.setBorder(new CompoundBorder(
						new EmptyBorder((rs.getRow() % 2 == 0 ? 20 : 0), 0, rs.getRow() % 2 == 0 ? 0 : 20, 0),
						new TitledBorder(new LineBorder(Color.black), rs.getString(1))));
				c_c.add(p);
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pop.setLayout(new GridLayout(1, 0));
		var w = new JPanel(new BorderLayout());
		var c = new JPanel(new BorderLayout());
		var e = new JPanel(new BorderLayout());
		Stream.of(w, c, e).forEach(p -> pop.add(p));
		for (int i = 0; i < up.length; i++) {
			up[i] = new JButton("▲");
			down[i] = new JButton("▼");

			up[i].setName(i + "");
			down[i].setName(i + "");

			up[i].addActionListener(a -> {
				var btn = ((JButton) a.getSource());
				var idx = toInt(btn.getName());

				if (idx == 0) {
					now = now.plusYears(1);
				} else if (idx == 1) {
					now = now.plusMonths(1);
				} else {
					now = now.plusDays(1);
				}

				now = LocalDate.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth());

				for (int j = 0; j < lbl.length; j++) {
					lbl[j].setText((j == 0 ? now.getYear() : j == 1 ? now.getMonthValue() : now.getDayOfMonth()) + "");
				}
			});
			down[i].addActionListener(a -> {
				var btn = ((JButton) a.getSource());
				var idx = toInt(btn.getName());

				if (idx == 0) {
					now = now.minusYears(1);
				} else if (idx == 1) {
					now = now.minusMonths(1);
				} else {
					now = now.minusDays(1);
				}

				for (int j = 0; j < lbl.length; j++) {
					lbl[j].setText((j == 0 ? now.getYear() : j == 1 ? now.getMonthValue() : now.getDayOfMonth()) + "");
				}
			});

			var p = (i == 0 ? w : i == 1 ? c : e);
			p.add(up[i], "North");
			p.add(lbl[i] = lbl((i == 0 ? now.getYear() : i == 1 ? now.getMonthValue() : now.getDayOfMonth()) + "", 0));
			p.add(down[i], "South");
		}

	}

	public static void main(String[] args) {
		uno = 1;
		new UserMain();
	}
}
