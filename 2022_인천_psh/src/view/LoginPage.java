package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginPage extends BasePage {
	JTextField txt[] = { new JTextField(15), new JPasswordField(15) };
	ChkBoxPanel chk = new ChkBoxPanel();
	JLabel sign;

	public LoginPage() {
		ui();
	}

	private void ui() {
		var cc = new JPanel(new GridLayout(0, 1, 5, 5));
		var cs = new JPanel(new BorderLayout());

		setLayout(new GridBagLayout());
		add(sz(border(c = new JPanel(new BorderLayout(5, 5)),
				new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5))), 200, 250));
		c.add(lbl("COVID-19", 0, 20), "North");
		c.add(cc);
		c.add(cs, "South");
		for (var c : "ID,PW".split(",")) {
			cc.add(lbl(c, 2, 12));
			cc.add(txt[Arrays.asList("ID,PW".split(",")).indexOf(c)]);
		}
		cs.add(chk, "North");
		cc.add(sign = lbl("<html><u>처음이십니까?", 2, Font.BOLD, 13));
		sign.setForeground(Color.orange);
	}

	class ChkBoxPanel extends JPanel {

	}
}
