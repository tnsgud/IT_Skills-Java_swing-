package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import view.Baggage.Item;

public class Baggage extends BaseFrame {
	ArrayList<Item> items = new ArrayList<>();
	JLabel totPrice = lbl("총 0원", 4, 20);
	JButton btn[] = new JButton[3];

	public Baggage() {
		super("수화물 구매", 400, 500);

		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		n.add(lbl("수하물 구매", 0, 25));
		n.add(lbl("<html>사이즈 초과 : 160cm 이상<br>무게 초과 : 25kg 이상", 2));

		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

		c.add(cn = new JPanel(new GridLayout(0, 4)));

		var cap = "+,사이즈 초과,무게 초과,요금".split(",");
		for (int i = 0; i < cap.length; i++) {
			cn.add(lbl(cap[i], 0, 15));
		}

		((JLabel) cn.getComponent(0)).addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				items.add(new Item());
				addItem();
			}
		});

		cap = "삭제,초기화,확인".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i] = btn(cap[i], a -> {
				if (a.getActionCommand().equals("삭제")) {
					new Delete().setVisible(true);
				} else if (a.getActionCommand().equals("초기화")) {
					items.clear();
					items.add(new Item());
					addItem();
				} else {
					iMsg("수하물 선택이 완료되었습니다.");
					dispose();
				}
			});
		}

		items.add(new Item());

		addItem();

		cn.setBorder(new MatteBorder(2, 0, 2, 0, Color.black));

		setVisible(true);
	}

	void addItem() {
		c.removeAll();

		c.add(cn);

		for (var i : items) {
			c.add(i);
		}

		c.add(totPrice);

		for (var com : c.getComponents()) {
			((JComponent) com).setMaximumSize(new Dimension(400, 40));
		}

		var tot = items.stream().mapToInt(it -> toInt(it.pricelbl.getText())).sum();
		totPrice.setText("총 " + format(tot) + "원");

		s.removeAll();
		s.setLayout(items.size() > 1 ? new GridLayout(0, 3) : new FlowLayout(1));

		if (items.size() > 1) {
			for (int i = 0; i < btn.length; i++) {
				s.add(btn[i]);
			}
		} else {
			s.add(btn[2]);
		}

		repaint();
		revalidate();
	}

	class Item extends JPanel {
		JCheckBox chk[] = new JCheckBox[2];
		JLabel namelbl, pricelbl = lbl(format(items.size() == 0 ? 0 : 50000) + "원", 0, 15);

		public Item() {
			super(new GridLayout(1, 0));

			setBorder(new MatteBorder(0, 0, 2, 0, Color.black));

			add(namelbl = lbl(
					"bag" + (items.size() == 0 ? 1 : toInt(items.get(items.size() - 1).namelbl.getText()) + 1), 0, 15));
			for (int i = 0; i < chk.length; i++) {
				add(chk[i] = new JCheckBox());

				chk[i].addItemListener(a -> {
					var me = ((JCheckBox) a.getSource());

					if (me == chk[0]) {
						pricelbl.setText(format(toInt(pricelbl.getText()) + (me.isSelected() ? 30000 : -30000)) + "원");
					} else {
						pricelbl.setText(format(toInt(pricelbl.getText()) + (me.isSelected() ? 35000 : -35000)) + "원");
					}

					var tot = items.stream().mapToInt(it -> toInt(it.pricelbl.getText())).sum();

					totPrice.setText("총 " + format(tot) + "원");
				});
			}

			add(pricelbl);
		}
	}

	class Delete extends JDialog {
		JPanel c = new JPanel(new GridLayout(0, 3));

		public Delete() {
			setLayout(new GridBagLayout());
			setSize(660, (items.size() / 3 + 1) * 220);
			setLocationRelativeTo(null);

			add(c);

			setUI();

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					addItem();
				}
			});
		}

		void setUI() {
			c.removeAll();

			for (var i : items) {
				var tmp = new JPanel(new BorderLayout());

				tmp.add(new JLabel(getIcon("./datafiles/수하물.jpg", 200, 200)));
				tmp.add(lbl(i.namelbl.getText(), 0), "South");

				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getClickCount() == 2) {
							items.remove(i);
							setUI();
						}
					}
				});

				c.add(tmp);
			}

			c.repaint();
			c.revalidate();
		}
	}

	public static void main(String[] args) {
		new Baggage();
	}
}
