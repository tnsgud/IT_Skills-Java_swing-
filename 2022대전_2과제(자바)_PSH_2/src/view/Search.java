package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.stream.Collectors;

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

public class Search extends BaseFrame {
	JTextField txt;
	DefaultListModel<String> m = new DefaultListModel<>();
	JList<String> list = new JList<>();
	Map<Object, ImageIcon> map;

	public Search() {
		super("농산물 검색", 800, 450);
		map = getRows("select b_no, b_img from base").stream()
				.collect(Collectors.toMap(a -> a.get(0), a -> getIcon(a.get(1), 200, 100)));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(hylbl("농산물 검색", 0, 1, 20), "North");
		n.add(nc = new JPanel(new FlowLayout(0)));

		nc.add(lbl(",과일,야채".split(",")[toInt(user.get(5))], 2, 15));
		nc.add(txt = new JTextField(10));
		nc.add(btn("검색", a -> search()));

		c.add(cw = sz(new JPanel(new BorderLayout()), 150, 0), "West");
		c.add(new JScrollPane(cc = new JPanel()));
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cw.add(lbl("관련 검색어", 0, 1, 15), "North");
		cw.add(list);

		search();

		evt(list, e -> {
			txt.setText(list.getSelectedValue());
			search();
		});

		setVisible(true);
	}

	private void search() {
		cc.removeAll();
		m.clear();

		var rs = getRows("select b_name from base where division <> ? and b_name like ?", user.get(5),
				"%" + txt.getText() + "%");
		for (var r : rs) {
			if (!txt.getText().isEmpty()) {
				m.addElement(r.get(0).toString());
			}
		}

		rs = getRows(
				"select b.b_no, concat(u_name, '(', c_name, ',', t_name, ')', format(f_amount, '#,##0'), '원'), f_no from user u, farm f, base b, city c, town t where u.u_no = f.u_no and f.b_no = b.b_no and u.t_no = t.t_no and t.c_no = c.c_no and b.division <> ? and b_name like ? order by u.u_no, b.b_no",
				user.get(5), "%" + txt.getText() + "%");

		var tmp = new JPanel(new FlowLayout(0, 0, 0));
		for (var r : rs) {
			int i = rs.indexOf(r);
			var temp = new JPanel(new BorderLayout());
			var img = new JLabel(map.get(r.get(0)));

			temp.add(img);
			temp.add(lbl(r.get(1).toString(), 0), "South");

			evt(img, e -> new Purchase(toInt(r.get(2))).addWindowListener(new Before(this)));

			temp.setBorder(new LineBorder(Color.black));

			tmp.add(sz(temp, 200, 120));

			if (i % 3 < 2) {
				tmp.add(Box.createHorizontalStrut(5));
			}

			if (i % 3 == 0) {
				cc.add(tmp);
			} else if (i % 3 == 2) {
				cc.add(Box.createVerticalStrut(5));
				tmp = new JPanel(new FlowLayout(0, 0, 0));
			}
		}

		cc.repaint();
		cc.revalidate();
	}
}
