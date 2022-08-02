package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

import view.BaseFrame.Before;

public class Search extends BaseFrame {
	JTextField txt;
	DefaultListModel<String> m = new DefaultListModel<>();
	JList<String> list = new JList<>(m);
	Map<Object, Object> imgMap;

	public Search() {
		super("농산물 검색", 800, 450);
		user = getRows("select * from user where u_no = 1").get(0);

		imgMap = getRows("select b_no, b_img from base").stream()
				.collect(Collectors.toMap(a -> a.get(0), a -> getIcon(a.get(1), 200, 100)));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(hylbl("농산물 검색", 0, 30));
		n.add(ns = new JPanel(new FlowLayout(0)), "South");

		ns.add(lbl("과일", 0));
		ns.add(txt = new JTextField(10));
		ns.add(btn("검색", a -> search()));

		c.add(cw = sz(new JPanel(new BorderLayout()), 150, 0), "West");
		c.add(new JScrollPane(cc = new JPanel()));
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cw.add(hylbl("관련 검색어", 0, 15), "North");
		cw.add(list);

		search();

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				txt.setText(list.getSelectedValue());
				search();
			}
		});

		setVisible(true);
	}

	private void search() {
		cc.removeAll();
		m.clear();

		var rs = getRows("select b_name from base where division <> ? and b_name like ?", user.get(5),
				"%" + txt.getText().replace(" ", "") + "%");
		for (var r : rs) {
			if (!txt.getText().isEmpty()) {
				m.addElement(r.get(0).toString());
			}
		}

		rs = getRows(
				"select b.b_no, concat(u_name,'(', c_name, ',', t_name, ')', format(f_amount, '#,##0'), '원'), f_no from base b, farm f, user u, city c, town t where f.b_no = b.b_no and f.u_no = u.u_no and u.t_no = t.t_no and c.c_no = t.c_no and b.division <> ? and b_name like ? order by u.u_no, b.b_no",
				user.get(5), "%" + txt.getText().replace(" ", "") + "%");

		var tmp = new JPanel(new FlowLayout(0, 0, 0));
		for (var r : rs) {
			int i = rs.indexOf(r);
			var temp = new JPanel(new BorderLayout());
			var img = new JLabel((ImageIcon) imgMap.get(r.get(0)));

			temp.add(img);
			temp.add(lbl(r.get(1).toString(), 0), "South");

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new Purchase(toInt(r.get(2))).addWindowListener(new Before(Search.this));
				}
			});

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

	public static void main(String[] args) {
		new Main();
	}
}
