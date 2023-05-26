package oci.vault;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class VaultConnector {

    public static void main(String[] args) throws IOException {

        AbstractAuthenticationDetailsProvider provider = getInstanceAuthenticationDetailsProvider();

        /* Create a service client */
        try (SecretsClient client = SecretsClient.builder().build(provider)) {

            /* Create a request and dependent object(s). */

            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder()
                                                                                  .secretId(
                                                                                      "ocid1.vaultsecret.oc1.me-jeddah-1.amaaaaaatx47jkaaxxcvhqyerhev2r7xae4s6dsmbhx4vnqnovgv6vms5ytq")
                                                                                  .stage(GetSecretBundleRequest.Stage.Current)
                                                                                  .build();

            /* Send request to the Client */
            GetSecretBundleResponse response = client.getSecretBundle(getSecretBundleRequest);
            Base64SecretBundleContentDetails bundle = (Base64SecretBundleContentDetails) response.getSecretBundle().getSecretBundleContent();
            String password = new String(java.util.Base64.getDecoder().decode(bundle.getContent()), StandardCharsets.UTF_8);
            System.out.println(password);
        }
    }

    private static AbstractAuthenticationDetailsProvider getAuthenticationDetailsProvider() throws IOException {
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        return provider;
    }

    private static AbstractAuthenticationDetailsProvider getInstanceAuthenticationDetailsProvider() {
        InstancePrincipalsAuthenticationDetailsProvider provider = null;
        try {
            provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof ConnectException) {
                System.out.println(
                    "This sample only works when running on an OCI instance. Are you sure you’re running on an OCI instance? For more info see: https://docs.cloud.oracle.com/Content/Identity/Tasks/callingservicesfrominstances.htm");
            }
        }
        return provider;
    }
}
