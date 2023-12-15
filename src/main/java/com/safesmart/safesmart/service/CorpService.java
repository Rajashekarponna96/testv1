package com.safesmart.safesmart.service;

import java.awt.desktop.UserSessionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.safesmart.safesmart.builder.CorpBuilder;
import com.safesmart.safesmart.builder.StoreInfoBuilder;
import com.safesmart.safesmart.common.CommonException;
import com.safesmart.safesmart.common.CommonExceptionMessage;
import com.safesmart.safesmart.controller.InsertBillController;
import com.safesmart.safesmart.dto.CorpRequest;
import com.safesmart.safesmart.dto.CorpResponse;
import com.safesmart.safesmart.dto.DashboardResponse;
import com.safesmart.safesmart.dto.EODReport;
import com.safesmart.safesmart.dto.InsertBillResponse;
import com.safesmart.safesmart.dto.PrinterRequest;
import com.safesmart.safesmart.dto.PrinterResponse;
import com.safesmart.safesmart.dto.StoreInfoResponse;
import com.safesmart.safesmart.model.ActionStatus;
import com.safesmart.safesmart.model.Corp;
import com.safesmart.safesmart.model.InsertBill;
import com.safesmart.safesmart.model.Printer;
import com.safesmart.safesmart.model.StoreInfo;
import com.safesmart.safesmart.model.UserInfo;
import com.safesmart.safesmart.remoterepository.Remote_CorpRepository;
import com.safesmart.safesmart.remoterepository.Remote_PrinterRepository;
import com.safesmart.safesmart.repository.CorpRepository;
import com.safesmart.safesmart.repository.InsertBillRepository;
import com.safesmart.safesmart.repository.PrinterRepository;
import com.safesmart.safesmart.repository.StoreInfoRepository;
import com.safesmart.safesmart.repository.UserInfoRepository;

@Service
@Transactional
public class CorpService {

	@Autowired
	private CorpRepository corpRepository;
	@Autowired
	private Remote_CorpRepository remote_CorpRepository;
	@Autowired
	private CorpBuilder corpbuilder;
	@Autowired
	private StoreInfoRepository storeinforepository;

	@Autowired
	private DashBoardService dashBoardService;

	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private InsertBillRepository insertBillRepository;
	@Autowired
	private StoreInfoService storeInfoService;

	@Autowired
	private UserService userService;

	@Autowired
	private StoreInfoBuilder storeInfoBuilder;

	public void add(CorpRequest corpRequest) {

		Corp corp = corpRepository.findByCorpName(corpRequest.getCorpName());
		if (corp != null) {
			throw CommonException.CreateException(CommonExceptionMessage.ALREADY_EXISTS, "CorpName");
		}

		Corp corp2 = new Corp();

		corp2.setCropName(corpRequest.getCorpName());
		corp2.setDescription(corpRequest.getDescription());
		corp2.setStatus(corpRequest.getStatus());
		corp2.setStateName(corpRequest.getStateName());
		corp2.setCityName(corpRequest.getCityName());
		corp2.setStreetName(corpRequest.getStreetName());
		corp2.setZipCode(corpRequest.getZipCode());

		corp2.setActionStatus(ActionStatus.Created);

		corpRepository.save(corp2);

		// server
//	    Corp corp1 = remote_CorpRepository.findByCorpName(corpRequest.getCorpName());
//		if (corp1 != null) {
//			throw CommonException.CreateException(CommonExceptionMessage.ALREADY_EXISTS, "CorpName");
//		}
//        corp1 = new Corp();
//        corp1.setId(longValue);
//        corp1.setCropName(corpRequest.getCorpName());
//        corp1.setDescription(corpRequest.getDescription());
//        corp1.setStatus(corpRequest.getStatus());
//        corp1.setStateName(corpRequest.getStateName());
//        corp1.setStreetName(corpRequest.getStreetName());
//        corp1.setCityName(corpRequest.getCityName());
//        corp1.setZipCode(corpRequest.getZipCode());
//	    
//	    remote_CorpRepository.save(corp1);
	}

	public static long uuidToLong(UUID uuid) {
		long mostSigBits = uuid.getMostSignificantBits();
		long leastSigBits = uuid.getLeastSignificantBits();
		long combinedValue = mostSigBits ^ leastSigBits;
		return combinedValue % 1000000000000000L;// last 15 digits
	}

	public List<CorpResponse> findAllUser() {

		boolean today = true;
		int allLocationsAllsafeAmount = 0;
		List<Corp> corpInfos = (List<Corp>) corpRepository.findAll();
		List<CorpResponse> corpss = new ArrayList<>();
		int allCorpInsertBillsAmount = 0;

		CorpResponse corpresponse = new CorpResponse();
		for (Corp corpinfo : corpInfos) {
			System.out.println("we are in foreach loop...................");
			int corpSafeAmount = getCorpSafeAmount(corpinfo.getId());
			if (today) {
				corpresponse = findByCorpName(corpinfo.getCorpName(), today);
				allCorpInsertBillsAmount = allCorpInsertBillsAmount + corpresponse.getTodayInsertBillsAmount();
				corpresponse.setAllCorpsTodayInsertBillsAmount(allCorpInsertBillsAmount);
				corpresponse.setCorpSafeAmount(corpSafeAmount);
				corpss.add(corpresponse);
			}

		}

		return corpss;
	}

	private int getCorpSafeAmount(Long id) {
		DashboardResponse dashboardResponse = new DashboardResponse();
		List<Long> list = new ArrayList<Long>();
		List<StoreInfoResponse> storeinfolist = new ArrayList<>();
		List<StoreInfo> storeIdList = storeinforepository.findByCorpId(id);
		int StoresafeAmount = 0;
		int CorpSafeAmount = 0;
		if (!storeIdList.isEmpty()) {

			for (StoreInfo storeinfo : storeIdList) {
				list.add(storeinfo.getId());
				Optional<StoreInfo> storeInfo = storeinforepository.findById(storeinfo.getId());
				StoreInfo store = new StoreInfo();
				if (!storeInfo.isEmpty()) {
					store = storeInfo.get();
				}
				// userfinfo start from here
				int grandTotal = 0;
				List<UserInfo> userid = store.getUsers();
				List<Long> userids = new ArrayList<>();
				for (UserInfo user : userid) {
					List<InsertBill> insertBilllist = insertBillRepository.findByUser_Id(user.getId());
					Set<String> distinctDenominations = new HashSet<String>();
					for (InsertBill bills : insertBilllist) {
						boolean withDrawStatus = bills.isWithDrawStatus();
						if (bills.isWithDrawStatus() == false) {
							distinctDenominations.add(bills.getAmount());
						}
					}
					int totalCount = 0;
					int sum = 0;

					// Adding Distinct denominations in to a set like $1,$2,...
					for (String a : distinctDenominations) {
						int count = 0;
						int product = 0;
						// checking number of notes of same denomination present in the current date
						// bill
						for (InsertBill bills : insertBilllist) {
							// System.out.println("--------we are in find the count for loop-----------");
							if (a.equals(bills.getAmount()) && bills.isWithDrawStatus() == false) {
								count++;
							}
						}
						System.out.println(count);
						product = a.equals("$1") ? 1 * count
								: a.equals("$2") ? 2 * count
										: a.equals("$5") ? 5 * count
												: a.equals("$10") ? 10 * count
														: a.equals("$20") ? 20 * count
																: a.equals("$50") ? 50 * count
																		: a.equals("$100") ? 100 * count : 1 * count;
						totalCount += count;
						sum += product;
						System.out.println("the sum of one user safe amount  is----------:" + sum);
					} // one insertbill loop end here
					grandTotal += sum;
				}
				System.out.println("In one  store  all user safe  amount is----------:" + grandTotal);
				dashboardResponse.setStoreSafeAmount(grandTotal);
				StoresafeAmount = dashboardResponse.getStoreSafeAmount();
				System.out.println("One store  safe amount is ------" + StoresafeAmount);
				CorpSafeAmount += dashboardResponse.getStoreSafeAmount();

				dashboardResponse.setCorpSafeAmount(CorpSafeAmount);
			} // users loop end here

		} // This is storelist forloop ending

		return CorpSafeAmount;
	}

	public void deleteByCorp(Long Id) {
		corpRepository.deleteById(Id);
	}

	public void updateCorp(CorpRequest corpRequest) {

		Corp corp = corpRepository.findById(corpRequest.getId()).get();
		corp.setCropName(corpRequest.getCorpName());
		corp.setDescription(corpRequest.getDescription());
		corp.setStatus(corpRequest.getStatus());
		corp.setStreetName(corpRequest.getStreetName());
		corp.setCityName(corpRequest.getCityName());
		corp.setStateName(corpRequest.getStateName());
		corp.setZipCode(corpRequest.getZipCode());
		corpRepository.save(corp);

	}

	public CorpResponse findByCorpName(String corpName, boolean toDay) {

		System.out.println("-----------------we are in findByCorpName methode-----------");
		Corp corp = corpRepository.findByCorpName(corpName);

		CorpResponse corpresponse = corpbuilder.toDto(corp);

		if (corp != null) {
			int corpAllLocationsAllSafeAmount1 = 0;

			Optional<Corp> abc = corpRepository.findById(corpresponse.getId());

			abc.stream().forEach(corpId -> {
				int corpAllLocationsAllSafeAmount = 0;
				int grandTotal = 0;
				List<Long> list = new ArrayList<Long>();
				// List<Integer> locationCountList=new ArrayList<>();
				int count = 0;
				List<StoreInfoResponse> storeinfolist = new ArrayList<>();
				List<StoreInfo> storeIdList = storeinforepository.findByCorp(corpId);
				StoreInfoResponse storeInfoResponse = new StoreInfoResponse();
				for (StoreInfo storeinfo : storeIdList) {
					list.add(storeinfo.getId());
					count = list.size();
					// locationCountList.add(count);

					List<EODReport> eodResponse = new ArrayList<>();
					eodResponse = dashBoardService.getEodReports(storeinfo.getStoreName(), toDay);

					storeInfoResponse = dashBoardService.getAllStoreInfo(storeinfo.getStoreName());

					// get the store safe amount by getStoreSafeAmount()
					int storeSafeAmount = getStoreSafeAmount(storeInfoResponse.getId());
					storeInfoResponse.setStoreSafeAmount(storeSafeAmount);

					// get the last bussiness day amouny by getStoreLastBussinesDayAmount()
					int storeLastBussinessDayAmount = getStoreLastBussinesDayAmount(storeinfo.getStoreName());
					storeInfoResponse.setLastBussinessDayAmount(storeLastBussinessDayAmount);

					// get the last pickup amount for store by
					int storeLastPickupAmount = getStoreLastPickupAmount(storeinfo.getId());
					storeInfoResponse.setLastPickupAmount(storeLastPickupAmount);

					// get the store Today insert bill amout(current bussiness day amount)
					for (EODReport eodReport : eodResponse) {
						int totalValue = eodReport.getTotalValue();
						System.out.println("idividual " + storeinfo.getStoreName() + "insertbill Amount is:"
								+ eodReport.getTotalValue());
						storeInfoResponse.setTotalInsertBillsAmount(totalValue);
						grandTotal = grandTotal + totalValue;

					}
					storeinfolist.add(storeInfoResponse);

					corpresponse.setStoreInfoResponse(storeinfolist);

				}
				corpAllLocationsAllSafeAmount += storeInfoResponse.getStoreSafeAmount();
				corpresponse.setStoreInfoId(list);
				corpresponse.setLocations(count);
				System.out.println("-----Locations list are-------" + corpresponse.getLocations());
				corpresponse.setTodayInsertBillsAmount(grandTotal);
				corpresponse.setAllLocationAllSafeAmount(corpAllLocationsAllSafeAmount);

			});

		}
		return corpresponse;
	}

//get the store last pickup amount 	
	private int getStoreLastPickupAmount(Long id) {
		StoreInfoResponse storeResponse = new StoreInfoResponse();
		int storeLastpickupAmount = 0;
		if (id != null) {
			List<UserInfo> userInfoList = userInfoRepository.findByStoreInfo_Id(id);
			for (UserInfo user : userInfoList) {
				if (user != null) {
					int num = 1;
					List<InsertBill> insertBillList = insertBillRepository.findByUser_Id(user.getId());
					
					List<LocalDateTime> insertBill= insertBillRepository.findByWithDrawDateTime();
					
                    System.out.println("lastpickup date is------------"+insertBill.size());
                    if(insertBill.size()!=0) {
                    LocalDateTime lastPickupDate =insertBill.get(insertBill.size()-1);
					System.out.println("lastpickup date is-- ------"+lastPickupDate);
					int grandTotal = 0;
					int userslastpickupamount =0;
					List<InsertBill> insertList = insertBillRepository.findByUser_IdAndWithDrawDateTime(user.getId(),lastPickupDate);

					if (!insertList.isEmpty()) {
						Set<String> distinctDenominations = new HashSet<String>();
						for (InsertBill insertBilllist : insertList) {
							distinctDenominations.add(insertBilllist.getAmount());
						}
						int totalCount = 0;
						int sum = 0;
						// Adding Distinct denominations in to a set like $1,$2,...
						for (String a : distinctDenominations) {
							int count = 0;
							int product = 0;
							// checking number of notes of same denomination present in the current date
							// bill
							for (InsertBill insertBilllist : insertList) {
								// System.out.println("--------we are in find the count for loop-----------");
								if (a.equals(insertBilllist.getAmount())) {
									count++;
								}
								insertBilllist.setWithDrawStatus(true);
								insertBillRepository.save(insertBilllist);
							}
							System.out.println(count);
							product = a.equals("$1") ? 1 * count
									: a.equals("$2") ? 2 * count
											: a.equals("$5") ? 5 * count
													: a.equals("$10") ? 10 * count
															: a.equals("$20") ? 20 * count
																	: a.equals("$50") ? 50 * count
																			: a.equals("$100") ? 100 * count : 1 * count;
							totalCount += count;
							sum += product;

							System.out.println("the sum of last pickup amount total  is----------:" + sum);
						}
						grandTotal += sum;

						System.out.println("Last  Pickupamount is --------------" + grandTotal);
						  
						}
					
					userslastpickupamount+=grandTotal;
					storeResponse.setLastPickupAmount(userslastpickupamount);
					System.out.println("store Last pickupAmount is  -------------"+userslastpickupamount);
					storeLastpickupAmount += userslastpickupamount;

				}
			} // userlist loop end here
		}
		}

		return storeLastpickupAmount;
	}

//get the  store lastbussiness day amount
	private int getStoreLastBussinesDayAmount(String storeName) {
		StoreInfoResponse storeResponse = new StoreInfoResponse();
		int grandTotal = 0;
		boolean toDay = false;
		List<EODReport> eodResponse = new ArrayList<>();
		if (storeName != null) {
			eodResponse = dashBoardService.getEodReports(storeName, toDay);
			for (EODReport eodReport : eodResponse) {
				int totalValue = eodReport.getTotalValue();
				// System.out.println("idividual "+storeinfo.getStoreName()+"insertbill Amount
				// is:"+eodReport.getTotalValue());
				storeResponse.setTotalInsertBillsAmount(totalValue);
				grandTotal = grandTotal + totalValue;

			}
		}

		return grandTotal;
	}

//get   the store safeAmount 
	private int getStoreSafeAmount(Long id) {
		StoreInfoResponse storeResponse = new StoreInfoResponse();
		int StoresafeAmount = 0;
		if (id != null) {

			List<UserInfo> usersList = userInfoRepository.findByStoreInfo_Id(id);
			for (UserInfo userInfo : usersList) {
				int grandTotal = 0;
				if (userInfo != null) {
					List<InsertBill> insertBilllist = insertBillRepository.findByUser_Id(userInfo.getId());
					Set<String> distinctDenominations = new HashSet<String>();
					for (InsertBill bills : insertBilllist) {
						boolean withDrawStatus = bills.isWithDrawStatus();
						if (bills.isWithDrawStatus() == false) {
							distinctDenominations.add(bills.getAmount());
						}
					}
					int totalCount = 0;
					int sum = 0;

					// Adding Distinct denominations in to a set like $1,$2,...
					for (String a : distinctDenominations) {
						int count = 0;
						int product = 0;
						// checking number of notes of same denomination present in the current date
						// bill
						for (InsertBill bills : insertBilllist) {
							// System.out.println("--------we are in find the count for loop-----------");
							if (a.equals(bills.getAmount()) && bills.isWithDrawStatus() == false) {
								count++;
							}
						}
						System.out.println(count);
						product = a.equals("$1") ? 1 * count
								: a.equals("$2") ? 2 * count
										: a.equals("$5") ? 5 * count
												: a.equals("$10") ? 10 * count
														: a.equals("$20") ? 20 * count
																: a.equals("$50") ? 50 * count
																		: a.equals("$100") ? 100 * count : 1 * count;
						totalCount += count;
						sum += product;
						System.out.println("the sum of one user safe amount  is----------:" + sum);
					} // one insertbill loop end here
					grandTotal += sum;

				}

				storeResponse.setStoreSafeAmount(grandTotal);
				StoresafeAmount += storeResponse.getStoreSafeAmount();
				System.out.println("One store  safe amount is ------" + StoresafeAmount);
				storeResponse.setStoreSafeAmount(StoresafeAmount);

			} // userlist end here

		}

		return StoresafeAmount;
	}

	// Pickup amount Api Service methode

	public DashboardResponse reportsInfo(Long id) {
		System.out.println("we are in service methode-------" + id);
		Optional<UserInfo> userinfo = userInfoRepository.findById(id);
		InsertBill insertBill = new InsertBill();
		LocalDateTime withDrawDate = LocalDateTime.now();
		if (!userinfo.isEmpty()) {
			UserInfo user = userinfo.get();
			Long userId = user.getId();
			System.out.println("-------userId is ----" + userId);
			List<InsertBill> list = insertBillRepository.findByUser_Id(userId);
			try {
				if (!list.isEmpty()) {
					for (InsertBill insertBillinfo : list) {
						if (insertBillinfo.getWithDrawDateTime() == null
								&& insertBillinfo.getWithDrawDateTime() != LocalDateTime.now()) {
							insertBill.setWithDrawDateTime(LocalDateTime.now());
						} else {
							insertBill.setWithDrawDateTime(insertBillinfo.getWithDrawDateTime());
						}
						boolean WithDrawStatus = false;
						if (insertBillinfo.isWithDrawStatus() == WithDrawStatus) {
							insertBill.setWithDrawStatus(true);
						} else {
							insertBill.setWithDrawStatus(false);
						}
						insertBill.setWithDrawStatus(insertBillinfo.isWithDrawStatus());
						insertBill.setActionStatus(insertBillinfo.getActionStatus());
						insertBill.setId(insertBillinfo.getId());
						insertBill.setAmount(insertBillinfo.getAmount());
						insertBill.setCreatedOn(insertBillinfo.getCreatedOn());
						insertBill.setDateTime(insertBillinfo.getDateTime());
						insertBill.setIdentifier(insertBillinfo.getIdentifier());
						insertBill.setTransactionNumber(insertBillinfo.getTransactionNumber());
						insertBill.setUser(insertBillinfo.getUser());
						insertBill.setSync(insertBillinfo.isSync());
						insertBillRepository.save(insertBill);

					}

				}
				// LocalDateTime withDrawDate=LocalDateTime.now();
				// withDrawDate=insertBill.getWithDrawDateTime();
				System.out.println("Withdraw date and time is ---" + withDrawDate);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String formattedDateTime = withDrawDate.format(formatter);
				LocalDateTime localDateTime = LocalDateTime.parse(formattedDateTime, formatter);
				System.out.println("today date and time are -----" + localDateTime);

				int grandTotal = 0;
				List<InsertBill> withdrawList = insertBillRepository.findByWithDrawDateTime(localDateTime);
				if (!withdrawList.isEmpty()) {
					Set<String> distinctDenominations = new HashSet<String>();
					for (InsertBill insertBilllist : withdrawList) {
						distinctDenominations.add(insertBilllist.getAmount());
					}
					int totalCount = 0;
					int sum = 0;
					// Adding Distinct denominations in to a set like $1,$2,...
					for (String a : distinctDenominations) {
						int count = 0;
						int product = 0;
						// checking number of notes of same denomination present in the current date
						// bill
						for (InsertBill insertBilllist : withdrawList) {
							// System.out.println("--------we are in find the count for loop-----------");
							if (a.equals(insertBilllist.getAmount())) {
								count++;
							}
							insertBilllist.setWithDrawStatus(true);
							insertBillRepository.save(insertBilllist);
						}
						System.out.println(count);
						product = a.equals("$1") ? 1 * count
								: a.equals("$2") ? 2 * count
										: a.equals("$5") ? 5 * count
												: a.equals("$10") ? 10 * count
														: a.equals("$20") ? 20 * count
																: a.equals("$50") ? 50 * count
																		: a.equals("$100") ? 100 * count : 1 * count;
						totalCount += count;
						sum += product;

						System.out.println("the sum of total amount is----------:" + sum);
					}
					grandTotal += sum;

					System.out.println("Today Pickupamount is" + grandTotal);

					// insertBill.setWithDrawStatus(true);
					// insertBillRepository.save(insertBill);
				}
				// System.out.println("Today Pickupamount is"+grandTotal);

				StoreInfo storeinfo = user.getStoreInfo();
				Long storeId = storeinfo.getId();
				Optional<StoreInfo> storeInfo = storeinforepository.findById(storeId);

				if (!storeInfo.isEmpty()) {
					StoreInfo storeInfo1 = storeInfo.get();
					String storeName = storeInfo1.getStoreName();
					System.out.println("The store name is  -----" + storeName);

					StoreInfoResponse storeInfoResponse = storeInfoService.findByStoreName(storeName);

					List<Long> userid = storeInfoResponse.getUserIds();
					System.out.println("users ids are--------" + userid);
					UserInfo userInfo = null;
					int usercount = 0;
//			 for (Long userids : userid) {
//			       userInfo =userService.getUserInfo(userids);
//			       userid.add(userId);
//			       usercount++;
//			       }
//			 

					Corp corp = storeInfo1.getCorp();
					System.out.println("----CorpInfo is-----:" + corp.getCorpName());

				}

			} catch (Exception e) {
				System.out.println("The exception are" + e);
			}

		}
		return null;
	}

//  serveice for AllLocations- AllSafe Amount  
	public DashboardResponse allLocationsAllSafe() {
		DashboardResponse dsahResponse = new DashboardResponse();
		List<Corp> corpList = (List<Corp>) corpRepository.findAll();
		List<Corp> corpInfoList = new ArrayList<>();
		List<Long> corpidsList = new ArrayList<>();
		int allLocationAllSafeAmount = 0;
		if (!corpList.isEmpty()) {
			for (Corp corp : corpList) {

				corpidsList.add(corp.getId());
				CorpResponse corpresponse = corpbuilder.toDto(corp);
				if (corp != null) {
					Optional<Corp> abc = corpRepository.findById(corpresponse.getId());
					abc.stream().forEach(corpId -> {
						List<Long> list = new ArrayList<Long>();
						List<StoreInfoResponse> storeinfolist = new ArrayList<>();
						List<StoreInfo> storeIdList = storeinforepository.findByCorp(corpId);
						int StoresafeAmount = 0;
						int CorpSafeAmount = 0;
						if (!storeIdList.isEmpty()) {

							for (StoreInfo storeinfo : storeIdList) {
								list.add(storeinfo.getId());
								Optional<StoreInfo> storeInfo = storeinforepository.findById(storeinfo.getId());
								StoreInfo store = new StoreInfo();
								if (!storeInfo.isEmpty()) {
									store = storeInfo.get();
								}
								// userfinfo start from here
								int grandTotal = 0;
								List<UserInfo> userid = store.getUsers();
								List<Long> userids = new ArrayList<>();
								for (UserInfo user : userid) {
									List<InsertBill> insertBilllist = insertBillRepository.findByUser_Id(user.getId());
									Set<String> distinctDenominations = new HashSet<String>();
									for (InsertBill bills : insertBilllist) {
										boolean withDrawStatus = bills.isWithDrawStatus();
										if (bills.isWithDrawStatus() == false) {
											distinctDenominations.add(bills.getAmount());
										}
									}
									int totalCount = 0;
									int sum = 0;

									// Adding Distinct denominations in to a set like $1,$2,...
									for (String a : distinctDenominations) {
										int count = 0;
										int product = 0;
										// checking number of notes of same denomination present in the current date
										// bill
										for (InsertBill bills : insertBilllist) {
											// System.out.println("--------we are in find the count for
											// loop-----------");
											if (a.equals(bills.getAmount()) && bills.isWithDrawStatus() == false) {
												count++;
											}
										}
										System.out.println(count);
										product = a
												.equals("$1")
														? 1 * count
														: a.equals("$2") ? 2 * count
																: a.equals("$5") ? 5 * count
																		: a.equals("$10") ? 10 * count
																				: a.equals("$20") ? 20 * count
																						: a.equals("$50") ? 50 * count
																								: a.equals("$100")
																										? 100 * count
																										: 1 * count;
										totalCount += count;
										sum += product;
										System.out.println("the sum of one user safe amount  is----------:" + sum);
									} // one insertbill loop end here
									grandTotal += sum;
								}
								System.out.println("In one  store  all user safe  amount is----------:" + grandTotal);
								dsahResponse.setStoreSafeAmount(grandTotal);
								StoresafeAmount = dsahResponse.getStoreSafeAmount();
								System.out.println("One store  safe amount is ------" + StoresafeAmount);
								CorpSafeAmount += dsahResponse.getStoreSafeAmount();

								dsahResponse.setCorpSafeAmount(CorpSafeAmount);
							} // users loop end here

						} // This is storelist forloop ending

						System.out.println("------storesid list----" + list);
						System.out.println(
								"the sum of all stores[One Corp] safe   amount is----------:" + CorpSafeAmount);

					});
					allLocationAllSafeAmount += dsahResponse.getCorpSafeAmount();
					dsahResponse.setAllLocationAllSafeAmount(allLocationAllSafeAmount);
				}

			} // this is corpList forloop ending
			System.out.println("Corp Ids list is -------" + corpidsList);
			System.out.println("the sum of all Corp[AllLocations-AllSafe ] safe   amount is----------:"
					+ dsahResponse.getAllLocationAllSafeAmount());
		}

		return dsahResponse;

	}

	public CorpResponse allCorpInfo(String corpName) {
		Corp corp = corpRepository.findByCorpName(corpName);
		CorpResponse corpresponse = corpbuilder.toDto(corp);
		// StoreInfoResponse storeResponse = new StoreInfoResponse();
		int corpAllLocationsAllSafeAmount = 0;
		if (corpresponse != null) {
			List<StoreInfoResponse> storeInfoList = new ArrayList<>();
			List<StoreInfo> storeInfos = storeinforepository.findByCorpId(corpresponse.getId());
			int StoresafeAmount = 0;
			StoreInfoResponse storeResponse = new StoreInfoResponse();
			for (StoreInfo storeInfo : storeInfos) {
				storeResponse = storeInfoBuilder.toDto(storeInfo);
				if (storeInfo != null) {
					List<UserInfo> usersList = userInfoRepository.findByStoreInfo_Id(storeInfo.getId());
					for (UserInfo userInfo : usersList) {
						int grandTotal = 0;
						if (userInfo != null) {
							List<InsertBill> insertBilllist = insertBillRepository.findByUser_Id(userInfo.getId());
							Set<String> distinctDenominations = new HashSet<String>();
							for (InsertBill bills : insertBilllist) {
								boolean withDrawStatus = bills.isWithDrawStatus();
								if (bills.isWithDrawStatus() == false) {
									distinctDenominations.add(bills.getAmount());
								}
							}
							int totalCount = 0;
							int sum = 0;

							// Adding Distinct denominations in to a set like $1,$2,...
							for (String a : distinctDenominations) {
								int count = 0;
								int product = 0;
								// checking number of notes of same denomination present in the current date
								// bill
								for (InsertBill bills : insertBilllist) {
									// System.out.println("--------we are in find the count for loop-----------");
									if (a.equals(bills.getAmount()) && bills.isWithDrawStatus() == false) {
										count++;
									}
								}
								System.out.println(count);
								product = a.equals("$1") ? 1 * count
										: a.equals("$2") ? 2 * count
												: a.equals("$5") ? 5 * count
														: a.equals("$10") ? 10 * count
																: a.equals("$20") ? 20 * count
																		: a.equals("$50") ? 50 * count
																				: a.equals("$100") ? 100 * count
																						: 1 * count;
								totalCount += count;
								sum += product;
								System.out.println("the sum of one user safe amount  is----------:" + sum);
							} // one insertbill loop end here
							grandTotal += sum;

						}

						storeResponse.setStoreSafeAmount(grandTotal);
						StoresafeAmount += storeResponse.getStoreSafeAmount();
						System.out.println("One store  safe amount is ------" + StoresafeAmount);
						storeResponse.setStoreSafeAmount(StoresafeAmount);

					} // userlist end here

					storeInfoList.add(storeResponse);
					corpAllLocationsAllSafeAmount += storeResponse.getStoreSafeAmount();
				}

			} // storelist end here

			corpresponse.setStoreInfoResponse(storeInfoList);
			corpresponse.setAllLocationAllSafeAmount(corpAllLocationsAllSafeAmount);
		}

		return corpresponse;
	}
//pickup amount records services 
	public  CorpResponse getPickupAmountRecords(String storeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
