package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.tools.Tool;

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
		
//		for (int i = 1; i < g_genre.length; i++) {
//			var l = lbl(g_genre[i], 2, 1, 15, e -> {
//				var me = (JLabel) e.getSource();
//
//				if (me.isEnabled())
//					genre.add(me.getName());
//				else
//					genre.remove(me.getName());
//
//				me.setEnabled(!me.isEnabled());
//				
//				var curPage = BasePage.mf.c.getComponent(BasePage.mf.c.getComponentCount() - 1);
//
//				if (curPage instanceof InfoEditPage) {
//					((InfoEditPage) curPage).setFilter();
//				} else if (curPage instanceof ChartPage) {
//					curPage.repaint();
//				} else if (curPage instanceof GameInfoPage) {
//					((GameInfoPage) curPage).lblGenre.setText(genre.stream().map(c->g_genre[toInt(c)]).collect(Collectors.joining(",")));
//				}
//			});
//
//			l.setName(i + "");
//			l.setEnabled(genre.indexOf(i + "") == -1);
//
//			c.add(l);
//		}

		s.add(btn("닫기", a -> {
			dispose();
		}));

		op((JPanel) getContentPane());
	}
}
