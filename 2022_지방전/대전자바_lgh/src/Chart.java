import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Chart extends BaseFrame {

	JTree tree;
	DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Orange Ticket");
	DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("분류");
	DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("차트디자인");
	DefaultMutableTreeNode child3 = new DefaultMutableTreeNode("차트색깔");
	DefaultMutableTreeNode type[] = { new DefaultMutableTreeNode("뮤지컬"), new DefaultMutableTreeNode("오페라"),
			new DefaultMutableTreeNode("콘서트") };
	DefaultMutableTreeNode design[] = { new DefaultMutableTreeNode("막대 그래프"), new DefaultMutableTreeNode("꺾은선 그래프") };
	DefaultMutableTreeNode chartColor[] = { new DefaultMutableTreeNode("Red"), new DefaultMutableTreeNode("Orange"),
			new DefaultMutableTreeNode("Blue") };

	{
		parent.add(child1);
		parent.add(child2);
		parent.add(child3);

		for (int i = 0; i < 3; i++) {
			child1.add(type[i]);
			child3.add(chartColor[i]);
			if (i < 2) {
				child2.add(design[i]);
			}
		}
	}

	String name = "M";
	Color col = Color.RED;
	int model = 1;
	int x[] = new int[5], y[] = new int[5];

	public Chart() {
		super("차트", 600, 700);

		this.add(n = new JPanel(new GridLayout()), "North");
		this.add(c = new JPanel(new GridLayout()));

		n.add(tree = new JTree(parent));

		setChart(name, col, model);

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				String t = path.getLastPathComponent().toString();

				if (t.equals("뮤지컬")) {
					name = "M";
				} else if (t.equals("오페라")) {
					name = "O";
				} else if (t.equals("콘서트")) {
					name = "C";
				} else if (t.equals("막대 그래프")) {
					model = 1;
				} else if (t.equals("꺾은선 그래프")) {
					model = 2;
				} else if (t.equals("Red")) {
					col = Color.RED;
				} else if (t.equals("Orange")) {
					col = Color.ORANGE;
				} else if (t.equals("Blue")) {
					col = Color.BLUE;
				}

				setChart(name, col, model);
			}
		});

		n.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "TOP 5", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("맑은 고딕", Font.BOLD, 20)));
		n.setBackground(Color.WHITE);

		this.setVisible(true);
	}

	void setChart(String name, Color col, int model) {
		c.removeAll();

		c.add(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				try {
					var rs = stmt.executeQuery(
							"select *, count(*) as cnt from perform p, ticket t where t.p_no = p.p_no and p.pf_no like '%"
									+ name + "%' group by p.p_name order by count(*) desc limit 5");
					int max = 0, i = 0, h = 0, base = 350;
					if (model == 1) {
						while (rs.next()) {
							if (i == 0) {
								max = rs.getInt("cnt");
							}

							h = (int) (rs.getInt("cnt") / (double) max * 200);

							g2d.setColor(col);
							g2d.fillRect(40 + (120 * i), base - h, 50, h);
							g2d.setColor(Color.BLACK);
							g2d.drawRect(40 + (120 * i), base - h, 50, h);
							g2d.setFont(new Font("맑은 고딕", Font.BOLD, 15));

							var fm = g2d.getFontMetrics(g2d.getFont());
							fm.stringWidth(rs.getString("p_name"));
							g2d.drawString(rs.getString("p_name"), 40 + (120 * i), base + 20);
							fm.stringWidth(rs.getString("cnt"));
							g2d.drawString(rs.getString("cnt"), 40 + (120 * i), base - h - 20);
							i++;
						}
					} else {
						while (rs.next()) {
							if (i == 0) {
								max = rs.getInt("cnt");
							}

							h = (int) (rs.getInt("cnt") / (double) max * 200);

							x[i] = 40 + (120 * i);
							y[i] = base - h;

							g2d.setColor(Color.BLACK);
							g2d.fillOval(40 + (120 * i), base - h, 10, 10);
							g2d.setFont(new Font("맑은 고딕", Font.BOLD, 15));

							var fm = g2d.getFontMetrics(g2d.getFont());
							fm.stringWidth(rs.getString("p_name"));
							g2d.drawString(rs.getString("p_name"), 40 + (120 * i), base + 20);
							fm.stringWidth(rs.getString("cnt"));
							g2d.drawString(rs.getString("cnt"), 40 + (120 * i), base - h - 20);

							i++;
						}

						g2d.setColor(col);
						g2d.setStroke(new BasicStroke(3));
						g2d.drawPolyline(x, y, x.length);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		new Chart();
	}
}
