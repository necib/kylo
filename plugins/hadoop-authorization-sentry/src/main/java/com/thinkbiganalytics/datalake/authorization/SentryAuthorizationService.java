package com.thinkbiganalytics.datalake.authorization;

import com.thinkbiganalytics.datalake.authorization.client.SentryClient;
import com.thinkbiganalytics.datalake.authorization.client.SentryClientConfig;
import com.thinkbiganalytics.datalake.authorization.client.SentryClientException;
import com.thinkbiganalytics.datalake.authorization.config.AuthorizationConfiguration;
import com.thinkbiganalytics.datalake.authorization.config.SentryConnection;
import com.thinkbiganalytics.datalake.authorization.model.HadoopAuthorizationGroup;
import com.thinkbiganalytics.datalake.authorization.service.HadoopAuthorizationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Sentry Authorization Service
 *
 * Created by Shashi Vishwakarma on 19/9/2016.
 */
public class SentryAuthorizationService implements HadoopAuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(SentryAuthorizationService.class);

    private static final String HADOOP_AUTHORIZATION_TYPE_SENTRY = "SENTRY";

    SentryClient sentryClientObject;

    @Override
    public void initialize(AuthorizationConfiguration config) {

        SentryConnection sentryConnection = (SentryConnection) config;
        // SentryClientConfig sentryClientConfiguration = new SentryClientConfig(sentryConnHelper.getDriverName() , sentryConnHelper.getConnectionURL() , sentryConnHelper.getUsername() , sentryConnHelper.getPassword());
        SentryClientConfig sentryClientConfiguration = new SentryClientConfig(sentryConnection.getDataSource());
        sentryClientObject = new SentryClient(sentryClientConfiguration);
    }

    @Override
    public HadoopAuthorizationGroup getGroupByName(String groupName) {
        return null;
    }

    @Override
    public List<HadoopAuthorizationGroup> getAllGroups() {
        try {
            sentryClientObject.createRole("thiSiShashi");
        } catch (SentryClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createOrUpdateReadOnlyHivePolicy(String categoryName, String feedName, List<String> hadoopAuthorizationGroups, String datebaseName, List<String> tableNames) {

    }

    @Override
    public void createOrUpdateReadOnlyHdfsPolicy(String categoryName, String feedName, List<String> hadoopAuthorizationGroups, List<String> hdfsPaths) {

    }

    @Override
    public void createReadOnlyHivePolicy(String categoryName, String feedName, List<String> hadoopAuthorizationGroups, String datebaseName, List<String> tableNames) {

    }

    @Override
    public void createReadOnlyHdfsPolicy(String categoryName, String feedName, List<String> hadoopAuthorizationGroups, List<String> hdfsPaths) {

    }

    @Override
    public void updateReadOnlyHivePolicy(String categoryName, String feedName, List<String> groups, String datebaseName, List<String> tableNames) {

    }

    @Override
    public void updateReadOnlyHdfsPolicy(String categoryName, String feedName, List<String> groups, List<String> hdfsPaths) {

    }

    @Override
    public void updateSecurityGroupsForAllPolicies(String categoryName, String feedName,List<String> hadoopAuthorizationGroups, Map<String,String> feedProperties) {

    }

    @Override
    public void deleteHivePolicy(String categoryName, String feedName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteHdfsPolicy(String categoryName, String feedName) {
        // TODO Auto-generated method stub

    }

    /*public List<HadoopAuthorizationPolicy> searchPolicy(Map<String, Object> searchCriteria) {
        // TODO Auto-generated method stub
        return null;
    }*/


    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return HADOOP_AUTHORIZATION_TYPE_SENTRY;
    }

}