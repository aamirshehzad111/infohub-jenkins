
pipelineJob('common-jobs/set_jenkins_user_token') {

    logRotator {
      numToKeep(10)
    }

  parameters {
      stringParam('USER_NAME', 'aamirshehzad111','Enter the name of user')
  }

    definition {
      cps {
        script('''
import hudson.model.*
import jenkins.model.*
import jenkins.security.*
import jenkins.security.apitoken.*
import jenkins.model.Jenkins
import groovy.json.*
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.google.common.base.Suppliers;
import com.amazonaws.SdkBaseException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.SecretListEntry;
import com.amazonaws.services.secretsmanager.model.Tag;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.IdCredentials;
import com.amazonaws.regions.*;
import io.jenkins.plugins.credentials.secretsmanager.factory.CredentialsFactory;
import org.acegisecurity.Authentication;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;  
import javax.annotation.Nonnull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import io.jenkins.plugins.credentials.secretsmanager.config.EndpointConfiguration;
import io.jenkins.plugins.credentials.secretsmanager.config.Filters;
import io.jenkins.plugins.credentials.secretsmanager.config.PluginConfiguration;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import java.security.InvalidParameterException;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient
import com.amazonaws.services.secretsmanager.model.*
import com.amazonaws.services.secretsmanager.model.PutSecretValueResult
import com.amazonaws.services.secretsmanager.model.PutSecretValueRequest


String secretName = "jenkins-user-token";    
def region = (Regions.getCurrentRegion() != null )? Regions.getCurrentRegion().getName() : "us-east-2"
AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard().withRegion(region).build(); 
PutSecretValueRequest putSecretValueRequest = new PutSecretValueRequest().withSecretId(secretName)
PutSecretValueResult putSecretValueResult = new PutSecretValueResult().withName(secretName)

def userName = "${USER_NAME}"
def tokenName = 'user-token'
def user = User.get(userName, false)
def apiTokenProperty = user.getProperty(ApiTokenProperty.class)
def result = apiTokenProperty.tokenStore.generateNewToken(tokenName)
user.save()

String secretString = userName+':'+result.plainValue
putSecretValueRequest.setSecretString(secretString) 
putSecretValueResult = client.putSecretValue(putSecretValueRequest);
        ''')
        sandbox(true)
      }
    }
}
