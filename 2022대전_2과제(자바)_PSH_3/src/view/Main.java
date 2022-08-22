package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	int idx = 0;
	SwingWorker swingWorker;
	JLabel lbl[] = new JLabel[6];

	public Main() {
		super("메인", 900, 600);
		setDefaultCloseOperation(3);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new GridLayout(0, 3)), "South");

		n.add(lbl("<html><font color='green'>농수산물판매관리", 0, 25), "West");
		n.add(lbl("로그인을 먼저 해주세요.", 0, 15), "East");

		var cap = "로그인,회원가입,거래내역,농산물관리,농산물검색,시도별분석".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();

			tmp.add(lbl[i] = event(new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 100, 100)), e -> {
				var me = (JLabel) e.getSource();
				if (!me.isEnabled())
					return;

				switch (me.getName()) {
				case "로그인":
					new Login().addWindowListener(new Before(this));
					break;
				case "로그아웃":
					logout();
					break;
				case "회원가입":
					new Sign().addWindowListener(new Before(this));
					break;
				case "거래내역":
					new TradeHistory().addWindowListener(new Before(this));
					break;
				case "농산물관리":
					new BaseManage().addWindowListener(new Before(this));
					break;
				case "농산물검색":
					new BaseSearch().addWindowListener(new Before(this));
					break;
				case "시도별분석":
					new Map().addWindowListener(new Before(this));
					break;
				}
			}));
			tmp.add(lbl(cap[i], 0));

			lbl[i].setName(cap[i]);
			lbl[i].setEnabled(i < 2);

			tmp.setBorder(new LineBorder(Color.lightGray));

			s.add(tmp);
		}

		setVisible(true);
		setCenter();
	}

	private void setCenter() {
		if (swingWorker != null) {
			swingWorker.cancel(true);
		}
		idx = 0;
		c.removeAll();

		var rs = getRows(
				"select b.b_name, b.b_note, b.b_img from sale s, farm f, base b where s.f_no = f.f_no and f.b_no = b.b_no "
						+ (user == null ? "" : "and b.division=" + user.get(5))
						+ " group by b.b_no order by sum(s.s_quantity) desc, sum(s.s_quantity * f.f_amount) desc limit 5");

		for (var r : rs) {
			int i = rs.indexOf(r);
			var l = new JLabel(getIcon(r.get(2), 900, 300));

			l.add(lbl("<html><font color='white'>" + r.get(0) + "[" + (i + 1) + "위]", 2, 30)).setBounds(50, 200, 200,
					50);
			l.add(lbl("<html><font color='white'>" + r.get(1), 2, 10)).setBounds(50, 210, 800, 100);

			c.add(l).setBounds(i * 900, 0, 900, 300);
		}

		c.repaint();
		c.revalidate();

		animation();
	}

	private void logout() {
		user = null;

		setCenter();
	}

	void login() {
		var l = (JLabel) n.getComponent(1);

		l.setText(user.get(1) + "님 환영합니다.");

		lbl[0].setIcon(getIcon("로그아웃.jpg", 100, 100));
		
		for (int i = 0; i < lbl.length; i++) {
			lbl[i].setEnabled(i != 1);
		}
		
		setCenter();
	}

	private void animation() {
		swingWorker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				Thread.sleep(3000);
				while (true) {
					for (var com : c.getComponents()) {
						int x = com.getX();
						com.setLocation(x - 10, 0);
					}

					Thread.sleep(20);

					if (c.getComponent(idx).getX() == -900) {
						c.getComponent(idx).setLocation(3600, 0);
						idx = idx == 4 ? 0 : idx + 1;
						Thread.sleep(3000);
					}
				}
			}
		};
		swingWorker.execute();
	}

	public static void main(String[] args) {
		new Main();
	}
}
