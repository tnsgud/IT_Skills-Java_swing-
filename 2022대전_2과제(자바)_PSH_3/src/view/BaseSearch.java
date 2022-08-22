package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.stream.Collectors;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class BaseSearch extends BaseFrame {
	JTextField txt = new JTextField(15);
	DefaultListModel<String> m = new DefaultListModel<>();
	JList<String> list = new JList<>(m);
	Map<String, ImageIcon> map = getRows("select b_no, b_img from base").stream()
			.collect(Collectors.toMap(a -> a.get(0).toString(), a -> getIcon(a.get(1), 150, 150)));

	public BaseSearch() {
		super("농산물 검색", 910, 500);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(lbl("농산물 검색", 0, 30));
		n.add(ns = new JPanel(new FlowLayout(0)), "South");

		ns.add(lbl(toInt(user.get(5)) == 1 ? "과일" : "야채", 0, 15));
		ns.add(txt);
		ns.add(btn("검색", a -> search()));

		c.add(cw = sz(new JPanel(new BorderLayout()), 250, 0), "West");
		c.add(new JScrollPane(cc = new JPanel()));
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cw.add(lbl("관련 검색어", 0, 15), "North");
		cw.add(event(list, e -> {
			txt.setText(list.getSelectedValue());
			search();
		}));

		search();

		setVisible(true);
	}

	private void search() {
		cc.removeAll();
		m.clear();

		if (!txt.getText().isEmpty()) {
			for (var rs : getRows("select b_name from base where division <> ? and b_name like ?", user.get(5),
					"%" + txt.getText().replace(" ", "") + "%")) {
				m.addElement(rs.get(0).toString());
			}
		}

		var rs = getRows(
				"select f_no, concat(u_name, '(', c_name, ', ', t_name, ')', format(f_amount, '#,##0'), '원'), b.b_no  from farm f, user u, town t, city c, base b where f.b_no = b.b_no and f.u_no = u.u_no and u.t_no = t.t_no and t.c_no = c.c_no and b.division <> ? and b_name like ? order by f.u_no, b.b_no",
				user.get(5), "%" + txt.getText() + "%");
		var tmp = new JPanel(new FlowLayout(0, 0, 0));
		for (var r : rs) {
			int i = rs.indexOf(r);
			var temp = new JPanel(new BorderLayout());
			var img = event(new JLabel(map.get(r.get(2).toString())),
					e -> new Purchase(toInt(r.get(2))).addWindowListener(new Before(this)));

			temp.add(img);
			temp.add(lbl(r.get(1).toString(), 0), "South");

			temp.setBorder(new LineBorder(Color.black));

			tmp.add(sz(temp, 200, 170));

			if (i % 3 < 2) {
				tmp.add(Box.createHorizontalStrut(5));
			}

			if (i % 3 == 2) {
				cc.add(tmp);
				cc.add(Box.createVerticalStrut(5));
				tmp = new JPanel(new FlowLayout(0, 0, 0));
			}
		}

		cc.repaint();
		cc.revalidate();
	}
}
