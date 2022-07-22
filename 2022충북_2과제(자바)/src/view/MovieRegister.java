package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MovieRegister extends BaseFrame {

	JLabel lblImg;
	JComboBox<String> com = new JComboBox<>(("선택," + getRows("select g_name from genre").stream()
			.map(a -> a.get(0).toString()).collect(Collectors.joining(","))).split(","));
	JTextArea area;
	JTextField txt = hintField("제목을 입력해주세요.", 0);
	JScrollPane scr;

	public MovieRegister() {
		super("영화등록", 650, 350);

		setLayout(new BorderLayout(5, 5));

		add(lblImg = sz(new JLabel(), 200, 300), "West");
		add(c = new JPanel(new BorderLayout(10, 10)));
		add(s = new JPanel(), "South");

		c.add(cn = new JPanel(new GridLayout(0, 1, 0, 5)), "North");
		c.add(scr = new JScrollPane(area = new JTextArea()));

		cn.add(txt);
		cn.add(com);

		s.add(btnBlack("취소", a -> dispose()));
		s.add(btn("등록", a -> {
			if (lblImg.getIcon() == null || txt.getText().isEmpty() || com.getSelectedIndex() == 0
					|| area.getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			if (!getOne("select * from movie where m_name=?", txt.getText()).isEmpty()) {
				eMsg("이미 등로괸 영화입니다.");
				return;
			}

			iMsg("등록이 완료되었습니다.");

			var img = new BufferedImage(lblImg.getWidth(), lblImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			var g2 = img.createGraphics();

			lblImg.paintAll(g2);

			execute("insert movie values(0,?,?,?,?,?,?)", com.getSelectedIndex(), 0.0, 0, 0, txt.getText(),
					area.getText());
			try {
				ImageIO.write(img, "jpg", new File("./datafile/영화/" + txt.getText() + ".jpg"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			dispose();
		}));

		lblImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var jfc = new JFileChooser("./datafile/영화");
				jfc.setFileFilter(new FileNameExtensionFilter("JPG Images", "jpg"));
				jfc.setMultiSelectionEnabled(false);

				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					lblImg.setIcon(getIcon(jfc.getSelectedFile().getAbsolutePath(), 200, 300));
				}
			}
		});

		area.setLineWrap(true);

		lblImg.setBorder(new LineBorder(Color.black, 2));
		scr.setBorder(new TitledBorder(new LineBorder(Color.black), "설명", 1, 0, lbl("", 0, 20).getFont()));

		setVisible(true);
	}

	public static void main(String[] args) {
		new MovieRegister();
	}
}
