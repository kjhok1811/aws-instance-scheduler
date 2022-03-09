package com.credential;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;

public class CredentialUtil {
    private static final String ACCESS_KEY = System.getenv("ACCESS_KEY");
    private static final String SECRET_ACCESS_KEY = System.getenv("SECRET_ACCESS_KEY");

    private CredentialUtil() {}

    public static AmazonEC2 getAmazonEC2Client() {
        return AmazonEC2ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(credentialsProvider())
                .build();
    }

    public static AmazonRDS getAmazonRDSClient() {
        return AmazonRDSClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(credentialsProvider())
                .build();
    }

    private static AWSStaticCredentialsProvider credentialsProvider() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_ACCESS_KEY);
        return new AWSStaticCredentialsProvider(basicAWSCredentials);
    }
}
