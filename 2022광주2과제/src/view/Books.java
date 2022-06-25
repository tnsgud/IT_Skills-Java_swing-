package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Books extends BaseFrame {

	JScrollPane scr;
	JTextField search = new JTextField();
	String gubun = "";
	ArrayList<JLabel> tabs = new ArrayList<>();
	ArrayList<JLabel> gubuns = new ArrayList<>();
	ArrayList<Item> items = new ArrayList<>();

	@Override
	public JButton btn(String c, ActionListener a) {
		var b = super.btn(c, a);
		b.setForeground(Color.white);
		b.setBackground(Color.black);
		return b;
	}

	public Books() {
		super("Books", 700, 400);

		add(n = new JPanel(new BorderLayout(10, 0)), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		n.add(lbl("BOOKS", 0, 1, 35), "North");
		n.add(nw = new JPanel(new GridLayout(1, 0, 5, 0)), "West");
		n.add(nc = new JPanel(new BorderLayout(10, 10)));

		var cap = "인기도서,신간도서,도서명".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = sz(lbl(cap[i], 0, 0, 15, e -> {
				for (var lbl : nw.getComponents()) {
					lbl.setForeground(Color.black);
					lbl.setBackground(new JPanel().getBackground());
				}

				var me = (JLabel) e.getSource();

				me.setForeground(Color.white);
				me.setBackground(Color.black);

				search.setText("");
				gubun = "";

				if (me.getText().equals("인기도서")) {
					popular();
				} else if (me.getText().equals("신간도서")) {
					newBook();
				} else {
					booName();
				}
			}), 100, 30);
			l.setOpaque(true);

			tabs.add(l);

			nw.add(l);

			if (i == 0) {
				l.setForeground(Color.white);
				l.setBackground(Color.black);
			}
		}

		nc.add(search);
		nc.add(btn("🔍", a -> {
			var lbl = tabs.stream().filter(l -> l.getBackground() == Color.black).findFirst().get();

			if (lbl.getText().equals("인기도서")) {
				popular();
			} else if (lbl.getText().equals("신간도서")) {
				newBook();
			} else {
				booName();
			}
		}), "East");

		c.add(cw = sz(new JPanel(new BorderLayout(0, 0)), 150, 0), "West");
		c.add(scr = new JScrollPane(cc = new JPanel(new GridLayout(0, 4, 5, 5))));

		{
			var tmp = new JPanel(new GridLayout(0, 2));

			cw.add(tmp);

			cap = "전체,총류,철학,종교,사회과학,자연과학,기술과학,예술,언어,문학,역사".split(",");
			for (int i = 0; i < cap.length; i++) {
				var l = lbl(cap[i], 0, 15);
				l.addMouseListener(new MouseAdapter() {
					boolean isSelect = false;

					@Override
					public void mouseEntered(MouseEvent e) {
						for (var com : tmp.getComponents()) {
							com.setForeground(Color.black);
							com.setBackground(new JPanel().getBackground());
						}

						var me = (JLabel) e.getSource();

						me.setForeground(Color.white);
						me.setBackground(Color.gray);

						repaint();
					}

					@Override
					public void mousePressed(MouseEvent e) {
						for (var com : gubuns) {
							com.setForeground(Color.black);
							com.setBackground(new JPanel().getBackground());
						}

						var me = (JLabel) e.getSource();
						var idx = gubuns.indexOf(me);

						me.setForeground(Color.white);
						me.setBackground(Color.gray);

						search.setText("");
						gubun = idx == 0 ? "" : gubuns.indexOf(me) - 1 + "00";

						isSelect = true;
					}

					@Override
					public void mouseExited(MouseEvent e) {
						if (isSelect)
							return;

						for (var com : tmp.getComponents()) {
							com.setForeground(Color.black);
							com.setBackground(new JPanel().getBackground());
						}
					}
				});
				gubuns.add(l);
				l.setOpaque(true);

				if (i == 0) {
					cw.add(l, "North");
					l.setForeground(Color.white);
					l.setBackground(Color.gray);
				} else {
					tmp.add(l);
				}
			}
		}

		n.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new MatteBorder(0, 0, 3, 0, Color.black)));
		n.setBackground(Color.white);
		nw.setOpaque(false);
		nc.setOpaque(false);
		c.setBorder(new EmptyBorder(10, 10, 10, 10));
		c.setBackground(Color.black);
		cc.setBackground(Color.black);
		scr.setBorder(BorderFactory.createEmptyBorder());

		popular();

		setVisible(true);
	}

	void ccUI(ArrayList<ArrayList<Object>> rs, String type) {
		if (rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			search.setText("");

			if (type.equals("pop")) {
				popular();
			} else if (type.equals("new")) {
				newBook();
			} else {
				booName();
			}

			return;
		}

		cc.removeAll();

		for (var r : rs) {
			var item = new Item(r);
			var pop = new JPopupMenu();

			items.add(item);
			item.setComponentPopupMenu(pop);

			cc.add(item);
		}

		repaint();
		revalidate();
	}

	private void booName() {
		var rs = getRows("select * from book where b_title like ? and b_gubun like ? order by b_title asc",
				"%" + search.getText() + "%", "%" + gubun + "%");
		ccUI(rs, "name");
	}

	private void newBook() {
		var rs = getRows("select * from book where b_title like ? and b_gubun like ? order by b_date desc",
				"%" + search.getText() + "%", "%" + gubun + "%");

		ccUI(rs, "new");
	}

	void popular() {
		var rs = getRows(
				"select * from book b left join reserve r on b.b_no = r.b_no where  b_gubun like ? and b_title like ? group by b.b_no order by count(*) desc, b.b_no asc",
				"%" + gubun + "%", "%" + search.getText() + "%");

		ccUI(rs, "pop");
	}

	class Item extends JPanel {
		JLabel img, lbl, like;
		JButton btn;

		public Item(ArrayList<Object> r) {
			super(new BorderLayout(5, 5));
			sz(this, 100, 200);

			img = new JLabel(getIcon("./Datafiles/book/" + r.get(1) + ".jpg", 110, 180));
			lbl = lbl(r.get(1).toString(), 0);
			like = lbl(getOne("select * from `like` where m_no = ? and b_no = ?", user.get(0), r.get(0)).isEmpty() ? "♡"
					: "♥", 2);
			btn = btn("", a -> {
				if (a.getActionCommand().equals("Return")) {
					new Return().addWindowListener(new Before(Books.this));
					return;
				}

				var endate = LocalDate.parse(getOne("select r_date from `return` where bo_no=156")).plusDays(toInt(
						getOne("select datediff(r.r_date, bo.bo_endate) from borrow bo left join `return` r on bo.bo_no = r.bo_no where m_no = ? and  bo.bo_endate < r.r_date",
								user.get(0))));
				if (endate.isAfter(LocalDate.now())) {
					eMsg("현재 대칠 및 예약 정지기간입니다. " + (LocalDate.now().toEpochDay() - endate.toEpochDay())
							+ "일 뒤부처 대출 가능합니다.");
					return;
				}

				if (!getOne(
						"select * from borrow bo left join `return` r on bo.bo_no = r.bo_no where m_no = ? and bo_endate < now() and r.r_no is null",
						user.get(0)).isEmpty()) {
					eMsg("연체된 도서가 있을 경우 도서 " + (a.getActionCommand().equals("Borrow") ? "대출" : "예약") + "은 불가능합니다.");
					return;
				}

				if (a.getActionCommand().equals("Borrow")) {

					if (toInt(getOne(
							"select count(*) from borrow bo left join `return` r on bo.bo_no = r.bo_no where m_no = ? and now() < bo_endate and r.r_no is null")) == 5) {
						eMsg("도서 대출은 최대 5권까지 입니다.");
						return;
					}

					new Borrow().addWindowListener(new Before(Books.this));
				} else {
					if (toInt(getOne("select count(*) from reserve where r_receive = 0 and m_no = ?",
							user.get(0))) >= 2) {
						eMsg("도서 예약은 최대 2권까지 입니다.");
						return;
					}

					if (!getOne("select * from reserve where r_receive = 0 and m_no = ? and b_no = ?", user.get(0),
							r.get(0)).isEmpty()) {
						eMsg("이미 예약한 도서 입니다.");
						return;
					}

					new Reserve().addWindowListener(new Before(Books.this));
				}
			});

			setBackground(Color.black);
			setBorder(new LineBorder(Color.white));

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						items.stream().filter(com -> com.getBackground() == Color.white).forEach(item -> {
							item.setBackground(Color.black);
							item.lbl.setForeground(Color.white);
							item.like.setVisible(false);
							item.btn.setVisible(false);
						});

						var isSelect = getBackground() == Color.black;

						lbl.setForeground(isSelect ? Color.black : Color.white);
						setBackground(isSelect ? Color.white : Color.black);
						like.setVisible(isSelect);
						btn.setVisible(isSelect);
					}
				}
			});
			img.setToolTipText(r.get(1).toString());

			lbl.setForeground(Color.white);

			like.setForeground(Color.red);
			like.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					String sql = "";
					if (like.getText() == "♡") {
						like.setText("♥");
						sql = "insert into `like` values(0, ?, ?)";
					} else {
						like.setText("♡");
						sql = "delete from `like` where m_no = ? and b_no = ?";
					}

					execute(sql, user.get(0), r.get(0));
				}
			});
			like.setVisible(false);
			btn.setVisible(false);

			add(img);
			add(lbl, "South");

			img.add(sz(like, 100, 20)).setBounds(5, 5, 20, 20);
			img.add(btn).setBounds(5, 90, 100, 20);

			btn.setForeground(Color.black);
			btn.setBackground(Color.white);

			if (toInt(getOne("select r_receive from reserve where m_no=? and b_no=?", user.get(0), r.get(0))) == 0) {
				btn.setText("Return");
			} else if (toInt(getOne(
					"select b_title, b_gun-ifnull((select sum(bo_num) from borrow bo left join `return` r on r.bo_no = bo.bo_no where bo.b_no = b.b_no group by r.r_no having r.r_no is null), 0) from book b where b_no= ?",
					r.get(0))) == 0) {
				btn.setText("Reserve");
			} else {
				btn.setText("Borrow");
			}
		}
	}

	public static void main(String[] args) {
		new Books();
	}
}
