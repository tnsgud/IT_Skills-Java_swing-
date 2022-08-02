package view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Receipt extends BaseFrame {
	String code;

	public Receipt(Purchase p) {
		super("영수증", 500, 500);

		add(hylbl("영수증", 0, 25), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(cc = new JPanel(new GridLayout(0, 1)));
		c.add(cs = sz(new JPanel(new GridLayout(1, 0, 5, 5)), 0, 60), "South");

		code = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + String.format("%03d", toInt(user.get(0)))
				+ String.format("%02d", toInt(p.bNo))
				+ String.format("%02d",
						toInt(getOne("select count(*) from purchase where f_no = ? and u_no = ? and p_date = ?", p.fNo,
								user.get(0), LocalDate.now())));

		var cap = "품명,판매자 명,판매처,발급날짜,수량,단가,총금액".split(",");
		var info = new String[] { p.txt[0].getText(), p.txt[1].getText(), getOne(
				"select concat(c_name, ',', t_name) from user u, city c, town t where u.t_no = t.t_no and c.c_no = t.c_no and u.u_no = ?",
				user.get(0)), LocalDate.now().toString(), p.txt[4].getText() + "개",
				format(toInt(p.txt[2].getText())) + "원", format(toInt(p.txt[5].getText())) + "원" };

		for (int i = 0; i < info.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(hylbl(cap[i], 2, 15), "West");
			tmp.add(hylbl(info[i], 2, 15), "East");

			cc.add(tmp);
		}

		for (int i = 0; i < code.length(); i++) {
			int num = (code.charAt(i) - '0') + 1;
			var l = new JLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.fillRect(0, 0, 4 * num, 50);
				}
			};

			cs.add(l);
		}

		s.add(lbl(code, 0, 15), "North");
		s.add(sc = new JPanel());

		sc.add(btn("확인", a -> {
			new Main();
			setVisible(false);
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
