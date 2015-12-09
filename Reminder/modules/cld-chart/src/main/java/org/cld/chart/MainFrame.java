package org.cld.chart;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.GridBagLayout;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.CqIndicators;
import org.cld.stock.StockDataConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectStrategy;

import javax.swing.JButton;

public class MainFrame extends JFrame implements ActionListener, ListSelectionListener{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MainFrame.class);

	private JSplitPane lrSplitPane = null;
	private CandlestickChart csChart = null;
	private TimeSeriesChart tsChart = null;
	private HistogramChart hsChart = null;
	
	private String pFile = "client1-v2.properties";
	private String strategyFile = "strategy.macd.properties";
	private CrawlConf cconf;
	private SelectStrategy bs = null;
	private String baseMarketId="nasdaq";
	
	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
		cconf = CrawlTestUtil.getCConf(pFile);
		try {
			PropertiesConfiguration pc = new PropertiesConfiguration(strategyFile);
			List<SelectStrategy> bsl = SelectStrategy.gen(pc, strategyFile, baseMarketId);
			if (bsl.size()>0){
				bs = bsl.get(0);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 * @throws Exception 
	 */
	private void initialize() {
		
		this.setSize(1071, 1021);
		this.setContentPane(getJSplitPane());
		this.setTitle("tt@cy");
		
		this.setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		
	}

	/**
	 * This method initializes pRight	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCandleStickChart() {
		if (csChart == null) {
			csChart = CandlestickChart.createCandlestickChart();
			//csChart.setLayout(new GridBagLayout());
			csChart.setName("rightPanel");
			csChart.setPreferredSize(new Dimension(500, 600));
		}
		return csChart;
	}
	
	private JPanel getTimeSeriesChart(){
		if (tsChart == null){
			tsChart = TimeSeriesChart.createTimeSeriesChart();
			tsChart.setPreferredSize(new Dimension(500, 250));
		}
		return tsChart;
	}
	
	private JPanel getHistogramChart(){
		if (hsChart == null){
			hsChart = HistogramChart.createHistogramChart();
			hsChart.setPreferredSize(new Dimension(500, 80));
		}
		return hsChart;
	}
	
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (lrSplitPane == null) {
			//left
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(null);
			leftPanel.setPreferredSize(new Dimension(230, 100));
			final DataConfigPanel dataConfigPanel = new DataConfigPanel();
			dataConfigPanel.setLocation(10, 11);
			leftPanel.add(dataConfigPanel);
			
			JButton btnOk = new JButton("OK");
			btnOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StockDataConfig sdcfg = dataConfigPanel.getData();
					List<CqIndicators> cqlist = StockUtil.getData(cconf, sdcfg, bs);
					int maxPeriods = bs.getMaxPeriod();
					cqlist = cqlist.subList(maxPeriods, cqlist.size());
					csChart.setData(cqlist);
					tsChart.setData(cqlist, bs);
					hsChart.setData(cqlist, bs);
				}
			});
			btnOk.setBounds(10, 182, 89, 23);
			leftPanel.add(btnOk);
			
			//right
			JSplitPane udSplitPane2 = new JSplitPane();
			udSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			udSplitPane2.setLeftComponent(getTimeSeriesChart());
			udSplitPane2.setRightComponent(getHistogramChart());
			
			JSplitPane udSplitPane1 = new JSplitPane();
			udSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			udSplitPane1.setLeftComponent(getCandleStickChart());
			udSplitPane1.setRightComponent(udSplitPane2);
			
			lrSplitPane = new JSplitPane();
			lrSplitPane.setOneTouchExpandable(true);
			lrSplitPane.setContinuousLayout(true);
			lrSplitPane.setLeftComponent(leftPanel);
			lrSplitPane.setRightComponent(udSplitPane1);
			
			
		}
		return lrSplitPane;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		logger.info(e.getSource().toString());
		
	}

	public static void main(String arg[]){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainFrame thisClass = new MainFrame();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
