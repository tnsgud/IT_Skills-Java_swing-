package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Cinema extends BaseFrame {
	int a_no = 1, tIdx = 0;
	String tName = "강남";
	JLabel tNameLbl, img;

	public Cinema() {
		super("영화관", 900, 500);

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(nn = new JPanel(new FlowLayout(1)), "North");
		n.add(nc = sz(new JPanel(new FlowLayout(1)), 900, 90));

		var rs = getRows("select a_name from area");

		for (var r : rs) {
			var l = lbl(r.get(0).toString(), 0, 12);

			l.setForeground(Color.white);
			l.setFont(new Font("맑은 고딕", 0, 12));

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					for (var lbl : nn.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					var me = (JLabel) e.getSource();

					me.setFont(new Font("맑은 고딕", 1, 12));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					for (var lbl : nn.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					nn.getComponent(a_no - 1).setFont(new Font("맑은 고딕", 1, 12));
				}

				@Override
				public void mousePressed(MouseEvent e) {
					for (var lbl : nn.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					var me = (JLabel) e.getSource();

					me.setFont(new Font("맑은 고딕", 1, 12));
					a_no = toInt(getOne("select a_no from area where a_name = ?", me.getText()));

					setTheater();
				}
			});

			nn.add(l);
		}

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(img = new JLabel());

		cn.add(tNameLbl = lbl("GGV강남", 2, 15), "West");
		cn.add(ce = new JPanel(new FlowLayout(2)), "East");

		for (var cap : "상영시간표,예매하기".split(",")) {
			ce.add(cap.contains("상영") ? btnBlack("상영시간표", a -> new Schedule().addWindowListener(new Before(this)))
					: btn(cap, a -> new Reserve().addWindowListener(new Before(this))));
		}

		setTheater();

		((JLabel) nn.getComponent(0)).setFont(new Font("맑은 고딕", 1, 12));
		((JLabel) nc.getComponent(0)).setFont(new Font("맑은 고딕", 1, 12));

		setCinema();

		nn.setBackground(Color.black);

		setVisible(true);
	}

	void setTheater() {
		nc.removeAll();

		var rs = getRows("select * from theater where a_no = ?", a_no);
		for (var r : rs) {
			var l = lbl(r.get(2).toString(), 0);

			l.setName(rs.indexOf(r) + "");

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					for (var lbl : nc.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					var me = (JLabel) e.getSource();

					me.setFont(new Font("맑은 고딕", 1, 12));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					for (var lbl : nc.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					nc.getComponent(tIdx).setFont(new Font("맑은 고딕", 1, 12));
				}

				@Override
				public void mousePressed(MouseEvent e) {
					for (var lbl : nc.getComponents()) {
						((JLabel) lbl).setFont(new Font("맑은 고딕", 0, 12));
					}

					var me = (JLabel) e.getSource();

					me.setFont(new Font("맑은 고딕", 1, 12));
					tName = me.getText();
					t_no = toInt(getOne("select t_no from theater where t_name = ?", me.getText()));
					tIdx = toInt(((JLabel) e.getSource()).getName());

					setCinema();
				}
			});

			if (rs.indexOf(r) != 0 && rs.indexOf(r) < rs.size()) {
				l.setBorder(new CompoundBorder(new MatteBorder(0, 1, 0, 0, Color.black), new EmptyBorder(0, 5, 0, 0)));
			}

			nc.add(l);
		}

		tName = rs.get(0).get(2).toString();

		setCinema();

		nc.repaint();
		nc.revalidate();
	}

	void setCinema() {
		tNameLbl.setText("GGV" + tName);
		img.setIcon(getIcon("./datafile/지점/" + tName + ".jpg", 900, 300));

		System.out.println("sddsfsdafasdf");
	}

	public static void main(String[] args) {
		new Cinema();
	}
}
