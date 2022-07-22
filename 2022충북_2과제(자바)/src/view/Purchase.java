package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Purchase extends BaseFrame {
	JLabel lblPrev, lblNext, lblCnt, lblPrice;

	public Purchase() {
		super("구매", 700, 350);

		setLayout(new BorderLayout(10, 0));

		var data = getRows("select * from store where s_no = ?", s_no).get(0);

		add(new JLabel(getIcon("./datafile/스토어/" + data.get(1) + ".jpg", 300, 300)), "West");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

		c.add(lbl(data.get(1).toString(), 2, 15));
		c.add(lbl(format(toInt(data.get(3))) + "원", 2, 15));
		c.add(cn = new JPanel(new BorderLayout(50, 0)));
		c.add(cc = new JPanel(new BorderLayout()));
		c.add(cs = new JPanel(new FlowLayout(2)));
		c.add(s = new JPanel(new GridLayout(1, 0, 5, 0)));

		cn.add(lbl("설명", 2, 12), "West");
		cn.add(lbl("<html><font color='gray'>" + data.get(2), 2));

		cc.add(lblPrev = lbl("-", 0, 1, 15, e -> {
			if (toInt(lblCnt.getText()) == 1) {
				eMsg("최소 1개 이상은 구매해야 합니다.");
				return;
			}

			lblCnt.setText((toInt(lblCnt.getText()) - 1) + "");

			lblPrice.setText(
					"<html><font color=rgb(255, 0, 55)>" + format(toInt(data.get(3)) * toInt(lblCnt.getText())) + "원");
		}), "West");
		cc.add(lblCnt = lbl("1", 0));
		cc.add(lblNext = lbl("+", 0, 1, 15, e -> {
			if (toInt(lblCnt.getText()) == 5) {
				eMsg("최대 5개 이상은 구매하실 수 있습니다.");
				return;
			}

			lblCnt.setText((toInt(lblCnt.getText()) + 1) + "");

			lblPrice.setText(
					"<html><font color=rgb(255, 0, 55)>" + format(toInt(data.get(3)) * toInt(lblCnt.getText())) + "원");
		}), "East");

		cs.add(lbl("총 상품금액", 0, 12));
		cs.add(lblPrice = lbl("<html><font color=rgb(255, 0, 55)>" + format(toInt(data.get(3))) + "원", 0, 15));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		cc.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		for (int i = 0; i < 3; i++) {
			((JComponent) c.getComponent(i)).setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
		}

		var cap = "취소,구매하기".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(i == 0 ? btnBlack(cap[i], a -> dispose()) : btn(cap[i], a -> {
				if (!isLogin) {
					eMsg("로그인 후 이용 가능합니다.");
					return;
				}

				new Payment(toInt(lblCnt.getText()), toInt(lblPrice.getText())).addWindowListener(new Before(this));
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		s_no = 1;
		new Purchase();
	}
}
