package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import db.DB;
import tool.Tool;
import view.Stage.Item;

public class Purchase extends BaseFrame implements Tool {
	ArrayList<Item> item;
	String str;
	ArrayList<String> t_seat = new ArrayList<String>(), t_discount = new ArrayList<String>();
	JButton btn;

	public Purchase(ArrayList<Item> item) {
		super("결제", 400, 500);
		this.item = item;

		ui();

		setVisible(true);
		pack();
		setSize(400, getHeight());
	}

	private void ui() {
		var s_n = new JPanel(new FlowLayout(0));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lbl("결제", 0, 25));

		{
			var p = new JPanel(new BorderLayout());
			p.add(lbl("<html>공연명<br/><br/>장소<br/><br/>날짜<br/><br/></html>", 2, 15), "West");
			var str = "<html><div align=right>";
			var rs = DB.rs("select p_name, p_place, p_date from perform where p_no=?", pno);
			try {
				if (rs.next()) {
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						str += rs.getString(i + 1) + "<br/><br/>";
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += "</div></html>";
			p.add(lbl(str, 0, 15), "East");
			p.setBorder(new MatteBorder(0, 0, 3, 0, Color.LIGHT_GRAY));
			c.add(p);
		}

		{
			var p = new JPanel(new BorderLayout());
			p.add(lbl("좌석", 2, 15), "West");
			str = "<html><div align=right>";
			item.forEach(e -> {
				str += e.t_seat + ":" + format.format(e.price) + "<br/><br/>";
			});
			str += "</div></html>";
			p.add(lbl(str, 2, 15), "East");
			p.setBorder(new MatteBorder(0, 0, 3, 0, Color.LIGHT_GRAY));
			c.add(p);
		}

		{
			var p = new JPanel(new BorderLayout());
			p.add(lbl("총금액", 2, 15), "West");
			p.add(lbl(format.format(totPrice), 4, 15), "East");
			c.add(p);
		}

		s.add(s_n);
		s_n.add(btn = btn("본인 인증", a -> {
			var an = JOptionPane.showInputDialog(null, "비밀번호를 입력해주세요.");
			if (DB.getOne("select u_pw from user where u_no=?", uno).equals(an + "\r")) {
				btn.setEnabled(false);
				var l = lbl("√", 0, 15);
				l.setForeground(Color.green);
				s_n.add(l);

				repaint();
				revalidate();
			}
		}));

		{
			var p = new JPanel(new GridLayout(1, 0, 5, 5));
			for (var cap : "결제하기,취소".split(",")) {
				p.add(btn(cap, a -> {
					if (cap.equals("취소")) {
						dispose();
					} else {
						if(btn.isEnabled()) {
							eMsg("본인인증을 해주세요.");
							return;
						}
						
						item.forEach(e->{
							t_seat.add(e.t_seat);
							t_discount.add(e.selDc+"");
						});
						DB.execute("insert into ticket values(0, ?, ?, ?, ?)", uno, pno, String.join(",", t_seat), String.join(",", t_discount));
						new Main().addWindowListener(new Before(Purchase.this));
					}
				}));
			}
		}

		n.setBorder(new MatteBorder(0, 0, 5, 0, Color.black));
	}

	public static void main(String[] args) {
		pno = 1;
		uno = 1;
		new Stage();
	}
}
