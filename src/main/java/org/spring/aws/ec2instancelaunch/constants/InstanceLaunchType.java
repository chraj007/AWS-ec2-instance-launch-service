package org.spring.aws.ec2instancelaunch.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public enum InstanceLaunchType {
	SPOT,ONDEMAND,RESERVED,AUTO;
	
	 private static Map<String, InstanceLaunchType> namesMap = new HashMap<String, InstanceLaunchType>(4);
	 static {
	        namesMap.put("spot", SPOT);
	        namesMap.put("ondemand", ONDEMAND);
	        namesMap.put("reserved", RESERVED);
	        namesMap.put("auto", AUTO);
	    }

	    public static InstanceLaunchType forValue(String value) {
	        return namesMap.get(value.toLowerCase());
	    }

	    public String toValue() {
	        for (Entry<String, InstanceLaunchType> entry : namesMap.entrySet()) {
	            if (entry.getValue() == this)
	                return entry.getKey();
	        }
	        return null; // or fail
	    }


}
