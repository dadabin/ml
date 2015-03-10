package org.common.cache;

import java.util.LinkedHashMap;  
import java.util.Collection;  
import java.util.Map;  
import java.util.ArrayList;

public class LRUCache<K,V> {
	private static final float hashTableLoadFactor=0.75f;
	
	private LinkedHashMap<K,V> map;
	private int cacheSize;
	
	public LRUCache(int cacheSize){
		
		this.cacheSize=cacheSize;
		int hashTableCapacity=(int)Math.ceil(cacheSize/hashTableLoadFactor)+1;

	}

}
