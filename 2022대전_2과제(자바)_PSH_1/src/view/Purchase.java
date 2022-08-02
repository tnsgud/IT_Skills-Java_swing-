package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Purchase extends BaseFrame {
	JLabel img;
	JTextField txt[] = new JTextField[6];
	JTextArea area = new JTextArea();
	int fNo, bNo;

	public Purchase(int fNo) {
		super("상품구매", 700, 500);
		this.fNo = fNo;

		add(c = new JPanel(new GridLayout(1, 0)));
		add(s = new JPanel(new BorderLayout()), "South");

		bNo = toInt(getOne("select b_no from farm where f_no = ?", fNo));
		c.add(img = new JLabel(getIcon(getRows("select b_img from base where b_no = ?", bNo).get(0).get(0), 350, 350)));
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var cap = "제품명,판매자,단가,재고,수량,합계".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(lbl(cap[i], 2, 15));
			cc.add(txt[i] = new JTextField());

			txt[i].setEnabled(i == 4);
		}

		s.add(sw = new JPanel(new BorderLayout()), "West");
		s.add(se = new JPanel(new FlowLayout(2)), "East");

		sw.add(lbl("상풒설명", 2, 15), "North");
		sw.add(sz(area, 230, 80));

		se.add(btn("구매하기", a -> {
			if (txt[4].getText().isEmpty()) {
				eMsg("수량을 입력하세요.");
				return;
			}

			iMsg(user.get(1) + "  " + txt[4].getText() + "개를 구매하였습니다.");

			execute("insert purchase values(0, ?, ?, ?, ?)", user.get(0), fNo, LocalDate.now(), txt[4].getText());
			execute("insert sale values(0, ?, ?, ?, ?)", user.get(0), fNo, LocalDate.now(), txt[4].getText());
			execute("update farm set f_quantity = f_quantity - ?", txt[4].getText());

			new Receipt().addWindowListener(new Before(this));
		}));

		var rs = getRows(
				"select b_name, u_name, f_amount, f_quantity from base b, farm f, user u where f.u_no = u.u_no and f.b_no = b.b_no and f.f_no = ?",
				fNo).get(0);
		for (int i = 0; i < rs.size(); i++) {
			txt[i].setText(rs.get(i).toString());
		}

		txt[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int cnt = toInt(txt[4].getText());

				if (toInt(txt[3].getText()) < cnt) {
					eMsg("재고가 부족합니다.");
					txt[4].setText("");
					return;
				}

				if (cnt < 1) {
					eMsg("1이상의 숫자로 입력하세요.");
					txt[4].setText("");
					return;
				}

				txt[5].setText(cnt * toInt(txt[2].getText()) + "");
			}
		});

		img.setBorder(new LineBorder(Color.black));
		area.setBorder(new LineBorder(Color.black));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
