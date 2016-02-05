package org.spring.aws.ec2instancelaunch.pojo;

import org.spring.aws.ec2instancelaunch.constants.InstanceLaunchType;

public class InstanceLaunchRequest {

	private String accessKey;
	private String accessSecret;
	private String keyName;
	private InstanceLaunchType instanceLaunchType;
	private String imageId;
	private String instanceType; 
	private String securityGroups;
	private String userData;
	private String region;
	private String spotPrice;
	private int spotInstanceCount;
	private double spotPriceHigherPercentage;
	
	public InstanceLaunchRequest(String accessKey, String accessSecret,
			String keyName, InstanceLaunchType instanceLaunchType,
			String imageId, String instanceType, String securityGroups,
			String userData, String region, String spotPrice,
			int spotInstanceCount, double spotPriceHigherPercentage) {
		super();
		this.accessKey = accessKey;
		this.accessSecret = accessSecret;
		this.keyName = keyName;
		this.instanceLaunchType = instanceLaunchType;
		this.imageId = imageId;
		this.instanceType = instanceType;
		this.securityGroups = securityGroups;
		this.userData = userData;
		this.region = region;
		this.spotPrice = spotPrice;
		this.spotInstanceCount = spotInstanceCount;
		this.spotPriceHigherPercentage = spotPriceHigherPercentage;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public InstanceLaunchType getInstanceLaunchType() {
		return instanceLaunchType;
	}

	public void setInstanceLaunchType(InstanceLaunchType instanceLaunchType) {
		this.instanceLaunchType = instanceLaunchType;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getSecurityGroups() {
		return securityGroups;
	}

	public void setSecurityGroups(String securityGroups) {
		this.securityGroups = securityGroups;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSpotPrice() {
		return spotPrice;
	}

	public void setSpotPrice(String spotPrice) {
		this.spotPrice = spotPrice;
	}

	public int getSpotInstanceCount() {
		return spotInstanceCount;
	}

	public void setSpotInstanceCount(int spotInstanceCount) {
		this.spotInstanceCount = spotInstanceCount;
	}

	public double getSpotPriceHigherPercentage() {
		return spotPriceHigherPercentage;
	}

	public void setSpotPriceHigherPercentage(double spotPriceHigherPercentage) {
		this.spotPriceHigherPercentage = spotPriceHigherPercentage;
	}
	
}
