package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class SearchPage extends BasePage {
	West west;

	public SearchPage() {
		add(west = sz(new West(), 250, 0), "West");
	}

	private void data() {

	}

	class West extends BasePage {
		JTextField txtSearch, txtAr, txtDe;
		JToggleButton tog[] = new JToggleButton[2];
		JPanel search, path;
		JScrollPane scr;

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search = new JPanel()));
			add(lbl("메인으로", 2, 20, Color.orange, e -> mf.swap(new MainPage())), "South");
			search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

			n.add(nn = new JPanel(new BorderLayout(5, 5)), "North");
			n.add(nc = new JPanel(new GridLayout(1, 0, 5, 5)));

			nn.add(txtSearch = new JTextField());
			nn.add(btn("검색", a -> {
			}), "East");

			var bg = new ButtonGroup();
			for (int i = 0; i < tog.length; i++) {
				tog[i] = new JToggleButton("검색,길찾기".split(",")[i]);
				tog[i].setForeground(Color.white);
				tog[i].setBackground(blue);
				tog[i].setUI(new MetalToggleButtonUI() {
					@Override
					protected Color getSelectColor() {
						return blue.darker();
					}
				});
				tog[i].addActionListener(a -> scr.setViewportView(a.getActionCommand().equals("검색") ? search : path));

				bg.add(tog[i]);
				nc.add(tog[i]);
			}

			path = new JPanel(new BorderLayout(5, 5));

			path.add(cn = new JPanel(new BorderLayout()), "North");
			path.add(cc = new JPanel());
			cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

			{
				var tmp1 = new JPanel(new GridLayout(0, 1, 5, 5));
				var tmp2 = new JPanel(new FlowLayout(2));

				tmp1.add(txtAr = new JTextField());
				tmp1.add(txtDe = new JTextField());

				tmp2.add(btn("집을 출발지로", a -> {
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
				}), "East");
				cn.add(tmp2, "South");
			}

			tog[0].doClick();

			n.setBackground(blue);
			nn.setOpaque(false);
			nc.setOpaque(false);

			n.setBorder(new EmptyBorder(5, 5, 5, 5));
		}
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new SearchPage());
		mf.setVisible(true);
	}
}
