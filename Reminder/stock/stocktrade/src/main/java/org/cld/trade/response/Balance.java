package org.cld.trade.response;

import java.util.Map;

public class Balance {
	
	private float accountValue;
	private float cash;
	private BuyingPower buyingPower;
	
	public static final String ACCOUNTVALUE="accountvalue";
	public static final String MONEY="money";
	public static final String CASH="cash";
	public static final String buyingpower_="buyingpower";
	
	public Balance(float accountValue, float cash, BuyingPower bp){
		this.accountValue = accountValue;
		this.cash = cash;
		this.buyingPower = bp;
	}
	
	public Balance(Map<String, Object> map){
		accountValue = Float.parseFloat((String) map.get(ACCOUNTVALUE));
		Map<String, Object> moneyMap = (Map<String, Object>) map.get(MONEY);
		cash = Float.parseFloat((String) moneyMap.get(CASH));
		Map<String, Object> buyingPowerMap = (Map<String, Object>) map.get(buyingpower_);
		setBuyingPower(new BuyingPower(buyingPowerMap));
	}
	
	public String toString(){
		return String.format("Balance:accountValue:%.2f,cash:%.2f,buyingpower:%s", accountValue, cash, buyingPower);
	}

	public boolean canBuy(float amount){
		if (accountValue + cash<0){
			return false;
		}
		float stockBuyPower = buyingPower.getStock();
		if (stockBuyPower > amount){
			return true;
		}else{
			return true;
		}
	}
	
	public float getAccountValue() {
		return accountValue;
	}

	public void setAccountValue(float accountValue) {
		this.accountValue = accountValue;
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public BuyingPower getBuyingPower() {
		return buyingPower;
	}

	public void setBuyingPower(BuyingPower buyingPower) {
		this.buyingPower = buyingPower;
	}
}
