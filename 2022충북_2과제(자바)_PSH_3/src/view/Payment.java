package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Payment extends BaseFrame {
	public Payment(ArrayList<Object> rs, int cnt) {
		super("결제", 300, 500);

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new GridLayout(0, 1)), "South");

		c.add(new JLabel(getIcon("./datafile/스토어/" + rs.get(1) + ".jpg", 200, 200)));
		c.add(cs = new JPanel(), "South");

		cs.add(lbl("<html>" + rs.get(1) + "<br>수량 " + cnt + "개", 0, 20));

		var cap = "상품단가,결제금액".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(lbl("<html><font color='white'>" + cap[i] + "원", 2), "West");
			tmp.add(lbl("<html><font color='white'>총 "
					+ (i == 0 ? rs.get(3).toString() : format(toInt(rs.get(3)) * cnt)) + "원", 4), "East");

			if (i == 0) {
				tmp.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));
			}

			s.add(tmp);
		}
		s.add(btn("결제하기", a -> {
			int ans = JOptionPane.showConfirmDialog(null, "결제 하시겠습니까?", "결제", JOptionPane.YES_NO_OPTION);
			
			if(ans == JOptionPane.YES_OPTION) {
				execute("insert orderlist values(0, ?,?,?,?)", user.get(0), rs.get(0), cnt, LocalDate.now().toString());
			}
			
			dispose();
		}));

		setVisible(true);

		s.setBackground(Color.black);
		s.setOpaque(true);

	}

	public static void main(String[] args) {
		new Store();
	}
}
