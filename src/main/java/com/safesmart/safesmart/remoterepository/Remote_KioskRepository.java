package com.safesmart.safesmart.remoterepository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.safesmart.safesmart.model.Kiosk;
import com.safesmart.safesmart.model.Printer;
import com.safesmart.safesmart.model.StoreInfo;


@Repository
public interface Remote_KioskRepository extends PagingAndSortingRepository<Kiosk, Long> {

	Kiosk findByKioskName(String kioskName);
	
	List<Kiosk> findByActive(boolean active);

	Kiosk findByKioskId(String kioskId);
	
	Kiosk findByIpAddress(String ipAddress);

	
	Kiosk findByMacAddress(String macAddress);
	
	Kiosk findByStoreinfok_Id(Long id);
	
	Kiosk findByIdentifier(String identifier);

	Kiosk findByStoreinfok_Id(StoreInfo storeinfok);
	
}