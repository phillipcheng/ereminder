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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.cld.util.DateTimeUtil;
import org.cld.util.PropertiesUtil;
import org.cld.util.StringUtil;
import org.cld.taskmgr.TaskUtil;
import org.cld.stock.analyze.AnalyzeBase;
import org.cld.stock.analyze.AnalyzeConf;
import org.cld.stock.analyze.AnalyzeResult;
import org.cld.stock.analyze.SelectStrategyByStockTask;
import org.cld.stock.analyze.StockAnalyzePersistMgr;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockDataConfig;
import org.cld.stock.common.TradeHour;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StrategyResult;
import javax.swing.JList;
import javax.swing.JScrollPane;


public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected static Logger logger =  LogManager.getLogger(MainFrame.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static final String chartPropertiesFile = "C:\\mydoc\\myprojects\\ereminder\\Reminder\\stock\\stockchart\\src\\main\\resources\\chart.properties";
	private static final String KEY_ACONF="aconf.properties";
	private static final String KEY_STRATEGY="strategy.properties";
	private static final String BASE_MARKET_ID="baseMarketId";
	private static final String INTERVAL_UNIT="interval.unit";
	private static final String SYMBOL_ID="symbol.id";
	private static final String START_DT="start.dt";
	private static final String END_DT="end.dt";

	private PropertiesConfiguration pc;
	private String strategyFile;//full path file name of the strategy file
	private String strategyName;
	private AnalyzeConf aconf;
	
	private JSplitPane udResultSplitPane = new JSplitPane();
	private JSplitPane lrSplitPane = new JSplitPane();
	private JPanel indicatorsPanel = new JPanel();
	private JSplitPane udSplitPane1 = new JSplitPane(); //for the upper candlestick and lower indicators
	private SelectStrategy bs = null;
	
	List<DataChart> dclist = new ArrayList<DataChart>();
	StockDataConfig sdc = new StockDataConfig();
	DataConfigPanel dataConfigPanel = new DataConfigPanel();
	JList<String> arList = new JList<String>();
	DefaultListModel<String> arListModel = new DefaultListModel<String>();
	
	
	public MainFrame() {
		super();
		//init config
		try {
			pc = new PropertiesConfiguration(chartPropertiesFile);
			strategyFile = pc.getString(KEY_STRATEGY);
			setStrategy(strategyFile);
			aconf = (AnalyzeConf) TaskUtil.getTaskConf(pc.getString(KEY_ACONF));
			if (pc.containsKey(BASE_MARKET_ID)){
				sdc.setBaseMarketId(pc.getString(BASE_MARKET_ID));
			}
			if (pc.containsKey(INTERVAL_UNIT)){
				sdc.setUnit(IntervalUnit.valueOf(pc.getString(INTERVAL_UNIT)));
			}
			if (pc.containsKey(SYMBOL_ID)){
				sdc.setStockId(pc.getString(SYMBOL_ID));
			}
			if (pc.containsKey(START_DT)){
				String strStartDt = pc.getString(START_DT);
				String strEndDt = pc.getString(END_DT);
				try{
					if (strStartDt.contains(":")){
						sdc.setStartDt(msdf.parse(strStartDt));
						sdc.setEndDt(msdf.parse(strEndDt));
					}else{
						sdc.setStartDt(sdf.parse(strStartDt));
						sdc.setEndDt(sdf.parse(strEndDt));
					}
				}catch(Exception e){
					logger.error("", e);
				}
			}
			
		}catch(Exception e){
			logger.error("", e);
		}
		//init UI
		initialize();
	}
	
	public void setStrategy(String strategyFile){
		this.strategyFile = strategyFile;
		String sFileName = (new File(strategyFile)).getName();
		strategyName = StringUtil.getStringBetweenFirstPreLastPost(sFileName, AnalyzeBase.STRATEGY_PREFIX, AnalyzeBase.STRATEGY_SUFFIX);
	}
	
	public void resetBs(){
		PropertiesConfiguration strategyPC = PropertiesUtil.getPC(strategyFile);
		List<SelectStrategy> bsl = SelectStrategy.genList(strategyPC, strategyFile, sdc.getBaseMarketId(), aconf.getDbconf());
		if (bsl.size()>0){
			bs = bsl.get(0);
		}
	}

	private void initialize() {
		this.setSize(1071, 1021);
		initSplitPane();
		this.setContentPane(udResultSplitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		udResultSplitPane.setRightComponent(scrollPane);
		scrollPane.setViewportView(arList);
		this.setTitle("tt@cy");
		this.setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
	}
	
	public void close(){
		pc.setProperty(KEY_STRATEGY, strategyFile);
		pc.setProperty(BASE_MARKET_ID, sdc.getBaseMarketId());
		pc.setProperty(INTERVAL_UNIT, sdc.getUnit().name());
		pc.setProperty(SYMBOL_ID, sdc.getStockId());
		pc.setProperty(START_DT, sdc.getUnit()==IntervalUnit.day?sdf.format(sdc.getStartDt()):msdf.format(sdc.getStartDt()));
		pc.setProperty(END_DT, sdc.getUnit()==IntervalUnit.day?sdf.format(sdc.getEndDt()):msdf.format(sdc.getEndDt()));
		try {
			pc.save(new File(chartPropertiesFile));
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
	}
	
	private static final int CURRENT=0;
	private static final int PREVIOUS=1;
	private static final int NEXT=2;
	
	private void changeDate(int dir){
		sdc = dataConfigPanel.getDataConfig();
		if (dir==PREVIOUS){
			sdc.setStartDt(DateTimeUtil.yesterday(sdc.getStartDt()));
			sdc.setEndDt(DateTimeUtil.yesterday(sdc.getEndDt()));
		}else if (dir == NEXT){
			sdc.setStartDt(DateTimeUtil.tomorrow(sdc.getStartDt()));
			sdc.setEndDt(DateTimeUtil.tomorrow(sdc.getEndDt()));
		}
		dataConfigPanel.setDataConfig(sdc);
	}
	
	private void execute(int dir){
		changeDate(dir);
		sdc = dataConfigPanel.getDataConfig();
		List<CqIndicators> cqilist = null;
		TradeHour th = TradeHour.All;
		if (sdc.getUnit()==IntervalUnit.day){
			th = TradeHour.All;
		}else if (sdc.getUnit()==IntervalUnit.minute){
			th = TradeHour.Normal;
		}
		List<SelectStrategy> bsl = new ArrayList<SelectStrategy>();
		resetBs();
		//setup the indicators panel
		setupIndicatorsPanel(bs);
		bsl.add(bs);
		//get all buy opportunity
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(aconf, bsl, sdc.getStockId(), 
				sdc.getStartDt(), sdc.getEndDt(), th, null);
		resetBs();
		//get all cq data
		cqilist = StockAnalyzePersistMgr.getData(aconf, sdc, bs, th);
		List<Date> dl = new ArrayList<Date>();
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			dl.add(scr.getDt());
		}
		int maxPeriods = ChartUtil.getFirstValid(cqilist);
		cqilist = cqilist.subList(maxPeriods, cqilist.size());
		for (DataChart dc: dclist){
			dc.setData(cqilist, bs, sdc.getUnit(), dl);
		}
		//get all result
		String strStartDt = sdc.getUnit()==IntervalUnit.day?sdf.format(sdc.getStartDt()):msdf.format(sdc.getStartDt());
		String strEndDt = sdc.getUnit()==IntervalUnit.day?sdf.format(sdc.getEndDt()):msdf.format(sdc.getEndDt());
		AnalyzeResult ar = AnalyzeBase.validateStrategiesLocal(pc.getString(KEY_ACONF), sdc.getBaseMarketId(), strStartDt, strEndDt, 
				strategyName, th, AnalyzeBase.BY_STRATEGY, 1, sdc.getStockId());
		Map<String, StrategyResult> srmap = ar.getOrderedResultByStrategy();
		arListModel.clear();
		for (Map.Entry<String, StrategyResult> ssr:srmap.entrySet()){
			String entry = ssr.getValue() + "_" + ssr.getKey();
			arListModel.addElement(entry);
		}
	}
	
	private void setupIndicatorsPanel(SelectStrategy thisBs){
		int indPanelNum=0;
		if (thisBs!=null){
			indPanelNum = thisBs.getChartNum();
		}
		indicatorsPanel.setLayout(new BoxLayout(indicatorsPanel, BoxLayout.Y_AXIS));
		for (int i=0; i<indPanelNum; i++){
			TimeSeriesChart ts = TimeSeriesChart.createTimeSeriesChart();
			ts.setMyName(i+1+"");
			indicatorsPanel.add(ts);
			dclist.add(ts);
		}
		
		udSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		udSplitPane1.setResizeWeight(0.5);
		CandlestickChart cc = CandlestickChart.createCandlestickChart();
		cc.setMyName("0");
		udSplitPane1.setLeftComponent(cc);
		dclist.add(cc);
		udSplitPane1.setRightComponent(indicatorsPanel);
	}
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private void initSplitPane() {
		//left
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setPreferredSize(new Dimension(230, 100));
		
		dataConfigPanel.setDataConfig(sdc);
		dataConfigPanel.setMf(this);
		dataConfigPanel.getStrategyFileChooser().setCurrentDirectory((new File(strategyFile)).getParentFile());
		dataConfigPanel.getBtnChooseStrategy().setText((new File(strategyFile)).getName());
		dataConfigPanel.setLocation(10, 11);
		leftPanel.add(dataConfigPanel);
		//ok
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute(CURRENT);
			}
		});
		btnOk.setBounds(14, 206, 53, 23);
		leftPanel.add(btnOk);
		//next
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute(NEXT);
			}
		});
		btnNext.setBounds(155, 206, 68, 23);
		leftPanel.add(btnNext);
		//prev
		JButton btnPrev = new JButton("Prev");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute(PREVIOUS);
			}
		});
		btnPrev.setBounds(77, 206, 68, 23);
		leftPanel.add(btnPrev);
		//right
		setupIndicatorsPanel(null);
		
		lrSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		lrSplitPane.setOneTouchExpandable(true);
		lrSplitPane.setContinuousLayout(true);
		lrSplitPane.setLeftComponent(leftPanel);
		
		arList.setModel(arListModel);
		lrSplitPane.setRightComponent(udSplitPane1);
		
		udResultSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		udResultSplitPane.setLeftComponent(lrSplitPane);
		udResultSplitPane.setResizeWeight(0.8);
			
	}

	public static void main(String arg[]){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final MainFrame frame = new MainFrame();
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
