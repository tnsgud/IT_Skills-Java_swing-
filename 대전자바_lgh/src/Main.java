import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Main extends BaseFrame {

	JPanel c;

	CardLayout card;
	South south[] = new South[3];
	Timer timer;

	static JLabel img;
	static JLabel lbl[] = new JLabel[5];

	String type[] = "M,O,C".split(",");
	String title[] = "������,�����,�ܼ�Ʈ".split(",");
	String str[] = "TICKETING,MONTH SCHEDULE,CHART,LOGIN,MYPAGE".split(",");
	int cardIdx, idx;

	public Main() {
		super("����", 750, 400);
		this.setDefaultCloseOperation(3);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(card = new CardLayout()));

		var n_w = new JPanel(new FlowLayout(0, 20, 0));
		var n_e = new JPanel(new FlowLayout(2, 20, 0));

		n.add(n_w, "West");
		n.add(n_e, "East");

		for (int i = 0; i < 3; i++) {
			n_w.add(lbl[i] = lbl(str[i], 0, 20));
		}
		n_e.add(img = new JLabel());
		for (int i = 3; i < str.length; i++) {
			n_e.add(lbl[i] = lbl(str[i], 0, 20));
		}

		for (int i = 0; i < south.length; i++) {
			c.add(south[i] = new South(title[i], type[i]), i + "");
			idx++;
		}

		for (int i = 0; i < lbl.length; i++) {
			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var lbl = (JLabel) e.getSource();
					if (lbl.getText().equals(str[0])) {
						new Search().addWindowListener(new Before(Main.this));
					} else if (lbl.getText().equals(str[1])) {
						new Month().addWindowListener(new Before(Main.this));
					} else if (lbl.getText().equals(str[2])) {
						new Chart().addWindowListener(new Before(Main.this));
					} else if (lbl.getText().equals("LOGIN")) {
						new Login().addWindowListener(new Before(Main.this));
					} else if (lbl.getText().equals("LOGOUT")) {
						eMsg("�α׾ƿ��Ǿ����ϴ�.");
						uno = "";
						img.setVisible(false);
						Main.lbl[3].setText("LOGIN");
					} else {
						if (uno == "") {
							eMsg("�α����� �ϼ���.");
							return;
						}

						new MyPage().addWindowListener(new Before(Main.this));
					}
				}
			});
		}

		timer = new Timer(1000, e -> {
			cardIdx = cardIdx == 3 ? 0 : cardIdx;
			card.show(c, cardIdx + "");
			cardIdx++;
		});
		timer.setDelay(1000);
		timer.start();

		c.setBorder(new EmptyBorder(40, 10, 10, 10));
		sz(img, 30, 30);
		img.setBorder(new LineBorder(Color.BLACK));
		img.setVisible(false);
		this.setVisible(true);
	}

	class South extends JPanel {
		JPanel c, s;

		public South(String title, String type) {
			super(new BorderLayout());
			this.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "�α����(" + title + ")", TitledBorder.LEFT,
					TitledBorder.TOP, new Font("���� ���", Font.BOLD, 25)));

			this.add(c = new JPanel(new GridLayout(1, 0, 5, 5)));
			this.add(s = new JPanel(new FlowLayout(1, 5, 5)), "South");

			try {
				var rs = stmt.executeQuery(
						"SELECT *, count(*) FROM 2021����.perform p, ticket t where t.p_no = p.p_no and p.pf_no like '%"
								+ type + "%' group by p.p_name order by count(*) desc limit 5;");
				int rank = 1;
				while (rs.next()) {
					var tmp = new JPanel(new BorderLayout());
					var img = new JLabel(icon(rs.getString(2), 150, 150));

					tmp.add(new JLabel(rank + "��"), "North");
					tmp.add(img);
					tmp.add(new JLabel(rs.getString(3), 0), "South");

					img.setBorder(new LineBorder(Color.BLACK));

					rank++;
					c.add(tmp);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			var pages = "1,2,3".split(",");

			for (int i = 0; i < pages.length; i++) {
				var lbl = new JLabel(pages[i], 0);

				if (idx == i) {
					lbl.setForeground(Color.RED);
				}

				s.add(lbl);
			}

			c.setBorder(new EmptyBorder(15, 10, 10, 10));
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
