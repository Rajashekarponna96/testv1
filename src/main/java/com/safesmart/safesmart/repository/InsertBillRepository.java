package com.safesmart.safesmart.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.safesmart.safesmart.dto.InsertBillResponse;
import com.safesmart.safesmart.model.InsertBill;
import com.safesmart.safesmart.model.UserInfo;
@Repository
@Transactional
public interface InsertBillRepository extends  PagingAndSortingRepository<InsertBill, String> {

	List<InsertBill> findByTransactionNumberIsNull();

	List<InsertBill> findByCreatedOn(LocalDate now);

	List<InsertBill> findByCreatedOnAndUser_IdIn(LocalDate now, List<Long> users);
	
	List<InsertBill> findByTransactionNumber(String transactionNumber);

	List<InsertBill> findByCreatedOnBetween(LocalDate minusDays, LocalDate now);

	List<InsertBill> findByUser_IdAndCreatedOnBetween(Long userId, LocalDate stDate, LocalDate endDate);
	
	List<InsertBill> findByUser_IdAndCreatedOn(Long userId, LocalDate now);

	List<InsertBill> findByUser_IdAndDateTimeBetween(Long userId, LocalDateTime stDate, LocalDateTime endDate);
	
	List<InsertBill> findBySync(boolean sync);

	List<InsertBill> findByUser_Id(Long userId);
	
	List<InsertBill> findByWithDrawDateTime(LocalDateTime date );
	

	//@Query(value="SELECT e FROM InsertBill e ORDER BY e.withdrawDateTime DESC")
	@Query(value="SELECT i.withDrawDateTime from InsertBill i  WHERE i.withDrawDateTime IN (SELECT max(i.withDrawDateTime) FROM InsertBill)")
	List<LocalDateTime> findByWithDrawDateTime();

	List<InsertBill> findByUser_IdAndWithDrawDateTime(Long id,LocalDateTime lastPickupDate);

	List<InsertBill> findByWithDrawStatus(boolean b);

	List<InsertBill> findByUser_IdAndWithDrawStatus(Long id,boolean b);
	
	
	

	
	
	
	
	

}
