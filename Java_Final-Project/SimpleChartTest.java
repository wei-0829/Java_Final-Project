import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChartTest {
    public static void main(String[] args) {
        System.out.println("Starting JFreeChart Test");
        
        // 創建一個簡單的測試窗口
        JFrame frame = new JFrame("Chart Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        JButton testButton = new JButton("Test JFreeChart");
        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // 測試是否可以載入JFreeChart類
                    Class.forName("org.jfree.chart.JFreeChart");
                    JOptionPane.showMessageDialog(frame, 
                        "成功載入JFreeChart庫！圖表功能應該可以正常工作。",
                        "測試成功", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 嘗試創建和顯示圖表
                    displayTestChart(frame);
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "無法載入JFreeChart庫！\n錯誤: " + ex.getMessage() + 
                        "\n\n請確保lib目錄中有jfreechart-1.5.3.jar和jcommon-1.0.24.jar文件。",
                        "測試失敗", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "嘗試創建圖表時出錯！\n錯誤: " + ex.getMessage(),
                        "測試失敗", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        panel.add(testButton, BorderLayout.CENTER);
        
        // 添加說明標籤
        JLabel label = new JLabel("<html>這是一個測試程序，用於確認JFreeChart庫是否正確加載。<br>" +
                                  "點擊按鈕測試JFreeChart功能。</html>");
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(label, BorderLayout.NORTH);
        
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        
        System.out.println("Test window opened");
    }
    
    // 嘗試顯示測試圖表
    private static void displayTestChart(JFrame parent) {
        try {
            // 使用反射避免直接引用JFreeChart類，這樣即使庫不存在也不會導致編譯錯誤
            Class<?> datasetClass = Class.forName("org.jfree.data.general.DefaultPieDataset");
            Object dataset = datasetClass.getDeclaredConstructor().newInstance();
            
            // 添加數據
            Class<?> pieDatasetClass = Class.forName("org.jfree.data.general.PieDataset");
            java.lang.reflect.Method setValue = datasetClass.getMethod("setValue", 
                                                                     Comparable.class, Number.class);
            setValue.invoke(dataset, "項目1", 40);
            setValue.invoke(dataset, "項目2", 30);
            setValue.invoke(dataset, "項目3", 30);
            
            // 創建圖表
            Class<?> chartFactoryClass = Class.forName("org.jfree.chart.ChartFactory");
            java.lang.reflect.Method createPieChart = chartFactoryClass.getMethod("createPieChart", 
                                                                       String.class, 
                                                                       pieDatasetClass, 
                                                                       boolean.class, 
                                                                       boolean.class, 
                                                                       boolean.class);
            Object chart = createPieChart.invoke(null, "測試餅圖", dataset, true, true, false);
            
            // 創建圖表面板
            Class<?> chartPanelClass = Class.forName("org.jfree.chart.ChartPanel");
            Object chartPanel = chartPanelClass.getDeclaredConstructor(chart.getClass())
                                              .newInstance(chart);
            
            // 顯示圖表
            JFrame chartFrame = new JFrame("測試圖表");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setSize(500, 400);
            
            // 將chartPanel添加到畫面
            chartFrame.getContentPane().add((Component)chartPanel);
            chartFrame.setVisible(true);
            
            System.out.println("Test chart displayed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create test chart: " + e.getMessage(), e);
        }
    }
} 