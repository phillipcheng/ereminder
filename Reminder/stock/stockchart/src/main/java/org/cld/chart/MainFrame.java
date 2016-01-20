package org.cld.chart;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.cld.util.PropertiesUtil;
import org.cld.taskmgr.TaskUtil;

import org.cld.stock.analyze.AnalyzeConf;
import org.cld.stock.analyze.SelectStrategyByStockTask;
import org.cld.stock.analyze.StockAnalyzePersistMgr;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockDataConfig;
import org.cld.stock.common.TradeHour;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;


public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected static Logger logger =  LogManager.getLogger(MainFrame.class);
	private static final String KEY_CCONF="cconf.properties";
	private static final String KEY_STRATEGY="strategy.properties";
	
	private JSplitPane lrSplitPane = null;
	private String chartPropertiesFile = "C://mydoc//myprojects//ereminder//Reminder//stock//stockchart//src//main//resources//chart.properties";
	private AnalyzeConf aconf;
	private SelectStrategy bs = null;
	private String baseMarketId="nasdaq";
	private String strategyFile;//full path file name of the strategy file
	private PropertiesConfiguration pc;
	
	private int indPanelNum=2;
	List<DataChart> dclist = new ArrayList<DataChart>();
	
	public MainFrame() {
		super();
		//init config
		try {
			pc = new PropertiesConfiguration(chartPropertiesFile);
			String pFile = pc.getString(KEY_CCONF);
			String strategyFile = pc.getString(KEY_STRATEGY);
			aconf = (AnalyzeConf) TaskUtil.getTaskConf(pFile);
			setStrategy(strategyFile);
		}catch(Exception e){
			logger.error("", e);
		}
		//init UI
		initialize();
	}
	
	public void setStrategy(String strategyFile){
		this.strategyFile = strategyFile;
		resetBs();
	}
	
	public void resetBs(){
		PropertiesConfiguration strategyPC = PropertiesUtil.getPC(strategyFile);
		List<SelectStrategy> bsl = SelectStrategy.genList(strategyPC, strategyFile, baseMarketId, aconf.getDbconf());
		if (bsl.size()>0){
			bs = bsl.get(0);
		}
	}

	private void initialize() {
		this.setSize(1071, 1021);
		this.setContentPane(getJSplitPane());
		this.setTitle("tt@cy");
		this.setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
	}
	
	public void close(){
		pc.setProperty(KEY_STRATEGY, strategyFile);
		try {
			pc.save(new File(chartPropertiesFile));
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
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
			dataConfigPanel.setMf(this);
			dataConfigPanel.getStrategyFileChooser().setCurrentDirectory((new File(strategyFile)).getParentFile());
			dataConfigPanel.getBtnChooseStrategy().setText((new File(strategyFile)).getName());
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
					List<SelectStrategy> bsl = new ArrayList<SelectStrategy>();
					resetBs();
					bsl.add(bs);
					List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, bsl, sdcfg.getStockId(), 
							sdcfg.getStartDt(), sdcfg.getEndDt(), th, null);
					resetBs();
					cqilist = StockAnalyzePersistMgr.getData(aconf, sdcfg, bs, th);
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
				MainFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				frame.addWindowListener(new WindowAdapter() {
				    @Override
				    public void windowClosing(WindowEvent windowEvent) {
				        frame.close();
				    }
				});
			}
		});
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
