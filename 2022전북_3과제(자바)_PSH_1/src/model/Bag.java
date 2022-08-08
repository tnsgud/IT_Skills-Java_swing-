package model;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import tool.Tool;
import view.Baggage;
import view.BaseFrame;

public class Bag extends JPanel implements Tool{
	public JCheckBox chk[] = new JCheckBox[2];
	public JLabel namelbl, pricelbl = lbl(format(BaseFrame.bag.size() == 0 ? 0 : 50000) + "원", 0, 15);

	public Bag() {
		super(new GridLayout(1, 0));

		setBorder(new MatteBorder(0, 0, 2, 0, Color.black));

		add(namelbl = lbl(
				"bag" + (BaseFrame.bag.size() == 0 ? 1 : toInt(BaseFrame.bag.get(BaseFrame.bag.size() - 1).namelbl.getText()) + 1), 0, 15));
		for (int i = 0; i < chk.length; i++) {
			add(chk[i] = new JCheckBox());

			chk[i].addItemListener(a -> {
				var me = ((JCheckBox) a.getSource());

				if (me == chk[0]) {
					pricelbl.setText(format(toInt(pricelbl.getText()) + (me.isSelected() ? 30000 : -30000)) + "원");
				} else {
					pricelbl.setText(format(toInt(pricelbl.getText()) + (me.isSelected() ? 35000 : -35000)) + "원");
				}

				var tot = BaseFrame.bag.stream().mapToInt(it -> toInt(it.pricelbl.getText())).sum();

				Baggage.totPrice.setText("총 " + format(tot) + "원");
			});
		}

		add(pricelbl);
	}
}