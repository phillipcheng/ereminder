package org.cld.trade.response;

import java.util.Map;

public class Balance {
	
	float accountValue;
	float cash;
	
	public static final String ACCOUNTVALUE="accountvalue";
	public static final String MONEY="money";
	public static final String CASH="cash";
	
	public Balance(Map<String, Object> map){
		accountValue = Float.parseFloat((String) map.get(ACCOUNTVALUE));
		Map<String, Object> moneyMap = (Map<String, Object>) map.get(MONEY);
		cash = Float.parseFloat((String) moneyMap.get(CASH));
	}
	
	public String toString(){
		return String.format("A:%.2f,%.2f", accountValue, cash);
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
	
	
}
