package view;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SearchPage extends BasePage {
	JComboBox com[] = new JComboBox[2];
	JTextField txt = new JTextField();
	JLabel icon = new JLabel(getIcon("./datafiles/기본사진/9.png", 25, 25));
	JPanel result;
	
	public SearchPage() {
		super("검색");

		add(n = new JPanel(), "North");
		add(new JScrollPane(c = new JPanel()));
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		var cap = "구분,장르,검색".split(",");
		for (int i = 0; i < cap.length; i++) {
			n.add(lbl(cap[i], 0, 15));
			n.add(i < 2 ? com[i] : txt);
		}
		n.add(icon);
		
		if(user == null) {
			n.add(btn("등록하기", a->new GameInfoPage()));
		}
		
		search();
	}

	private void search() {
		
	}
}
