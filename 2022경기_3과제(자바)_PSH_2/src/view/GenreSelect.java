package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GenreSelect extends BaseDialog {
	@Override
	public JButton btn(String c, ActionListener a) {
		var b = super.btn(c, a);
		b.setBackground(null);
		b.setForeground(Color.black);
		return b;
	}

	public GenreSelect(ArrayList<String> genre) {
		super("장르 선택", 200, 250);

		add(c = new JPanel(new GridLayout(0, 2)));
		add(s = new JPanel(), "South");

		for (int i = 0; i < g_genre.length; i++) {
			var l = lbl(g_genre[i], 2, 1, 15, e -> {
				var me = (JLabel) e.getSource();

				if (me.isEnabled())
					genre.add(me.getName());
				else
					genre.remove(me.getName());

				me.setEnabled(!me.isEnabled());

				var page = BasePage.mf.c.getComponent(BasePage.mf.c.getComponentCount() - 1);
				
				if(page instanceof InfoEditPage) {
					((InfoEditPage) page).setFilter();
				}else if(page instanceof ChartPage) {
					page.repaint();
				}else if(page instanceof GameInfoPage) {
					
				}
			});
		}
	}
}
