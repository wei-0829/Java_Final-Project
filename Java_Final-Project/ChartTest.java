import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 用於測試JFreeChart是否正確加載的簡單測試類
 */
public class ChartTest {
    public static void main(String[] args) {
        System.out.println("開始測試JFreeChart庫...");
        
        try {
            // 測試加載主要的JFreeChart類
            Class.forName("org.jfree.chart.JFreeChart");
            System.out.println("✅ JFreeChart類已成功加載");
            
            Class.forName("org.jfree.data.general.DefaultPieDataset");
            System.out.println("✅ DefaultPieDataset類已成功加載");
            
            Class.forName("org.jfree.chart.ChartFactory");
            System.out.println("✅ ChartFactory類已成功加載");
            
            // 創建一個簡單的餅圖進行測試
            System.out.println("正在創建測試圖表...");
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("測試項目1", 40);
            dataset.setValue("測試項目2", 30);
            dataset.setValue("測試項目3", 30);
            
            JFreeChart chart = ChartFactory.createPieChart(
                "測試餅圖", 
                dataset, 
                true, 
                true, 
                false
            );
            System.out.println("✅ 成功創建JFreeChart圖表對象");
            
            // 顯示測試圖表
            JFrame testFrame = new JFrame("JFreeChart測試");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(500, 400);
            testFrame.add(new ChartPanel(chart));
            testFrame.setVisible(true);
            System.out.println("✅ 測試圖表已顯示，如果看到餅圖，說明JFreeChart庫正常工作");
            System.out.println("請關閉測試窗口以繼續");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ 找不到JFreeChart庫類：" + e.getMessage());
            System.err.println("請確保lib目錄中有正確的jar檔案且已正確配置classpath");
            JOptionPane.showMessageDialog(null, 
                "無法載入JFreeChart庫。\n錯誤：" + e.getMessage() + 
                "\n\n請確認lib目錄中有jfreechart-1.5.3.jar和jcommon-1.0.24.jar文件",
                "庫文件載入錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("❌ 測試JFreeChart時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "測試JFreeChart時發生錯誤：" + e.getMessage(),
                "測試錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
} 