package view;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ChartPage extends BasePage {

	public ChartPage() {
		setLayout(new GridLayout(1, 0));

		var word = new WordCloud();
		var table = new TableChart();

		add(word);
		add(table);

		repaint();
	}

	class WordCloud extends JPanel {
		public WordCloud() {
		}
	}

	class TableChart extends JPanel {
		DefaultTableModel m = new DefaultTableModel(null, "지역,신규 확진자,총 확진자".split(",")) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		JTable t = new JTable(m);

		public TableChart() {
			add(t);
			
			data();
		}

		private void data() {
			for (int i = 0; i < 7; i++) {
				try {
					var rs = rs("select r.name, count(*) from region r inner join infection infect on r.no = infect.region where r.no = "+(i+1));
					if(rs.next()) {
						Object row[]= new Object[3];
						for (int j = 0; j < row.length; j++) {
							row[i] =rs.getString(j+1);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new ChartPage());
	}
}
