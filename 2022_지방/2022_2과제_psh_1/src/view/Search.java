package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	DefaultTableModel m = model("지역".split(","));
	JTable t = table(m);
	JComboBox<String> com = new JComboBox<>();
	JTextField txt = new JTextField(15);
	JPanel cc;
	ArrayList<String> cno = new ArrayList<>();

	public Search() {
		super("검색", 800, 500);

		data();
		ui();

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		addRow(m, "select a_name from area");
		m.insertRow(0, new Object[] { "전국" });
		t.setRowSelectionInterval(0, 0);
		try {
			var rs = rs("select * from genre");
			com.addItem("전체");
			while (rs.next()) {
				com.addItem(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var cn = new JPanel(new FlowLayout(2));

		add(lbl("방탈출 카페 검색", 2, 35), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));

		c.add(cn, "North");
		c.add(sz(new JScrollPane(t), 80, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		cn.add(lbl("장르", 2));
		cn.add(com);
		cn.add(lbl("테마", 2));
		cn.add(txt);
		cn.add(btn("검색", a -> {
			search();
		}));

		reset();
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	private void reset() {
		cno.clear();
		txt.setText("");
		com.setSelectedIndex(0);
		t.setRowSelectionInterval(0, 0);

		try {
			var rs = rs("select * from cafe");
			while (rs.next()) {
				var item = new Item(rs.getString(1));
				cc.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void search() {
		cno.clear();
		cc.removeAll();

		var ano = t.getSelectedRow();
		var gno = com.getSelectedIndex();
		var rs1 = rs("select t_no from theme t, genre g where t.g_no=g.g_no and " + (gno < 1 ? "" : "g.g_no=")
				+ "? and t_name like ?", gno < 1 ? 1 : gno, "%" + txt.getText() + "%");
		try {
			while (rs1.next()) {
				var tno = rs1.getString(1);
				var rs2 = rs(
						"select * from cafe where (t_no like ? or t_no like ? or t_no like ?) and "
								+ (ano < 1 ? "" : "a_no=") + "?",
						tno + ",%", "%," + tno + ",%", "%," + tno, ano < 1 ? 1 : ano);
				while (rs2.next()) {
					if (cno.contains(rs2.getString(1)))
						continue;
					var item = new Item(rs2.getString(1));
					cc.add(item);
					cno.add(rs2.getString(1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cc.getComponentCount() == 0) {
			eMsg("검색 결과가 없습니다.");
			reset();
		}

		cc.repaint();
		cc.revalidate();
	}

	class Item extends JPanel {
		String cno;

		public Item(String cno) {
			this.cno = cno;
			setLayout(new BorderLayout(5, 5));

			var name = getOne("select c_name from cafe where c_no=?", cno);

			add(img("지점/" + name.split(" ")[0] + ".jpg", 200, 100));
			add(lbl(name, 0), "South");

			setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(e.getClickCount() == 2) {
						BaseFrame.cno = cno;
						new Introduction().addWindowListener(new Before(Search.this));
					}
				}
			});
		}
	}
}
