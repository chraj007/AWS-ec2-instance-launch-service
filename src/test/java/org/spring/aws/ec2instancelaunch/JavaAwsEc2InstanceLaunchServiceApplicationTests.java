package org.spring.aws.ec2instancelaunch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.aws.ec2instancelaunch.constants.InstanceLaunchType;
import org.spring.aws.ec2instancelaunch.pojo.InstanceLaunchRequest;
import org.spring.aws.ec2instancelaunch.service.base.AWSEC2InstanceLaunch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JavaAwsEc2InstanceLaunchServiceApplication.class)
public class JavaAwsEc2InstanceLaunchServiceApplicationTests {

	@Autowired
	private AWSEC2InstanceLaunch awsEC2InstanceLaunch;
	
	@Test
	public void testOnDemandInstanceLaunch() {
		String userData = "";// your shell script, will automatically run after your instance launch
		InstanceLaunchRequest instanceLaunchRequest = new InstanceLaunchRequest("<YOUR-AWS-ACCESS-KEY>",
				"<YOUR-AWS-ACCESS-SECRET>",
				"<YOUR-KEY-NAME/PEM-FILE-NAME>",
				InstanceLaunchType.ONDEMAND,
				"<YOUR-AMI-ID>",
				"t2.small", // Can be any instance type e.g t2.micro, m4.large etc.
				"<COMMA-SEPERATED-SECURITY-GROUPS>",
				userData,
				"us-west-1", // can be any valid AWS regions 
				"", //not required for ondemand
				0,//not required for ondemand
				0);//not required for ondemand
		
		// Uncomment below line to test with valid details
		//awsEC2InstanceLaunch.launchEC2Instance(instanceLaunchRequest);
	}
	
	@Test
	public void testSpotInstanceLaunch(){
		String userData = "";// your shell script, will automatically run after your instance launch
		InstanceLaunchRequest instanceLaunchRequest = new InstanceLaunchRequest("<YOUR-AWS-ACCESS-KEY>",
				"<YOUR-AWS-ACCESS-SECRET>",
				"<YOUR-KEY-NAME/PEM-FILE-NAME>",
				InstanceLaunchType.SPOT,
				"<YOUR-AMI-ID>",
				"t2.small", // Can be any instance type(except t2)  m4.large etc.
				"<COMMA-SEPERATED-SECURITY-GROUPS>",
				userData,
				"us-west-1", // can be any valid AWS regions 
				"0.05", //Your bid price
				1,
				30);//will get latest price and bids more than 30% of current price
		
		// Uncomment below line to test with valid details
		//awsEC2InstanceLaunch.launchEC2Instance(instanceLaunchRequest);
	}

}
