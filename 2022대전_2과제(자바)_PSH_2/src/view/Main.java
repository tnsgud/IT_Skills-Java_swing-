package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	JLabel lblLogin, icon[] = new JLabel[6], lbl[] = new JLabel[6];
	SwingWorker worker;

	public Main() {
		super("메인", 800, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = sz(new JPanel(null), 0, 300));
		add(s = new JPanel(new GridLayout(0, 3)), "South");

		n.add(hylbl("<html><font color='green'>농수산물판매관리", 2, 1, 35), "West");
		n.add(lblLogin = hylbl("로그인을 먼저 해주세요.", 4, 0, 12), "East");

		var cap = "로그인,회원가입,거래내역,농산물관리,농산물검색,시도별분석".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();

			tmp.add(icon[i] = new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 80, 80)));
			tmp.add(lbl[i] = lbl(cap[i], 0));

			icon[i].setName(cap[i]);
			evt(icon[i], e -> {
				var me = (JLabel) e.getSource();

				if (!me.isEnabled())
					return;

				switch (me.getName()) {
				case "로그인":
					new Login().addWindowListener(new Before(this));
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
					new Search().addWindowListener(new Before(this));
					break;
				case "시도별분석":
					new Map();
					break;
				case "로그아웃":
					iMsg("로그아웃 되었습니다.");
					logout();
					break;
				}
			});

			tmp.setBorder(new LineBorder(Color.LIGHT_GRAY));

			s.add(tmp);
		}

		if (user == null) {
			logout();
		} else {
			login();
		}

		setVisible(true);
	}

	private void animation() {
		c.removeAll();

		if (worker != null) {
			worker.cancel(true);
		}

		var div = user == null ? "" : "and u.division = " + user.get(5);
		var sql = "select b.b_no, b.b_name, b.b_note, b.b_img, sum(s.s_quantity) cnt, sum(f.f_amount * s.s_quantity) price from base b, farm f, sale s, user u where s.f_no = f.f_no and f.b_no = b.b_no and f.u_no = u.u_no "
				+ div + " group by b.b_no order by cnt desc, price desc, b.b_no limit 5";
		var rs = getRows(sql);

		for (var r : rs) {
			var img = new JLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					var g2 = (Graphics2D) g;
					var paint = new GradientPaint(0, 0, new Color(0, 255, 255, 50), getWidth(), getHeight(),
							new Color(0, 0, 255, 50));
					var image = new ImageIcon((byte[]) r.get(3)).getImage();

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
					g2.setPaint(paint);
					g2.fillRect(0, 0, getWidth(), getHeight());

					g2.setColor(Color.white);
					g2.setFont(new Font("맑은 고딕", 0, 25));
					g2.drawString(r.get(1) + "[" + (rs.indexOf(r) + 1) + "위]", 30, getHeight() - 60);

					g2.setFont(new Font("맑은 고딕", 0, 10));
					g2.drawString(r.get(2).toString(), 30, getHeight() - 30);
				}
			};

			c.add(img).setBounds((c.getComponentCount() - 1) * 800, 0, 800, 320);
		}

		worker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				Thread.sleep(3000);

				while (true) {
					for (var com : c.getComponents()) {
						com.setLocation(com.getX() - 5, com.getY());

						if (com.getX() < -800) {
							com.setLocation(3200, com.getY());
							Thread.sleep(3000);
						}

						Thread.sleep(1);
					}
				}
			}
		};
		worker.execute();

		c.repaint();
		c.revalidate();
	}

	void login() {
		lblLogin.setText(user.get(1) + "님 환영합니다.");

		for (int i = 0; i < 6; i++) {
			icon[i].setEnabled(i != 1);
		}

		lbl[0].setText("로그아웃");
		icon[0].setName("로그아웃");

		animation();
	}

	void logout() {
		lblLogin.setText("로그인을 먼저 해주세요.");

		for (int i = 0; i < icon.length; i++) {
			icon[i].setEnabled(i < 2);
		}

		lbl[0].setText("로그인");
		icon[0].setName("로그인");

		animation();
	}

	public static void main(String[] args) {
		new Main();
	}
}
