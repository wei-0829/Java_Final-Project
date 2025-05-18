package AccountProgram;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartUtils {

    private static final Font CHINESE_FONT = new Font("SimSun", Font.PLAIN, 14);

    public static void createLineChart(AccountList accountList, String title, String xAxis, String yAxis, JFrame frame) {
        if (accountList.size() == 0) {
            JLabel noDataLabel = new JLabel("無資料可顯示", SwingConstants.CENTER);
            noDataLabel.setFont(CHINESE_FONT);
            frame.add(noDataLabel);
            return;
        }

        XYSeries incomeSeries = new XYSeries("收入");
        XYSeries expenseSeries = new XYSeries("支出");

        // Find date range
        String earliestDate = accountList.get(0).getDate();
        String latestDate = accountList.get(0).getDate();
        for (int i = 1; i < accountList.size(); i++) {
            String date = accountList.get(i).getDate();
            if (date.compareTo(earliestDate) < 0) earliestDate = date;
            if (date.compareTo(latestDate) > 0) latestDate = date;
        }
        System.out.println("Earliest date: " + earliestDate + ", Latest date: " + latestDate);

        // Generate all dates in range in "YYYY/MM/DD" format
        List<String> allDates = generateDateRange(earliestDate, latestDate);
        System.out.println("All dates for line chart: " + allDates);

        // Populate series using actual dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        double maxValue = 0;
        for (String dateStr : allDates) {
            try {
                Date date = sdf.parse(dateStr);
                long time = date.getTime();
                boolean found = false;
                for (int j = 0; j < accountList.size(); j++) {
                    Account account = accountList.get(j);
                    String accountDate = account.getDate().trim();
                    if (accountDate.equals(dateStr)) {
                        double income = account.getIncome();
                        double expenses = account.getTotal();
                        incomeSeries.add(time, income);
                        expenseSeries.add(time, expenses);
                        System.out.println("Matched Date: " + accountDate + ", Income: " + income + ", Expenses: " + expenses);
                        maxValue = Math.max(maxValue, Math.max(income, expenses));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    incomeSeries.add(time, 0);
                    expenseSeries.add(time, 0);
                    System.out.println("Date: " + dateStr + ", Income: 0, Expenses: 0");
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date " + dateStr + ": " + e.getMessage());
            }
        }

        if (incomeSeries.getItemCount() == 0) {
            JLabel noDataLabel = new JLabel("無有效日期資料可顯示", SwingConstants.CENTER);
            noDataLabel.setFont(CHINESE_FONT);
            frame.add(noDataLabel);
            return;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(incomeSeries);
        dataset.addSeries(expenseSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxis,
                yAxis,
                dataset
        );

        chart.getTitle().setFont(CHINESE_FONT);
        chart.getLegend().setItemFont(CHINESE_FONT);

        XYPlot plot = chart.getXYPlot();
        DateAxis dateAxis = new DateAxis(xAxis);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy/MM/dd"));
        dateAxis.setLabelFont(CHINESE_FONT);
        dateAxis.setTickLabelFont(CHINESE_FONT);
        plot.setDomainAxis(dateAxis);

        plot.getRangeAxis().setLabelFont(CHINESE_FONT);
        plot.getRangeAxis().setTickLabelFont(CHINESE_FONT);
        if (maxValue > 0) {
            plot.getRangeAxis().setRange(0, maxValue * 1.1);
        } else {
            plot.getRangeAxis().setRange(0, 100); // Default range
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);

        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        frame.add(chartPanel);
    }

    private static List<String> generateDateRange(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            while (!calendar.getTime().after(end)) {
                dates.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (ParseException e) {
            System.err.println("Error generating date range: " + e.getMessage());
        }
        return dates;
    }

    public static void createPieChartExpenses(AccountList accountList, String title, JFrame frame) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        int breakfastTotal = 0, lunchTotal = 0, dinnerTotal = 0, othersTotal = 0;
        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);
            breakfastTotal += account.getBreakfast();
            lunchTotal += account.getLunch();
            dinnerTotal += account.getDinner();
            othersTotal += account.getOthers();
        }

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        if (breakfastTotal > 0) {
            labels.add("早餐");
            values.add(breakfastTotal);
        }
        if (lunchTotal > 0) {
            labels.add("午餐");
            values.add(lunchTotal);
        }
        if (dinnerTotal > 0) {
            labels.add("晚餐");
            values.add(dinnerTotal);
        }
        if (othersTotal > 0) {
            labels.add("其他");
            values.add(othersTotal);
        }

        if (labels.isEmpty()) {
            JLabel noDataLabel = new JLabel("無支出資料可顯示", SwingConstants.CENTER);
            noDataLabel.setFont(CHINESE_FONT);
            frame.add(noDataLabel);
            return;
        }

        for (int i = 0; i < labels.size(); i++) {
            dataset.setValue(labels.get(i), values.get(i));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );

        chart.getTitle().setFont(CHINESE_FONT);
        chart.getLegend().setItemFont(CHINESE_FONT);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(CHINESE_FONT);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} 元 ({2})",
                NumberFormat.getNumberInstance(),
                NumberFormat.getPercentInstance()
        ));
        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        frame.add(chartPanel);
    }

    public static void createPieChartIncomeVsExpenses(AccountList accountList, String title, JFrame frame) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        int totalIncome = 0, totalExpenses = 0;
        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);
            totalIncome += account.getIncome();
            totalExpenses += account.getTotal();
        }

        dataset.setValue("收入", totalIncome);
        dataset.setValue("支出", totalExpenses);

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );

        chart.getTitle().setFont(CHINESE_FONT);
        chart.getLegend().setItemFont(CHINESE_FONT);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(CHINESE_FONT);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} 元 ({2})",
                NumberFormat.getNumberInstance(),
                NumberFormat.getPercentInstance()
        ));
        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        frame.add(chartPanel);
    }

    public static void createNetBalanceChart(AccountList accountList, String title, String xAxis, String yAxis, JFrame frame) {
        if (accountList.size() == 0) {
            JLabel noDataLabel = new JLabel("無資料可顯示", SwingConstants.CENTER);
            noDataLabel.setFont(CHINESE_FONT);
            frame.add(noDataLabel);
            return;
        }

        XYSeries series = new XYSeries("淨餘額");

        String earliestDate = accountList.get(0).getDate();
        String latestDate = accountList.get(0).getDate();
        for (int i = 1; i < accountList.size(); i++) {
            String date = accountList.get(i).getDate();
            if (date.compareTo(earliestDate) < 0) earliestDate = date;
            if (date.compareTo(latestDate) > 0) latestDate = date;
        }

        List<String> allDates = generateDateRange(earliestDate, latestDate);
        System.out.println("All dates for net balance chart: " + allDates);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        double maxValue = 0;
        double minValue = 0;
        for (String dateStr : allDates) {
            try {
                Date date = sdf.parse(dateStr);
                long time = date.getTime();
                boolean found = false;
                for (int j = 0; j < accountList.size(); j++) {
                    Account account = accountList.get(j);
                    String accountDate = account.getDate().trim();
                    if (accountDate.equals(dateStr)) {
                        int netBalance = account.getIncome() - account.getTotal();
                        series.add(time, netBalance);
                        System.out.println("Matched Date: " + accountDate + ", Net Balance: " + netBalance);
                        maxValue = Math.max(maxValue, netBalance);
                        minValue = Math.min(minValue, netBalance);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    series.add(time, 0);
                    System.out.println("Date: " + dateStr + ", Net Balance: 0");
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date " + dateStr + ": " + e.getMessage());
            }
        }

        if (series.getItemCount() == 0) {
            JLabel noDataLabel = new JLabel("無有效日期資料可顯示", SwingConstants.CENTER);
            noDataLabel.setFont(CHINESE_FONT);
            frame.add(noDataLabel);
            return;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxis,
                yAxis,
                dataset
        );

        chart.getTitle().setFont(CHINESE_FONT);
        chart.getLegend().setItemFont(CHINESE_FONT);

        XYPlot plot = chart.getXYPlot();
        DateAxis dateAxis = new DateAxis(xAxis);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy/MM/dd"));
        dateAxis.setLabelFont(CHINESE_FONT);
        dateAxis.setTickLabelFont(CHINESE_FONT);
        plot.setDomainAxis(dateAxis);

        plot.getRangeAxis().setLabelFont(CHINESE_FONT);
        plot.getRangeAxis().setTickLabelFont(CHINESE_FONT);
        if (maxValue > minValue) {
            plot.getRangeAxis().setRange(Math.min(minValue * 1.1, 0), maxValue * 1.1);
        } else {
            plot.getRangeAxis().setRange(-100, 100); // Default range
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        frame.add(chartPanel);
    }
}