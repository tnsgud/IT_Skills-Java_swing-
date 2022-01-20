package Base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window.Type;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import Project.LOgin;

public interface Base {
	
	public void design();
	public void action();
	
	Color blue = new Color(0, 123 ,255);
	
	ArrayList<ArrayList<String>> member = new ArrayList<>();
	ArrayList<ArrayList<String>> location = new ArrayList<>();
	ArrayList<ArrayList<String>> location2 = new ArrayList<>();
	
	JPanel pitem2 = new JPanel(new GridLayout(0, 1));
	
	DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	default void SetFrame(JFrame f, String title, int ex ,int x, int y) {
		f.setTitle(title);
		f.setDefaultCloseOperation(ex);
		f.setSize(x, y);
		f.getContentPane().setBackground(LOgin.back);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
	}
	
	default void SetDial(JDialog d, String title, int ex, int x, int y) {
		d.setTitle(title);
		d.setDefaultCloseOperation(ex);
		d.setSize(x, y);
		d.setLocationRelativeTo(null);
		d.getContentPane().setBackground(LOgin.back);
		d.setType(Type.UTILITY);
		d.setModal(true);
	}
	
	default <Any> Any get(JComponent comp, Set...sets) {
		
		comp.setBackground(LOgin.back);
		comp.setForeground(LOgin.fore);
		comp.setFont(new Font("맑은 고딕", 0, comp.getFont().getSize()));
		
		if (comp instanceof JButton) {
			comp.setBackground(blue);
			comp.setForeground(Color.white);
			comp.setFont(new Font("맑은 고딕", 1, comp.getFont().getSize()));
		}
		
		
		for (Set set : sets) {
			set.set(comp);
		}
		
		return (Any) comp;
		
	}
	
	default Set set(boolean tf) {
		return c->c.setEnabled(tf);
	}
	
	default Set set(Border border) {
		return c->c.setBorder(border);
	}
	
	default Set set(int x, int y) {
		return c->c.setPreferredSize(new Dimension(x, y));
	}
	
	default Set setf(Color color) {
		return c->c.setForeground(color);
	}
	
	default Set setb(Color color) {
		return c->c.setBackground(color);
	}
	
	default Set set(int font) {
		return c->c.setFont(new Font("맑은 고딕", 1, font));
	}
	
	default void allfont(JComponent comp) {
		comp.setFont(new Font("맑은 고딕", 0, comp.getFont().getSize()));
		for (Component c : comp.getComponents()) {
			c.setFont(new Font("맑은 고딕", 0, comp.getFont().getSize()));
		}
		comp.revalidate();
		comp.repaint();
	}
	
	default JTextField gettext(String title, Set...sets) {
		
		JTextField txt = new JTextField() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (!this.getText().isBlank()) {
					return;
				}
				
				g.setColor(Color.gray);
				g.drawString(title, this.getInsets().left, this.getInsets().top + g.getFontMetrics().getMaxAscent() + 5);
				
			}
		};
		
		txt.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		txt.setBackground(LOgin.back);
		txt.setForeground(LOgin.fore);
		
		for (Set set : sets) {
			set.set(txt);
		}
		
		return txt;
		
	}
	
	default JPasswordField getpass(String title, Set...sets) {
		
		JPasswordField txt = new JPasswordField() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (!this.getText().isBlank()) {
					return;
				}
				
				g.setColor(Color.gray);
				g.drawString(title, this.getInsets().left, this.getInsets().top + g.getFontMetrics().getMaxAscent() + 5);
				
			}
		};
		
		txt.setEchoChar('*');
		txt.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		txt.setBackground(LOgin.back);
		txt.setForeground(LOgin.fore);
		
		for (Set set : sets) {
			set.set(txt);
		}
		
		return txt;
		
	}
	
	default void popup(JPopupMenu pop, JComponent comp, int r, int c) {
		
		pop.removeAll();
		
		JPanel in = new JPanel(new GridLayout(1, 2));
		JPanel pitem1 = new JPanel(new GridLayout(0, 1));
		
		JScrollPane scl1 = get(new JScrollPane(pitem1, 20, 31), set(90, 250));
		JScrollPane scl2 = get(new JScrollPane(pitem2, 20, 31), set(90, 250));
		
		pitem1.removeAll();
		pitem2.removeAll();
		
		pop.add(in);
		
		in.add(scl1);
		in.add(scl2);
		
		Query("SELECT * FROM busticketbooking.location;", location);
		
		for (int i = 0; i < location.size(); i++) {
			
			JButton btn = new JButton(location.get(i).get(1));
			
			int n = i;
			
			btn.addActionListener(e->{
				
				Query("select * from location2 where location_no = ?;", location2, location.get(n).get(0));
				pitem2.removeAll();
				
				for (int j = 0; j < location2.size(); j++) {
					
					JButton btnn = new JButton(location2.get(j).get(1));
					
					int nn = j;
					btnn.addActionListener(e2->{
						
						if (comp instanceof JTextField) {
							((JTextField) comp).setText(btn.getText() + " " + btnn.getText());
							comp.setName(location2.get(nn).get(0));
						}else {
							((JTable)comp).setValueAt(btn.getText() + " " + btnn.getText(), r, c);
						}
						
					});
					
					pitem2.add(btnn);
					
				}
				
				pitem2.revalidate();
				pitem2.repaint();
				
			});
			
			pitem1.add(btn);
			
		}
		
	}
	
	default void tblcenter(JTable tbl) {
		
		for (int i = 0; i < tbl.getRowCount(); i++) {
			for (int j = 0; j < tbl.getColumnCount(); j++) {
				
				TableCellRenderer cell = tbl.getCellRenderer(i, j);
				Component c= tbl.prepareRenderer(cell, i, j);
				
				((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
				c.setBackground(LOgin.back);
				c.setForeground(LOgin.fore);
				
			}
		}
		
		tbl.setAutoCreateRowSorter(true);
		tbl.setSelectionMode(0);
		
		UIManager.getLookAndFeelDefaults().put("Table.ascendingSortIcon", sicon("↑"));
		UIManager.getLookAndFeelDefaults().put("Table.descendingSortIcon", sicon("↓"));
		
	}
	
	default Integer intnum(String txt) {
		return Integer.parseInt(txt);
	}
	
	default Icon sicon(String txt) {
		
		Icon icon = new Icon() {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(txt, x, y + 4);
			}
			
			@Override
			public int getIconWidth() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getIconHeight() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
		return icon;
		
	}
	
	default JLabel DBimg(String no, String title, int x, int y, Set...sets) {
		
		JLabel comp = new JLabel(new ImageIcon(getimg(no, title).getImage().getScaledInstance(x, y, 4)));
		
		for (Set set : sets) {
			set.set(comp);
		}
		
		return comp;
		
	}
	
	default JLabel Getimg(String file, int x, int y, Set...sets) {
		
		JLabel comp = new JLabel(new ImageIcon(new ImageIcon(file(file)).getImage().getScaledInstance(x, y, 4)));
		
		for (Set set : sets) {
			set.set(comp);
		}
		
		return comp;
		
	}
	
	default void change(JComponent comp) {
		comp.setBackground(LOgin.back);
		comp.setForeground(LOgin.fore);
		LOgin.back = LOgin.back.equals(Color.white) ? Color.DARK_GRAY : Color.white;
		LOgin.fore = LOgin.fore.equals(Color.black) ? Color.white : Color.black;
	}
	
	default void tema(JComponent comp) {
		
		comp.setBackground(LOgin.back);
		comp.setForeground(LOgin.fore);
		for (Component c : comp.getComponents()) {
			if (c instanceof JButton == false) {
				c.setBackground(LOgin.back);
				c.setForeground(LOgin.fore);
			}
		}
		comp.revalidate();
		comp.repaint();
		
	}
	
	default String file(String txt) {
		return "지급파일/images/" + txt;
	}
	
	default void Query(String sql, ArrayList<ArrayList<String>> list, String...v) {
		
		try {
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost/busticketbooking?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			PreparedStatement s = c.prepareStatement(sql);
			
			for (int i = 0; i < v.length; i++) {
				s.setString(i + 1, v[i]);
			}
			
			list.clear();
			ResultSet rs = s.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			
			while (rs.next()) {
				ArrayList row = new ArrayList<>();
				for (int i = 1; i <= rsm.getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				list.add(row);
			}
			
			s.close();
			c.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	default void Updat(String sql, String...v) {
		
		try {
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost/busticketbooking?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			PreparedStatement s = c.prepareStatement(sql);
			
			for (int i = 0; i < v.length; i++) {
				s.setString(i + 1, v[i]);
			}
			
			s.executeUpdate();
			s.close();
			c.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	default void SaveImg(String file, String no, String title) {
		
		try {
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost/busticketbooking?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			PreparedStatement s = c.prepareStatement("update recommend_info set img = ? where recommend_no = ? and title = ?");
			
			s.setBytes(1, new FileInputStream(new File(file)).readAllBytes());
			s.setString(2, no);
			s.setString(3, title);
			
			s.executeUpdate();
			s.close();
			c.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	default ImageIcon getimg(String no, String title) {
		
		try {
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost/busticketbooking?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			PreparedStatement s = c.prepareStatement("select img from recommend_info where recommend_no = ? and title = ?");
			
			s.setString(1, no);
			s.setString(2, title);
			ResultSet rs = s.executeQuery();
			
			rs.next();
			
			var img = rs.getBinaryStream(1);
			
			return new ImageIcon(img.readAllBytes());
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
		
	}
	
}
