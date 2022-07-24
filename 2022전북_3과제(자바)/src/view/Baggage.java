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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import model.Bag;

public class Baggage extends BaseFrame {
	public static JLabel totPrice;
	JButton btn[] = new JButton[3];

	public Baggage() {
		super("수화물 구매", 400, 500);

		bag.clear();

		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(new JScrollPane(c = new JPanel()));
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
				if (bag.size() == 5) {
					eMsg("수하물은 최대 5개까지 구매할 수 있습니다.");
					return;
				}
				bag.add(new Bag());
				addItem();
			}
		});

		cap = "삭제,초기화,확인".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i] = btn(cap[i], a -> {
				if (a.getActionCommand().equals("삭제")) {
					new Delete().setVisible(true);
				} else if (a.getActionCommand().equals("초기화")) {
					bag.clear();
					bag.add(new Bag());
					addItem();
				} else {
					iMsg("수하물 선택이 완료되었습니다.");
					dispose();
				}
			});
		}

		bag.add(new Bag());

		addItem();

		cn.setBorder(new MatteBorder(2, 0, 2, 0, Color.black));

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if (e.getOppositeWindow() instanceof JDialog) {
					return;
				}

				dispose();
			}
		});

		setVisible(true);
	}

	void addItem() {
		c.removeAll();

		c.add(cn);

		for (var i : bag) {
			c.add(i);
		}

		c.add(totPrice = lbl("총 0원", 4, 20));

		for (var com : c.getComponents()) {
			((JComponent) com).setMaximumSize(new Dimension(400, 40));
		}

		var tot = bag.stream().mapToInt(it -> toInt(it.pricelbl.getText())).sum();
		totPrice.setText("총 " + format(tot) + "원");

		s.removeAll();
		s.setLayout(bag.size() > 1 ? new GridLayout(0, 3) : new FlowLayout(1));

		if (bag.size() > 1) {
			for (int i = 0; i < btn.length; i++) {
				s.add(btn[i]);
			}
		} else {
			s.add(btn[2]);
		}

		repaint();
		revalidate();
	}

	class Delete extends JDialog {
		JPanel c = new JPanel(new GridLayout(0, 3));

		public Delete() {
			setLayout(new GridBagLayout());
			setSize(660, (bag.size() / 3 + 1) * 220);
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

			for (var i : bag) {
				var tmp = new JPanel(new BorderLayout());

				tmp.add(new JLabel(getIcon("./datafiles/수하물.jpg", 200, 200)));
				tmp.add(lbl(i.namelbl.getText(), 0), "South");

				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getClickCount() == 2) {
							bag.remove(i);
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
}
