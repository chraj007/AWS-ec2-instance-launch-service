package org.spring.aws.ec2instancelaunch.service.base;

import java.util.List;

import org.spring.aws.ec2instancelaunch.pojo.InstanceLaunchRequest;

public interface AWSEC2InstanceLaunch {

	/**
	 * Launch instance.
	 *
	 * @param instanceLaunchRequest the instance launch request
	 * @return the list
	 */
	List<String> launchEC2Instance(InstanceLaunchRequest instanceLaunchRequest);
	
	/**
	 * Terminate instance.
	 *
	 * @param instanceLaunchRequest the instance launch request
	 * @param instanceIds the instance ids
	 */
	void terminateInstance(InstanceLaunchRequest instanceLaunchRequest, List<String> instanceIds);
	
	/**
	 * Gets the current ec2 instance id.
	 *
	 * @return the current ec2 instance id
	 */
	public String getCurrentEc2InstanceId();
	
	public String getInstanceStatus(String instanceId, String accessKey, String accessSecret, String region) throws Exception;
}
