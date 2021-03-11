package io.flex.UserInterface;

import io.flex.business.MarketDataEngine;
import io.flex.business.OptionsRiskEngine;
import io.flex.commons.Instrument;
import io.flex.commons.Portfolio;
import io.flex.commons.Position;
import io.flex.tda.TDAClient;
import org.jfree.chart.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

import static com.studerw.tda.model.option.Option.PutCall.CALL;

public class Controls extends JFrame {
    private JSlider slider1;
    private JPanel panel1;
    private JTable table1;
    private JPanel graphPanel;


    private final TDAClient tdaClient;

    private ChartPanel chartPanel;

    private JFreeChart chart;

    private XYDataset chart_dataset;

    private String viewingSymbol = null;

    private final MarketDataEngine mdEngine;

    private final OptionsRiskEngine riskEngine;

    private Portfolio portfolio = new Portfolio();

    private Instrument currentMasterInstrument = null;

    private ValueMarker domainMarker;

    private ValueMarker cursorMarker;

    private JSlider volSlider;
    private JLabel vol_label;

    private JLabel volLabel;

    private OptionsTable optionsTable = new OptionsTable(portfolio);

    private JScrollPane scrollPane = new JScrollPane(optionsTable);




    private void __buildPortfolio() {
        String expString = "2020-11-23:16";
        String expString2 = "2020-11-23:16";
        //String sym = "$SPX.X";
        String sym = "SPY";
        Instrument o1 = Instrument.createOptionInstrument(tdaClient, sym, 329, expString, CALL);
        Instrument o2 = Instrument.createOptionInstrument(tdaClient, sym, 339, expString, CALL);
        Instrument o3 = Instrument.createOptionInstrument(tdaClient, sym, 348, expString, CALL);
        Instrument o4 = Instrument.createOptionInstrument(tdaClient, sym, 349, expString, CALL);

        Instrument o5 = Instrument.createOptionInstrument(tdaClient, sym, 330, expString, CALL);
        Instrument o6 = Instrument.createOptionInstrument(tdaClient, sym, 330, expString2, CALL);
        Instrument o7 = Instrument.createOptionInstrument(tdaClient, sym, 336, expString, CALL);
        Instrument o8 = Instrument.createOptionInstrument(tdaClient, sym, 341, expString, CALL);

        ArrayList<Position> positions = new ArrayList<>();
        positions.add(new Position(o1, 10));
        positions.add(new Position(o2, -20));
        positions.add(new Position(o3, 10));

        positions.add(new Position(o4, 1));

//        positions.add(new Position(o5, -10));
//        positions.add(new Position(o6, 10));


        this.portfolio.put(sym, positions);
        this.currentMasterInstrument = o1;
    }




    public Controls() {
        this.setSize(1600, 900);
        this.add(panel1);

        this.tdaClient = new TDAClient();
        this.__buildPortfolio();

        this.mdEngine = new MarketDataEngine(this.tdaClient, this::updateCharts);
        this.mdEngine.portfolio = this.portfolio;       // setting to this generated portfolio
        this.riskEngine = new OptionsRiskEngine(this.mdEngine.portfolio, 0.1);
        this.riskEngine.positions = this.portfolio.get(this.portfolio.firstSymbol);






        //this.chart_dataset = createDataset();
        this.chart = createChart(this.chart_dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(1300,600));

        //chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        cursorMarker = new ValueMarker(320);
        chart.getXYPlot().addDomainMarker(cursorMarker);

        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                cursorMarker.setValue(chartMouseEvent.getChart().getXYPlot().getDomainCrosshairValue());
                System.out.println(chartMouseEvent.getChart().getXYPlot().getDomainCrosshairValue());
                chartPanel.repaint();
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {

            }
        });

        domainMarker = new ValueMarker(this.currentMasterInstrument.underlyingMark);
        this.chart.getXYPlot().addDomainMarker(domainMarker);
        this.chart.getXYPlot().addRangeMarker(new ValueMarker(0));

        this.graphPanel.add(chartPanel);



        this.setVisible(true);
        this.mdEngine.start();
    }


    private JFreeChart createChart(XYDataset dataset) {
        chart = ChartFactory.createXYLineChart(
                "Average salary per age",
                "Underlying Price",
                "PnL",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(0.2f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(0.7f));
        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesStroke(2, new BasicStroke(0.2f));
        renderer.setSeriesPaint(3, Color.yellow);
        renderer.setSeriesStroke(3, new BasicStroke(0.7f));
        renderer.setSeriesPaint(4, Color.pink);
        renderer.setSeriesStroke(4, new BasicStroke(0.7f));
        renderer.setSeriesPaint(5, Color.ORANGE);
        renderer.setSeriesStroke(5, new BasicStroke(0.2f));
        renderer.setSeriesPaint(6, Color.MAGENTA);
        renderer.setSeriesStroke(6, new BasicStroke(0.7f));
        renderer.setSeriesPaint(7, Color.CYAN);
        renderer.setSeriesStroke(7, new BasicStroke(0.7f));



        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("SPY",
                        new Font("San", java.awt.Font.BOLD, 18)
                )
        );


        return chart;
    }


    private void updateCharts(Portfolio portfolio) {
        Map<Double, OptionsRiskEngine.Payload> riskGraph = riskEngine.getRiskGraphToday();
        XYSeries series = new XYSeries("T+"+riskEngine.eval_days_from_now);

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph.entrySet()) {
            series.add(entry.getKey(), (Double) entry.getValue().getTheoPrice());
        }
        Map<Double, OptionsRiskEngine.Payload> riskGraph2 = riskEngine.getRiskGraphAfterEvalDate(2);
        XYSeries series2 = new XYSeries("T+"+(riskEngine.eval_days_from_now+2));

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph2.entrySet()) {
            series2.add(entry.getKey(), (Double) entry.getValue().getTheoPrice());
        }
        Map<Double, OptionsRiskEngine.Payload> riskGraph3 = riskEngine.getRiskGraphAfterEvalDate(4);
        XYSeries series3 = new XYSeries("T+"+(riskEngine.eval_days_from_now+4));

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph3.entrySet()) {
            series3.add(entry.getKey(), (Double) entry.getValue().getTheoPrice());
        }

        XYSeries seriesDelta = new XYSeries("Delta");

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph.entrySet()) {
            if (!Double.isNaN(entry.getValue().getDelta())) {
                //System.out.println(entry.getValue().getDelta());
                seriesDelta.add(entry.getKey(), (Double) entry.getValue().getDelta());
            }
        }

        XYSeries seriesTheta = new XYSeries("Theta");

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph.entrySet()) {
            if (!Double.isNaN(entry.getValue().getTheta()))
                seriesTheta.add(entry.getKey(), (Double) entry.getValue().getTheta());
        }

        XYSeries seriesGamma = new XYSeries("Gamma");

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph.entrySet()) {
            if (!Double.isNaN( entry.getValue().getGamma()))
                seriesGamma.add(entry.getKey(), (Double) entry.getValue().getGamma());
        }

        XYSeries seriesVega = new XYSeries("Vega");

        for (Map.Entry<Double, OptionsRiskEngine.Payload> entry : riskGraph.entrySet()) {
            if (!Double.isNaN( entry.getValue().getVega()))
                seriesVega.add(entry.getKey(), (Double) entry.getValue().getVega());
        }

        Map<Double, Double> expRiskGraph = riskEngine.getRiskGraphExpiration(riskEngine.positions);
        XYSeries seriesExp = new XYSeries("Expiration");

        for (Map.Entry<Double, Double> entry : expRiskGraph.entrySet()) {
            seriesExp.add(entry.getKey(), entry.getValue());
        }

        domainMarker.setValue(this.currentMasterInstrument.underlyingMark);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesExp);
        dataset.addSeries(series);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
//        dataset.addSeries(seriesDelta);
//        dataset.addSeries(seriesGamma);
//        dataset.addSeries(seriesTheta);
//        dataset.addSeries(seriesVega);
        this.chart_dataset = dataset;
        this.chart.setNotify(true);
        this.chart.getXYPlot().setDataset(this.chart_dataset);
        this.chartPanel.repaint();

        this.optionsTable = new OptionsTable(portfolio);
        scrollPane.setViewportView(this.optionsTable);
        scrollPane.repaint();
        System.out.print(".");
    }



    public static void main(String [] args) {
        new Controls().setVisible(true);
    }

    private void createUIComponents() {
        this.graphPanel = new JPanel();
        this.slider1 = new JSlider();
        this.slider1.setMinimum(-15);
        this.slider1.setMaximum(15);

        this.slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                //   updateCharts(portfolio);
                vol_label.setText("IV +/- :"+ Integer.toString(slider1.getValue()) + "%");
                riskEngine.vol_offset = slider1.getValue()*0.01;
            }
        });



        //this.vol_label = new JLabel();
    }
}
