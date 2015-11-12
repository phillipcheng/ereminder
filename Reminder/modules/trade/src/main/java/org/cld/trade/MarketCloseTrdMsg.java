package org.cld.trade;

public class MarketCloseTrdMsg extends TradeMsg {

	public MarketCloseTrdMsg() {
		super(TradeMsgType.marketCloseSoon);
	}

	/**
	 *  [market will close]
	 * 		|__ has position
	 * 				|__ duration met: cancel sell order, submit sell on close order, clean msgs
	 * 				|__ duration not met: do nothing (open sell order, [monitor stop trailing order, monitor price cross])
	 *      |__ no position, do nothing
	 */
	@Override
	public TradeMsgPR process(TradeMgr tm) {
		// TODO Auto-generated method stub
		return null;
	}



}
