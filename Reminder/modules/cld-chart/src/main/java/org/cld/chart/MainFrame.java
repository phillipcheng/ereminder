package org.cld.chart;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.CqIndicators;
import org.cld.stock.StockDataConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.TradeHour;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SelectStrategyByStockTask;
import org.cld.util.PropertiesUtil;

import javax.swing.BoxLayout;
import javax.swing.JButton;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MainFrame.class);
	private static final String KEY_CCONF="cconf.properties";
	private static final String KEY_STRATEGY="strategy.properties";
	
	private JSplitPane lrSplitPane = null;
	private String chartPropertiesFile = "chart.properties";
	private CrawlConf cconf;
	private SelectStrategy bs = null;
	private String baseMarketId="nasdaq";
	
	private int indPanelNum=2;
	List<DataChart> dclist = new ArrayList<DataChart>();
	
	public MainFrame() {
		super();
		initialize();
		try {
			PropertiesConfiguration pc = new PropertiesConfiguration(chartPropertiesFile);
			String pFile = pc.getString(KEY_CCONF);
			String strategyFile = pc.getString(KEY_STRATEGY);
			cconf = CrawlTestUtil.getCConf(pFile);
			PropertiesConfiguration strategyPC = PropertiesUtil.getPC(strategyFile);
			List<SelectStrategy> bsl = SelectStrategy.gen(strategyPC, strategyFile, baseMarketId);
			if (bsl.size()>0){
				bs = bsl.get(0);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	private void initialize() {
		this.setSize(1071, 1021);
		this.setContentPane(getJSplitPane());
		this.setTitle("tt@cy");
		this.setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
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
					List<CqIndicators> cqilist = null;
					TradeHour th = TradeHour.All;
					if (sdcfg.getUnit()==IntervalUnit.day){
						th = TradeHour.All;
					}else if (sdcfg.getUnit()==IntervalUnit.minute){
						th = TradeHour.Normal;
					}
					cqilist = StockUtil.getData(cconf, sdcfg, bs, th);
					List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(cconf, new SelectStrategy[]{bs}, sdcfg.getStockId(), 
							sdcfg.getStartDt(), sdcfg.getEndDt(), th, null);
					List<Date> dl = new ArrayList<Date>();
					for (Object[] kv:kvl){
						SelectCandidateResult scr = (SelectCandidateResult) kv[0];
						dl.add(scr.getDt());
					}
					int maxPeriods = ChartUtil.getFirstValid(cqilist);
					cqilist = cqilist.subList(maxPeriods, cqilist.size());
					for (DataChart dc: dclist){
						dc.setData(cqilist, bs, sdcfg.getUnit(), dl);
					}
				}
			});
			btnOk.setBounds(10, 199, 89, 23);
			leftPanel.add(btnOk);
			
			//right
			JPanel multiPanel = new JPanel();
			multiPanel.setLayout(new BoxLayout(multiPanel, BoxLayout.Y_AXIS));
			for (int i=0; i<indPanelNum; i++){
				TimeSeriesChart ts = TimeSeriesChart.createTimeSeriesChart();
				ts.setMyName(i+1+"");
				multiPanel.add(ts);
				dclist.add(ts);
			}
			
			JSplitPane udSplitPane1 = new JSplitPane();
			udSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			udSplitPane1.setDividerLocation(0.5);
			CandlestickChart cc = CandlestickChart.createCandlestickChart();
			cc.setMyName("0");
			udSplitPane1.setLeftComponent(cc);
			dclist.add(cc);
			udSplitPane1.setRightComponent(multiPanel);
			
			lrSplitPane = new JSplitPane();
			lrSplitPane.setOneTouchExpandable(true);
			lrSplitPane.setContinuousLayout(true);
			lrSplitPane.setLeftComponent(leftPanel);
			lrSplitPane.setRightComponent(udSplitPane1);
			
			
		}
		return lrSplitPane;
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
