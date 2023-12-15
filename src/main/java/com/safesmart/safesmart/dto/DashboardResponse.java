package com.safesmart.safesmart.dto;

public class DashboardResponse {
	
	private int allCorpsTodayInsertBillsAmount;
	
	private int insertBillAmount;
	
	private int userSafeAmount;
	
	private int storeSafeAmount;
	
	private int corpSafeAmount;
	
	
	private int allLocationAllSafeAmount;

	public int getAllCorpsTodayInsertBillsAmount() {
		return allCorpsTodayInsertBillsAmount;
	}

	public void setAllCorpsTodayInsertBillsAmount(int allCorpsTodayInsertBillsAmount) {
		this.allCorpsTodayInsertBillsAmount = allCorpsTodayInsertBillsAmount;
	}

	public int getAllLocationAllSafeAmount() {
		return allLocationAllSafeAmount;
	}

	public void setAllLocationAllSafeAmount(int allLocationAllSafeAmount) {
		this.allLocationAllSafeAmount = allLocationAllSafeAmount;
	}

	public int getInsertBillAmount() {
		return insertBillAmount;
	}

	public void setInsertBillAmount(int insertBillAmount) {
		this.insertBillAmount = insertBillAmount;
	}

	public int getUserSafeAmount() {
		return userSafeAmount;
	}

	public void setUserSafeAmount(int userSafeAmount) {
		this.userSafeAmount = userSafeAmount;
	}

	public int getStoreSafeAmount() {
		return storeSafeAmount;
	}

	public void setStoreSafeAmount(int storeSafeAmount) {
		this.storeSafeAmount = storeSafeAmount;
	}

	public int getCorpSafeAmount() {
		return corpSafeAmount;
	}

	public void setCorpSafeAmount(int corpSafeAmount) {
		this.corpSafeAmount = corpSafeAmount;
	}
	
	
	

}
