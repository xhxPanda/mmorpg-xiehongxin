package com.hh.mmorpg.Increment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncrementManager {
	
	public static final IncrementManager INSTANCE = new IncrementManager();
	
	private Map<String, IncrementDomain> incrementMap = new HashMap<>();
	
	private IncrementManager() {
		List<IncrementDomain> incrementDomains = IncrementDao.INSTANCE.getAllIncrementDomain();
		
		for(IncrementDomain domain : incrementDomains) {
			incrementMap.put(domain.getName(), domain);
		}
	}

	
	public int increase(String name) {
		IncrementDomain domain = incrementMap.get(name);
		
		int i = domain.getNow().incrementAndGet();
		IncrementDao.INSTANCE.updateIncrementDomain(i, name);
		return i;
	}
}
