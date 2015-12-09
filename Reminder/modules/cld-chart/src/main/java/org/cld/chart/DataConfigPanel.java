package org.cld.chart;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;

import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockDataConfig;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import org.cld.stock.strategy.IntervalUnit;

public class DataConfigPanel extends JPanel {

	protected static Logger logger =  LogManager.getLogger(DataConfigPanel.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final long serialVersionUID = 1L;

	private JLabel lblStockId;
	private JTextField txtStockId;
	private JLabel lblInterval;
	private JComboBox cbUnit = null;
	private JLabel lblStart;
	private JTextField tfStartDt = null;
	private JLabel lblEnd;
	private JTextField tfEndDt = null;

	private StockDataConfig dc;  //  @jve:decl-index=0:
	private JTextField txtBaseMarketId;


	/**
	 * This is the default constructor
	 */
	public DataConfigPanel() {
		super();
		initialize();
	}
	
	public StockDataConfig getData() {
		dc = new StockDataConfig();
		dc.setStockId(txtStockId.getText());
		dc.setBaseMarketId(txtBaseMarketId.getText());
		dc.setUnit((IntervalUnit) cbUnit.getSelectedItem());
		try {
			if (dc.getUnit()==IntervalUnit.day){
				dc.setStartDt(sdf.parse(tfStartDt.getText()));
				dc.setEndDt(sdf.parse(tfEndDt.getText()));
			}else{
				dc.setStartDt(msdf.parse(tfStartDt.getText()));
				dc.setEndDt(msdf.parse(tfEndDt.getText()));
			}
		}catch(Exception e){
			logger.error("", e);
		}
		
		return dc;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		lblInterval = new JLabel();
		lblInterval.setBounds(new Rectangle(0, 61, 60, 25));
		lblInterval.setText("Unit:");
		lblEnd = new JLabel();
		lblEnd.setBounds(new Rectangle(0, 112, 46, 22));
		lblEnd.setText("EndDt:");
		lblStart = new JLabel();
		lblStart.setBounds(new Rectangle(0, 88, 46, 22));
		lblStart.setText("StartDt:");
		lblStockId = new JLabel();
		lblStockId.setText("StockId:");
		lblStockId.setBounds(new Rectangle(0, 34, 60, 25));
		this.setSize(198, 160);
		this.setLayout(null);
		this.add(lblStockId, null);
		this.add(lblStart, null);
		this.add(getTfStartDt(), null);
		this.add(lblEnd, null);
		this.add(getTfEndDt(), null);
		this.add(lblInterval, null);
		this.add(getCbUnit(), null);
		
		txtStockId = new JTextField();
		txtStockId.setBounds(70, 36, 86, 20);
		add(txtStockId);
		txtStockId.setColumns(10);
		
		JLabel lblBaseMarketId = new JLabel();
		lblBaseMarketId.setText("BaseMarketId:");
		lblBaseMarketId.setBounds(new Rectangle(0, 34, 60, 25));
		lblBaseMarketId.setBounds(0, 11, 70, 25);
		add(lblBaseMarketId);
		
		txtBaseMarketId = new JTextField();
		txtBaseMarketId.setColumns(10);
		txtBaseMarketId.setBounds(70, 13, 86, 20);
		add(txtBaseMarketId);
	}

	/**
	 * This method initializes tfSD	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTfStartDt() {
		if (tfStartDt == null) {
			tfStartDt = new JTextField();
			tfStartDt.setBounds(new Rectangle(70, 88, 123, 22));
			tfStartDt.setText("2011-04-06");
			tfStartDt.setName("tfType");
		}
		return tfStartDt;
	}

	/**
	 * This method initializes tfED	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTfEndDt() {
		if (tfEndDt == null) {
			tfEndDt = new JTextField();
			tfEndDt.setBounds(new Rectangle(70, 112, 123, 22));
			tfEndDt.setText("2011-04-07");
			tfEndDt.setName("tfType");
		}
		return tfEndDt;
	}

	/**
	 * This method initializes cbIndustry	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCbUnit() {
		if (cbUnit == null) {
			cbUnit = new JComboBox();
			cbUnit.setModel(new DefaultComboBoxModel(IntervalUnit.values()));
			cbUnit.setBounds(new Rectangle(70, 61, 116, 25));
		}
		return cbUnit;
	}
}  //  @jve:decl-index=0:visual-constraint="4,5"
