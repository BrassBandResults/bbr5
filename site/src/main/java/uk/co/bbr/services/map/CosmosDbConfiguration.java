package uk.co.bbr.services.map;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosClientTelemetryConfig;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("prod")
@EnableCosmosRepositories(basePackages = "uk.co.bbr.services.map.repo")
public class CosmosDbConfiguration extends AbstractCosmosConfiguration {

    @Value("${azure.cosmosdb.uri}")
    private String uri;

    @Value("${azure.cosmosdb.access-key}")
    private String key;

    @Value("${azure.cosmosdb.database}")
    private String dbName;

    private AzureKeyCredential azureKeyCredential;

    // https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/azure-spring-data-cosmos

    @Bean
    public CosmosClientBuilder getCosmosClientBuilder() {
        this.azureKeyCredential = new AzureKeyCredential(key);
        DirectConnectionConfig directConnectionConfig = new DirectConnectionConfig();
        GatewayConnectionConfig gatewayConnectionConfig = new GatewayConnectionConfig();
        return new CosmosClientBuilder()
            .endpoint(uri)
            .credential(azureKeyCredential)
            .directMode(directConnectionConfig, gatewayConnectionConfig)
            .clientTelemetryConfig(
                new CosmosClientTelemetryConfig()
                    .diagnosticsThresholds(
                        new CosmosDiagnosticsThresholds()));
    }

    @Override
    public CosmosConfig cosmosConfig() {
        return CosmosConfig.builder().build();
    }

    @Override
    protected String getDatabaseName() {
        return this.dbName;
    }
}
