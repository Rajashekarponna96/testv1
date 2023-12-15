package com.safesmart.safesmart.configuration;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;
import com.safesmart.safesmart.model.ActionStatus;
import com.safesmart.safesmart.model.Kiosk;
import com.safesmart.safesmart.model.Role;
import com.safesmart.safesmart.model.SequenceInfo;
import com.safesmart.safesmart.model.StoreInfo;
import com.safesmart.safesmart.model.UserInfo;
import com.safesmart.safesmart.remoterepository.Remote_KioskRepository;
import com.safesmart.safesmart.repository.KioskRepository;
import com.safesmart.safesmart.repository.RoleRepository;
import com.safesmart.safesmart.repository.SequenceInfoRepository;
import com.safesmart.safesmart.repository.StoreInfoRepository;
import com.safesmart.safesmart.repository.UserInfoRepository;
import com.safesmart.safesmart.util.Base64BasicEncryption;


@Component
public class DatabaseRunner implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private SequenceInfoRepository sequenceInfoRepository;

	@Autowired
	private StoreInfoRepository storeInfoRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private Base64BasicEncryption passwordEncrypt;
	
	@Autowired
	private KioskRepository kioskRepository;
	
	@Autowired
	private Remote_KioskRepository remotekioskRepository;

	Kiosk syss = new Kiosk();

	@Override
	public void run(String... args) throws Exception {

		List<Role> roles = (List<Role>) roleRepository.findAll();
		if (roles == null || roles.isEmpty()) {

			List<String> all = new ArrayList<String>();
			all.add("All");

			Role adminRole = new Role();
			adminRole.setName("ADMIN");
			adminRole.setDescription("Administrator");
			adminRole.setFeatures(all);
			adminRole.setWebModule(all);
			adminRole.setActionStatus(ActionStatus.Created);

			List<String> insertBills = new ArrayList<String>();
			insertBills.add("InsertBills");
			Role employeRole = new Role();
			employeRole.setName("EMPLOYEE");
			employeRole.setDescription("Employeee");
			employeRole.setFeatures(insertBills);
			employeRole.setActionStatus(ActionStatus.Created);

			Role manager = new Role();
			manager.setName("MANAGER");
			manager.setDescription("manager");
			manager.setFeatures(all);
			manager.setWebModule(all);
			manager.setActionStatus(ActionStatus.Created);
			
			Role shiftmanager = new Role();
			shiftmanager.setName("SHIFTMANAGER");
			shiftmanager.setDescription("Shift Manager");
			shiftmanager.setActionStatus(ActionStatus.Created);

			List<String> shiftFeatures = new ArrayList<String>();
			shiftFeatures.add("InsertBills");
			shiftFeatures.add("Admin");
			shiftFeatures.add("Doors");
			shiftFeatures.add("ChangeRequestDoors");
			shiftFeatures.add("StandBank");
			shiftmanager.setFeatures(shiftFeatures);

			Role truck = new Role();
			truck.setName("TRUCK");
			truck.setDescription("Truck");
			List<String> truckFeatures = new ArrayList<String>();
			truckFeatures.add("OTPScreen");
			truckFeatures.add("Valut");
			truck.setFeatures(truckFeatures);
			truck.setActionStatus(ActionStatus.Created);
			
			Role owner = new Role();
			owner.setName("OWNER");
			owner.setDescription("owner");
			owner.setFeatures(all);
			owner.setWebModule(all);
			owner.setActionStatus(ActionStatus.Created);
			
			Role store_admin = new Role();
			store_admin.setName("STORE_ADMIN");
			store_admin.setDescription("store_admin");
			store_admin.setFeatures(all);
			store_admin.setWebModule(all);
			store_admin.setActionStatus(ActionStatus.Created);
			
			Role super_admin = new Role();
			super_admin.setName("SUPER_ADMIN");
			super_admin.setDescription("super_admin");
			super_admin.setFeatures(all);
			super_admin.setWebModule(all);
			super_admin.setActionStatus(ActionStatus.Created);
			
			Role director_of_operation = new Role();
			director_of_operation.setName("DIRECTOR_OF_OPERATION");
			director_of_operation.setDescription("director_of_operation");
			director_of_operation.setFeatures(all);
			director_of_operation.setWebModule(all);
			director_of_operation.setActionStatus(ActionStatus.Created);

			roles = new ArrayList<Role>();
			roles.add(adminRole);
			roles.add(employeRole);
			roles.add(manager);
			roles.add(shiftmanager);
			roles.add(truck);
			roles.add(owner);
			roles.add(store_admin);
			roles.add(super_admin);
			roles.add(director_of_operation);

			roleRepository.saveAll(roles);

			SequenceInfo sequenceInfo = new SequenceInfo();

			sequenceInfo.setName("TRANSACTIONNO");
			sequenceInfo.setValue(1);
			sequenceInfoRepository.save(sequenceInfo);

//			StoreInfo storeInfo = new StoreInfo();
//			storeInfo.setSerialNumber("UT0");
//			storeInfo.setCorpStoreNo("ABC");
//			storeInfo.setStoreName("XYZ");
//			storeInfo.setStartTime(LocalTime.now());
//			storeInfo.setEndTime(LocalTime.NOON);
//			storeInfo.setStatus(true);
//			storeInfo.setActionStatus(ActionStatus.Created);
//
//			storeInfoRepository.save(storeInfo);
//
//			UserInfo userInfo = new UserInfo();
//			userInfo.setUsername("Admin");
//			userInfo.setPassword(passwordEncrypt.encodePassword("1234"));
//			userInfo.setRole(roleRepository.findByName("ADMIN"));
//			userInfo.setStoreInfo(storeInfoRepository.findByStoreName("XYZ"));
//			userInfo.setActionStatus(ActionStatus.Created);
//			userInfoRepository.save(userInfo);

			
		}
		
		// Ip and Mac
		InetAddress ip;
		StringBuilder sb = new StringBuilder();
        
            ip = InetAddress.getLocalHost();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

           
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
    		
//    		syss.setIpAddress(ip.getHostAddress());
//    		syss.setMacAddress(sb.toString());

  
        
      Kiosk k= remotekioskRepository.findByMacAddress(sb.toString());
        
	    if(k==null) {
				//kiosk
				syss.setBrandName(System.getProperty("os.name"));
				syss.setModelName(InetAddress.getLocalHost().getHostName());
				syss.setCpu(System.getenv("PROCESSOR_IDENTIFIER"));
				
				//C hard disk
				File file = new File("c:");
			    long totalSpace = file.getTotalSpace(); //total disk space in bytes.
					    	
				long total1=totalSpace /1024 /1024/1024;
				String totalSpace1=String.valueOf(total1);
				syss.setHdd(totalSpace1);
				
				//Ram
				long ram= ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
			    long sizekb = ram /1000;
			    long sizemb = sizekb / 1000;
			    long sizegb = sizemb / 1000 ;
			    String s=String.valueOf(sizegb);

			    syss.setRamMemory(s);
			    
			    // screen size
			       try {
			        	 System.setProperty("java.awt.headless", "false");
			        Dimension size= Toolkit.getDefaultToolkit().getScreenSize();
			        
		          // width will store the width of the screen
		          int width = (int)size.getWidth();
		     
		          // height will store the height of the screen
		          int height = (int)size.getHeight();
		          
		          syss.setScreenSize(width+"*"+height);
				 } catch (Exception e) {

			            e.printStackTrace();

			        }
				// Ip and Mac
//				InetAddress ip;
//		        try {
//
//		            ip = InetAddress.getLocalHost();
//
//		            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
//
//		            byte[] mac = network.getHardwareAddress();
//
//		            StringBuilder sb = new StringBuilder();
//		            for (int i = 0; i < mac.length; i++) {
//		                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//		            }
//		    		
		    		syss.setIpAddress(ip.getHostAddress());
		    		syss.setMacAddress(sb.toString());
//
//		        } catch (Exception e) {
//
//		            e.printStackTrace();
//
//		        }
			       
			   	syss.setIpAddress(ip.getHostAddress());
	    		syss.setMacAddress(sb.toString());
		        
		        //Port
				SerialPort [] AvailablePorts = SerialPort.getCommPorts();
			     // use the for loop to print the available serial ports
			     for(SerialPort S : AvailablePorts)
				syss.setPort(S.getSystemPortName());
		 
		        syss.setActive(true);
		        syss.setActionStatus(ActionStatus.Created);
		        System.out.println("coming...");
		        remotekioskRepository.save(syss);
				this.getIdField();
	    }
	}
	
	public void getIdField() {
		syss.setKioskId(syss.getId().toString());
		syss.setKioskName("Kiosk" + syss.getId().toString());
		remotekioskRepository.save(syss);
	}

}
