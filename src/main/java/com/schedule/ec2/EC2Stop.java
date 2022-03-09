package com.schedule.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.CollectionUtils;
import com.credential.CredentialUtil;
import java.util.List;
import java.util.stream.Collectors;

public class EC2Stop implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object event, Context context) {
        LambdaLogger logger = context.getLogger();

        AmazonEC2 amazonEC2Client = CredentialUtil.getAmazonEC2Client();
        DescribeInstancesResult describeInstancesResult = amazonEC2Client.describeInstances(new DescribeInstancesRequest());

        List<String> instanceIds = describeInstancesResult.getReservations().stream()
                .flatMap(ec2 -> ec2.getInstances().stream().filter(this::isRunning)
                .map(Instance::getInstanceId))
                .collect(Collectors.toList());

        if (CollectionUtils.isNullOrEmpty(instanceIds)) {
            logger.log("There are no started instances.");
            return null;
        }

        for (String instanceId : instanceIds) {
            try {
                StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
                amazonEC2Client.stopInstances(stopInstancesRequest);
                logger.log("instanceId : " + instanceId + " The instance stopped normally.");
            } catch (AmazonEC2Exception e) {
                logger.log("instanceId '" + instanceId + "' cannot be stopped because it is spot applied.");
            }
        }
        return null;
    }

    private boolean isRunning(Instance instance) {
        return EC2State.RUNNING.name().equalsIgnoreCase(instance.getState().getName());
    }
}
