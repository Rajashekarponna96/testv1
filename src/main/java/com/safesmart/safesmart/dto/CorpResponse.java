package com.safesmart.safesmart.dto;

import java.util.List;

public class CorpResponse {
	
    private Long id;
    
    private String corpName;
	
	private String description;
	
    private boolean status;
    
    private String cityName;
	
    private String zipCode;
		
	 private String stateName;
		
	 private String streetName;
	 
	 private List<Long> storeInfoId;
	  
	 private int locations;
	 
	 private int todayInsertBillsAmount;
	 
	 private int allCorpsTodayInsertBillsAmount;
	 
	 private List<StoreInfoResponse> storeInfoResponse;
	 
	 private int insertBillAmount;
		
	 private int userSafeAmount;
		
	 private int storeSafeAmount;
		
	 private int corpSafeAmount;
		
	 private int allLocationAllSafeAmount;

	 
	 public Long getId() {
		return id;
	}

	public CorpResponse() {
		// TODO Auto-generated constructor stub
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	

	
	

	public List<Long> getStoreInfoId() {
		return storeInfoId;
	}

	public void setStoreInfoId(List<Long> storeInfoId) {
		this.storeInfoId = storeInfoId;
	}

	public int getLocations() {
		return locations;
	}

	public void setLocations(int locations) {
		this.locations = locations;
	}

	public int getTodayInsertBillsAmount() {
		return todayInsertBillsAmount;
	}

//	public List<Integer> getLocations() {
//		return locations;
//	}
//
//	public void setLocations(List<Integer> locations) {
//		this.locations = locations;
//	}

	public void setTodayInsertBillsAmount(int todayInsertBillsAmount) {
		this.todayInsertBillsAmount = todayInsertBillsAmount;
	}

	public List<StoreInfoResponse> getStoreInfoResponse() {
		return storeInfoResponse;
	}

	public void setStoreInfoResponse(List<StoreInfoResponse> storeInfoResponse) {
		this.storeInfoResponse = storeInfoResponse;
	}

	public int getAllCorpsTodayInsertBillsAmount() {
		return allCorpsTodayInsertBillsAmount;
	}

	public void setAllCorpsTodayInsertBillsAmount(int allCorpsTodayInsertBillsAmount) {
		this.allCorpsTodayInsertBillsAmount = allCorpsTodayInsertBillsAmount;
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

	public int getAllLocationAllSafeAmount() {
		return allLocationAllSafeAmount;
	}

	public void setAllLocationAllSafeAmount(int allLocationAllSafeAmount) {
		this.allLocationAllSafeAmount = allLocationAllSafeAmount;
	}
	
	
	

	
//	@Override
//	public String toString() {
//		return "CorpResponse [id=" + id + ", corpName=" + corpName + ", description=" + description + ", status="
//				+ status + ", cityName=" + cityName + ", zipCode=" + zipCode + ", stateName=" + stateName
//				+ ", streetName=" + streetName + "]";
//	}
//
//	
	 
	 

}
