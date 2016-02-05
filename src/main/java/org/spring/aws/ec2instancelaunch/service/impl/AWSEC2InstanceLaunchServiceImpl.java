package org.spring.aws.ec2instancelaunch.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.spring.aws.ec2instancelaunch.pojo.InstanceLaunchRequest;
import org.spring.aws.ec2instancelaunch.service.base.AWSEC2InstanceLaunch;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.util.Base64;

@Service
public class AWSEC2InstanceLaunchServiceImpl implements AWSEC2InstanceLaunch{

	@Override
	public List<String> launchEC2Instance(InstanceLaunchRequest instanceLaunchRequest) {
	List<String> instanceIds = new ArrayList<String>(0);
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(instanceLaunchRequest.getAccessKey(), instanceLaunchRequest.getAccessSecret());
		AmazonEC2 ec2 = new AmazonEC2Client(awsCreds);
		ec2.setRegion(Region.getRegion(Regions.fromName(instanceLaunchRequest.getRegion())));
		switch (instanceLaunchRequest.getInstanceLaunchType()) {
		case SPOT:
			instanceIds = launchSpot(instanceLaunchRequest,ec2);
			break;
		case ONDEMAND:
			instanceIds = launchOnDemand(instanceLaunchRequest, ec2);
			break;
		case RESERVED:

			break;
		case AUTO:

			break;
		default:
			break;
		}
	    return instanceIds;
	}
	private List<String> launchOnDemand(InstanceLaunchRequest instanceLaunchRequest, AmazonEC2 ec2){
		List<String> onDemand = new ArrayList<String>(0);
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		 runInstancesRequest.withImageId(instanceLaunchRequest.getImageId())
		 .withInstanceType(instanceLaunchRequest.getInstanceType())
         .withMinCount(1)
         .withMaxCount(1)
         .withKeyName(instanceLaunchRequest.getKeyName())
         .withUserData(new String(Base64.encode(instanceLaunchRequest.getUserData().getBytes())))
         .withSecurityGroups(instanceLaunchRequest.getSecurityGroups());
		 try {
			 RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
			 Reservation reservation = runInstancesResult.getReservation();
			 if(reservation != null){
				 List<Instance> instances = reservation.getInstances();
				 boolean anyOpen;
				 do {
			    	 anyOpen = false;
					 for (Instance instance : instances) {
						if(instance.getInstanceId()==null){
							anyOpen = true;
							try{
								 if(anyOpen)
								 // Sleep for 60 seconds.
								 Thread.sleep(60*1000);
							 }catch (Exception e) {
					    	        // Do nothing because it woke up early.
							 }
		    	            break;
						}
						onDemand.add(instance.getInstanceId());
					 }
				 }while(anyOpen);
		 	}
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 	}
		 return onDemand;
	}
	private LaunchSpecification getLaunchSpecification(InstanceLaunchRequest instanceLaunchRequest){
		LaunchSpecification launchSpecification = new LaunchSpecification();
		launchSpecification.setImageId(instanceLaunchRequest.getImageId());
		launchSpecification.setInstanceType(instanceLaunchRequest.getInstanceType());
		launchSpecification.setSecurityGroups(Arrays.asList(instanceLaunchRequest.getSecurityGroups().split(",")));
		launchSpecification.setUserData(new String(Base64.encode(instanceLaunchRequest.getUserData().getBytes())));
		return launchSpecification;
	}
	
	private List<String> launchSpot(InstanceLaunchRequest instanceLaunchRequest, AmazonEC2 ec2){
		List<String> spotInstanceIds = new ArrayList<String>();
		RequestSpotInstancesRequest requestRequest = new RequestSpotInstancesRequest();
		
		requestRequest.setInstanceCount(instanceLaunchRequest.getSpotInstanceCount());
		String spotPrice = null;
		if(instanceLaunchRequest.getSpotPrice() == null){
			spotPrice = getBidPrice(ec2, instanceLaunchRequest.getInstanceType(), instanceLaunchRequest.getSpotPriceHigherPercentage())+"";
		}else{
			spotPrice = instanceLaunchRequest.getSpotPrice();
		}
		requestRequest.setSpotPrice(spotPrice);
		requestRequest.setLaunchSpecification(getLaunchSpecification(instanceLaunchRequest));
		
		RequestSpotInstancesResult requestResult = ec2.requestSpotInstances(requestRequest);
	    List<SpotInstanceRequest> requestResponses = requestResult.getSpotInstanceRequests();
		
	    List<String> spotInstanceRequestIds = new ArrayList<String>();
	    for (SpotInstanceRequest requestResponse : requestResponses) {
	        spotInstanceRequestIds.add(requestResponse.getSpotInstanceRequestId());
	    }
	    
	    boolean anyOpen;
	    DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
	    describeRequest.setSpotInstanceRequestIds(spotInstanceRequestIds);
	    do {
	    	 anyOpen = false;
	    	 try {
	    		 DescribeSpotInstanceRequestsResult describeResult = ec2.describeSpotInstanceRequests(describeRequest);
	    		 List<SpotInstanceRequest> describeResponses = describeResult.getSpotInstanceRequests();
	    		 for (SpotInstanceRequest describeResponse : describeResponses) {
	    	          // If the state is open, it hasn't changed since we
	    	          // attempted to request it. There is the potential
	    	          // for it to transition almost immediately to closed or
	    	          // canceled so we compare against open instead of active.
	    	          if (describeResponse.getState().equals("open")) {
	    	            anyOpen = true;
	    	            break;
	    	          }

	    	          // Add the instance id to the list we will
	    	          // eventually terminate.
	    	          spotInstanceIds.add(describeResponse.getInstanceId());
	    		 }
			} catch (Exception e) {
				e.printStackTrace();
				 anyOpen = true;
			}
	    	 try {
    	        // Sleep for 60 seconds.
    	        Thread.sleep(60*1000);
    	      } catch (Exception e) {
    	        // Do nothing because it woke up early.
    	      }
		} while (anyOpen);
	    
	    return spotInstanceIds;
	}

	public double getBidPrice(AmazonEC2 ec2, String instanceType, double percentageHigher){
		// Get the spot price history
		String nextToken = "";
		Date endDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -90);
	    // Prepare request (include nextToken if available from previous result)
	    DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest().withNextToken(nextToken);
	    List<String> instanceTypes = new ArrayList<String>();
	    instanceTypes.add(instanceType);
	    request.setInstanceTypes(instanceTypes);
	    request.setStartTime(calendar.getTime());
	    request.setEndTime(endDate);
	    // Perform request
	    DescribeSpotPriceHistoryResult result = ec2.describeSpotPriceHistory(request);
	    SpotPrice spotPrice = null;
	    for (int i = 0; i < result.getSpotPriceHistory().size();) {
	    	spotPrice = result.getSpotPriceHistory().get(i);
	    	break;
	    }
	    double bidPrice = Double.parseDouble(spotPrice.getSpotPrice())/10 + Double.parseDouble(spotPrice.getSpotPrice());
	    // 'nextToken' is the string marking the next set of results returned (if any), 
	    // it will be empty if there are no more results to be returned.            
	    nextToken = result.getNextToken();

		return bidPrice;
	}
	@Override
	public void terminateInstance(InstanceLaunchRequest spotLaunchRequest, List<String> instanceIds) {
		if(instanceIds != null && instanceIds.size() > 0){
			TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest(instanceIds);
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(spotLaunchRequest.getAccessKey(), spotLaunchRequest.getAccessSecret());
			AmazonEC2 ec2 = new AmazonEC2Client(awsCreds);
			//ec2.setRegion(Region.getRegion(Regions.fromName(spotLaunchRequest.getRegion())));
			ec2.setRegion(Region.getRegion(Regions.fromName(spotLaunchRequest.getRegion())));
			ec2.terminateInstances(terminateRequest);
		}
	}

	@Override
	public String getCurrentEc2InstanceId() {
		String EC2Id = "";
		String inputLine;
		BufferedReader in = null;
		try {
			URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/instance-id");
			URLConnection EC2MD = EC2MetaData.openConnection();
			in = new BufferedReader(
			new InputStreamReader(EC2MD.getInputStream()));
			while ((inputLine = in.readLine()) != null)
			{	
				EC2Id = inputLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return EC2Id;
	}

	@Override
	public String getInstanceStatus(String instanceId, String accessKey, String accessSecret, String region) throws Exception{
		DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withInstanceIds(instanceId);
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, accessSecret);
		AmazonEC2 ec2 = new AmazonEC2Client(awsCreds);
		ec2.setRegion(Region.getRegion(Regions.fromName(region)));
		DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
		List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();
		while (state.size() < 1) { 
		    // Do nothing, just wait, have thread sleep if needed
		    describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
		    state = describeInstanceResult.getInstanceStatuses();
		}
		return state.get(0).getInstanceState().getName();
		
	}
}
