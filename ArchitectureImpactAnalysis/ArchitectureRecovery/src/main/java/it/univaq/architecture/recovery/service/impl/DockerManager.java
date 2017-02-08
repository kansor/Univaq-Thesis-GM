package it.univaq.architecture.recovery.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.univaq.architecture.recovery.model.MicroService;

public class DockerManager {

	public List<MicroService> getNetwork(List<MicroService> services) {
		List<MicroService> richServices = services;
		Iterator<MicroService> it = richServices.iterator();
		while (it.hasNext()) {
			MicroService service = (MicroService) it.next();
			String s = null;
			Process p;
			try {
				p = Runtime.getRuntime().exec("docker inspect " + service.getContainerID());
				System.out.println("docker inspect " + service.getContainerID() + " | grep Network");
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((s = br.readLine()) != null) {
					if (s.contains("NetworkMode")) {
						service.setNetwork(s.substring(s.indexOf(":") + 3, s.length() - 2));
					}else if(s.contains("IPAddress")){
						service.setIp((s.substring(s.indexOf(":") + 3, s.length() - 2)));
					}
					

				}
				System.out.println(service.getName() + "IP: " + service.getIp() + " network: " + service.getNetwork());
				p.waitFor();
				p.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		return richServices;
	}

	public String getClientIP(String network){
		String clientIP = null;
		Process p;
		String s = null;
		try {
			p = Runtime.getRuntime().exec("docker network " + network);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((s = br.readLine()) != null) {
				if (s.contains("Gateway")) {
					clientIP = s.substring(s.indexOf(":"), s.lastIndexOf('"'));
				}

			}
			p.waitFor();
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientIP;
	}

	public List<MicroService> getContainerId(List<MicroService> services) {
		// String result = this.reader("docker ps");
		List<MicroService> richServices = services;
		Iterator<MicroService> it = richServices.iterator();
		while (it.hasNext()) {
			MicroService service = (MicroService) it.next();
			String s = null;
			Process p;
			try {
				p = Runtime.getRuntime().exec("docker ps");
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((s = br.readLine()) != null) {
					if (s.contains(service.getName())) {
						String containerID = s.substring(0, 13);
						service.setContainerID(containerID);
					}

				}
				p.waitFor();
				p.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return richServices;

	}
	public List<String> checkIfContainerHasTheSameNetwork(List<MicroService> containers){
		List<String> networks = new ArrayList<String>();
		
		
		Iterator<MicroService> it = containers.iterator();
		while (it.hasNext()) {
			MicroService microService = (MicroService) it.next();
			if(!networks.contains(microService.getNetwork())){
				networks.add(microService.getNetwork());
			}
		}
		
		
		return networks;
	}

}
