package view;

import java.awt.Color;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ChartPage extends BasePage {
    TableChart table = new TableChart();

    public ChartPage() {
        add(table);

        table.start();
    }

    class TableChart extends JPanel {
        DefaultTableModel m = new DefaultTableModel(null, "지역,신규 확진자,총 확진자".split(",")) {
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        JTable t = new JTable(m);
        JScrollPane scr = new JScrollPane(t);
        HashMap<String, Integer> datas = new HashMap<String, Integer>();
        LocalDateTime now = LocalDateTime.of(2022, 1, 1, 8, 50, 0);
        String oldTime = createTime(now), curTime = createTime(now.plusMinutes(10));
        JLabel lbls[] = new JLabel[7];
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                return lbls[row];
            };
        };

        String createTime(LocalDateTime time) {
            return time.withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public TableChart() {
            add(scr);

            now = now.plusMinutes(10);
            
            for (int i = 0; i < lbls.length; i++) {
                lbls[i] = lbl("", JLabel.LEFT);
            }
            
            t.getColumnModel().getColumn(2).setCellRenderer(dtcr);

            data();
        }

        private void data() {
            try {
                m.setRowCount(0);
                var rs = rs("SELECT r.name, count(*)-(SELECT COUNT(*) FROM infection WHERE `when` <= '" + oldTime + "' "
                        + "AND fect.region = region) AS `new`, COUNT(*) AS 'total' FROM region r, infection fect "
                        + "WHERE r.no = fect.region AND fect.`when` <= '" + curTime + "' GROUP BY r.name");
                while (rs.next()) {
                    Object row[] = new Object[3];
                    for (int i = 0; i < row.length; i++) {
                        row[i] = rs.getString(i + 1);
                    }

                    var cap = "<html><pre><font face='맑은 고딕'>,</font></pre>".split(",");

                    int idx = rs.getRow() - 1;

                    if (0 < toInt(row[1])) {
                        lbls[idx].setText(cap[0] + "▲\t" + row[2] + cap[1]);
                        lbls[idx].setForeground(Color.red);
                    } else if (toInt(row[1]) == 0) {
                        lbls[idx].setText(cap[0] + "=\t" + row[2] + cap[1]);
                        lbls[idx].setForeground(Color.green);
                    }
                    
                    m.addRow(row);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            repaint();
        }

        public void start() {
            Timer timer = new Timer(1000, a -> {
                System.out.println("oldTime: " + oldTime + ", curTime: " + curTime);
                now = now.plusMinutes(10);
                oldTime = curTime;
                curTime = createTime(now);

                data();
            });
            timer.start();
        }
    }

    public static void main(String[] args) {
        BasePage.mf.swapPage(new ChartPage());
        BasePage.mf.setVisible(true);
    }
}