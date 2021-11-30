package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ReservePage extends BasePage {
	JComboBox<String> com;
	JTextField txt;

	JTextField[] txt1 = new JTextField[3];
	JTextField[] txt2 = new JTextField[2];
	JComboBox<String> type;

	String cap[] = "이름,주민등록 번호,휴대전화 번호,백신 종류,의료기간".split(",");

	public ReservePage() {
		super();

		ui();
	}

	void ui() {
		setLayout(new GridBagLayout());

		this.add(n = new JPanel(new GridLayout(0, 1)));

		n.add(nn = new JPanel());
		n.add(nc = new JPanel(new BorderLayout()));

		nc.add(nw = new JPanel(new GridLayout(0, 1, 10, 10)), "West");
		nc.add(ne = new JPanel(new GridLayout()), "East");

		nn.add(lbl("인증할 수단을 선택해 주세요.", 0, 15));

		nw.add(com = new JComboBox<String>());
		nw.add(txt = new JTextField(15));

		ne.add(btn("확인", event -> {
			if (txt.getText().isEmpty()) {
				eMsg("빈칸");
				return;
			}
			
			try {
				String sql = (com.getSelectedIndex() == 0) ? "select * from user where "
						: "select * from user where resident = '" + txt.getText() + "'";
				var rs = stmt.executeQuery(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			n.removeAll();
			reserve();
			repaint();
			revalidate();
		}));

		com.addItem("전화번호");
		com.addItem("주민번호");

		nw.setBorder(new EmptyBorder(0, 0, 0, 10));
	}

	void reserve() {
		add(c = new JPanel(new GridLayout(0, 1, 0, 0)));
		for (int i = 0; i < cap.length; i++) {
			var tmp_m = new JPanel(new BorderLayout());
			var tmp_n = new JPanel(new FlowLayout(FlowLayout.LEFT));
			var tmp_c = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp_m.add(tmp_n, "North");
			tmp_m.add(tmp_c);

			tmp_n.add(BasePage.lbl(cap[i], JLabel.LEFT, 15));
			if (i < 3) {
				tmp_c.add(txt1[i] = new JTextField(15));
				if (i == 1)
					tmp_c.add(btn("확인", a -> {
					}));
			} else if (i == 3) {
				tmp_c.add(type = new JComboBox<String>());
			} else if (i == 4) {
				tmp_n.add(BasePage.lbl("날짜", JLabel.LEFT, 15));
				for (int j = 0; j < txt2.length; j++) {
					tmp_c.add(txt2[j] = new JTextField(7));
				}
			}
			c.add(tmp_m);
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new ReservePage());
		mf.setVisible(true);
	}
}
