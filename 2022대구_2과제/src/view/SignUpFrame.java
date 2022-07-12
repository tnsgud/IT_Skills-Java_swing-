package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SignUpFrame extends BaseFrame {
	JLabel lblImg;
	ImageIcon icon = getIcon("./지급자료/image/user/0.jpg", 80, 80);
	JComboBox com[] = new JComboBox[3];
	JTextField txt[] = new JTextField[5];
	JRadioButton radio[] = new JRadioButton[2];
	LocalDate date, today = LocalDate.now();
	String path = "";

	public SignUpFrame() {
		super("Sign up", 750, 600);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		lblImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					path = getFilePath();
					icon = path.isEmpty() ? icon : getIcon(path, 80, 80);
					lblImg.repaint();
				}
			}
		});

		com[0].addActionListener(a -> {
			date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);

			com[1].removeAllItems();
			com[2].removeAllItems();

			var flag = date.getYear() == today.getYear();
			var month = flag ? today.getMonthValue() : 12;
			var day = flag ? today.getDayOfMonth() : date.lengthOfMonth();

			for (int i = 0; i < month; i++) {
				com[1].addItem(String.format("%02d", i + 1));
			}

			for (int i = 0; i < day; i++) {
				com[2].addItem(String.format("%02d", i + 1));
			}
		});

		com[1].addActionListener(a -> {
			if (com[1].getItemCount() == 0)
				return;

			com[2].removeAllItems();

			date = LocalDate.of(toInt(com[0].getSelectedItem()), toInt(com[1].getSelectedItem()), 1);
			for (int i = 0; i < date.lengthOfMonth(); i++) {
				com[2].addItem(String.format("%02d", i + 1));
			}
		});
	}

	private void data() {
		for (int i = 1900; i < 2023; i++) {
			com[0].addItem(i);
		}

		for (int i = 0; i < LocalDate.now().getMonthValue(); i++) {
			com[1].addItem(String.format("%02d", i + 1));
		}

		for (int i = 0; i < LocalDate.now().getDayOfMonth(); i++) {
			com[2].addItem(String.format("%02d", i + 1));
		}

		for (int i = 0; i < 3; i++) {
			com[i].setSelectedIndex(com[i].getItemCount() - 1);
		}
	}

	private void ui() {
		add(w = sz(new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				var paint = new GradientPaint(0, 0, red, getWidth(), getHeight(), Color.yellow);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setPaint(paint);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		}, getWidth() / 2 - 50, 0), "West");
		add(c = new JPanel(new BorderLayout(10, 10)));

		w.add(lbl("<html><font color='white'>Create your Account<br>회원가입", 2, 20), "North");
		w.add(lbl("<html><font color='white'>SKILL CINEMA에 오신걸 환영합니다.<br>계정 정보를 입력해주세요.", 2, 15));

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(sz(btnRound("완료", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					t.requestFocus();
					return;
				}
			}

			var id = txt[0].getText();
			var pw = txt[1].getText();

			if (!(id.matches(".*[0-9].*") && id.matches(".*[a-zA-Z].*")) || (id.length() < 4 || id.length() > 20)) {
				eMsg("아이디 형식이 올바르지 않습니다.");
				txt[0].setText("");
				txt[0].requestFocus();
				return;
			}

			if (!getOne("select * from user where u_id=?", id).isEmpty()) {
				eMsg("중목된 아이디입니다.");
				txt[0].setText("");
				txt[0].requestFocus();
				return;
			}

			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| (id.length() < 6 || id.length() > 30)) {
				eMsg("비밀번호 형식이 올바르지 않습니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (!pw.equals(txt[2].getText())) {
				eMsg("비밀번호 확인이 일치하지 않습니다.");
				txt[2].setText("");
				txt[2].requestFocus();
				return;
			}

			if (path.isEmpty()) {
				eMsg("이미지를 선택해주세요.");
				return;
			}

			if (!(radio[0].isSelected() || radio[1].isSelected())) {
				eMsg("성별을 선택해주세요.");
				return;
			}

			var birth = Stream.of(com).map(co -> co.getSelectedItem().toString()).collect(Collectors.joining("-"));
			execute("insert user values(0,?,?,?,?,?,1)", id, pw, txt[3].getText(), birth,
					radio[0].isSelected() ? 1 : 2);
			user = getRows("select * from user order by u_no desc").get(0);
			try {
				Files.copy(new File(path).toPath(), new File("./지급자료/image/user/" + user.get(0) + ".jpg").toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iMsg("회원가입이 완료되었습니다.");

			setVisible(false);
			MainFrame.login();
			new MainFrame();
		}), 0, 30), "South");

		cn.add(lblImg = sz(new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var buf = new BufferedImage(80, 80, BufferedImage.TYPE_4BYTE_ABGR);
				var bufG2 = (Graphics2D) buf.getGraphics();
				var g2 = (Graphics2D) g;

				bufG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				icon.paintIcon(null, bufG2, 0, 0);
				g2.setPaint(new TexturePaint(buf, new Rectangle2D.Double(0, 0, getWidth(), getHeight())));
				g2.fillOval(0, 0, buf.getWidth(), buf.getHeight());
			}
		}, 80, 80));

		var cap = "아이디,비밀번호,비밀번호 확인,이름,전화번호,생년월일".split(",");
		var hint = "ID,Password,Password Check,Name,Phone Number".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			if (i == 5) {
				var tmpW = new JPanel(new BorderLayout());
				var tmpE = new JPanel(new BorderLayout());
				var temp1 = new JPanel(new FlowLayout(0));
				var temp2 = new JPanel(new FlowLayout(0));
				var bg = new ButtonGroup();
				var ca = "년,월,일".split(",");

				tmpW.add(lbl(cap[i], 2, 13), "North");
				tmpE.add(lbl("성별", 2, 13), "North");

				tmpW.add(temp1);
				tmpE.add(temp2);

				for (int j = 0; j < com.length; j++) {
					temp1.add(com[j] = new JComboBox());
					com[j].setBackground(Color.white);
					temp1.add(lbl(ca[j], 2, 13));
				}

				ca = "남,여".split(",");
				for (int j = 0; j < ca.length; j++) {
					temp2.add(radio[j] = new JRadioButton(ca[j]));
					bg.add(radio[j]);
				}

				tmp.add(tmpW, "West");
				tmp.add(tmpE, "East");
			} else {
				tmp.add(lbl(cap[i], 2, 13), "North");
				tmp.add(txt[i] = i == 1 || i == 2 ? hintPassField(hint[i], 0) : hintField(hint[i], 0));
			}

			cc.add(tmp);
		}

		w.setBorder(new EmptyBorder(20, 10, 300, 10));

		opaque(c);
		c.setBackground(Color.white);
		c.setBorder(new EmptyBorder(10, 50, 5, 50));
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
