package com.schedule.rds;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.StartDBInstanceRequest;
import com.amazonaws.util.CollectionUtils;
import com.credential.CredentialUtil;
import java.util.List;
import java.util.stream.Collectors;

public class RDSStart implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object event, Context context) {
        LambdaLogger logger = context.getLogger();

        AmazonRDS amazonRDSClient = CredentialUtil.getAmazonRDSClient();
        DescribeDBInstancesResult describeDBInstancesResult = amazonRDSClient.describeDBInstances();

        List<String> instanceIdentifiers = describeDBInstancesResult.getDBInstances().stream()
                .filter(this::isStopped)
                .map(DBInstance::getDBInstanceIdentifier)
                .collect(Collectors.toList());

        if (CollectionUtils.isNullOrEmpty(instanceIdentifiers)) {
            logger.log("There are no stopped DBInstances.");
            return null;
        }

        for (String instanceIdentifier : instanceIdentifiers) {
            StartDBInstanceRequest startDBInstanceRequest = new StartDBInstanceRequest().withDBInstanceIdentifier(instanceIdentifier);
            amazonRDSClient.startDBInstance(startDBInstanceRequest);
            logger.log("instanceIdentifier : " + instanceIdentifier + " The DBInstance available normally.");
        }
        return null;
    }

    private boolean isStopped(DBInstance dbInstance) {
        return RDSState.STOPPED.name().equalsIgnoreCase(dbInstance.getDBInstanceStatus());
    }
}
