import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BoostPoors implements Serializable {

    public static void main(String[] args) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient(getClientConfig());
        System.out.println("Helping with the poor");
        IMap<Long, Object> map = client.getMap("people");
        map.executeOnEntries((entry) -> {
            GenericRecord genericRecord = (GenericRecord) entry.getValue();
            Double newBalance = genericRecord.getFloat64("balance") + 10.0;
            GenericRecord modifiedGenericRecord = genericRecord.newBuilderWithClone()
                    .setNullableFloat64("balance", newBalance)
                    .build();
            entry.setValue(modifiedGenericRecord);
            System.out.printf("key=%d new balance=%5.2f\n", entry.getKey(), newBalance);
            return entry;
        }, entry -> ((GenericRecord) (entry.getValue())).getFloat64("balance") < 5.0);
        System.out.println("Done!");
        client.shutdown();
    }

    public static ClientConfig getClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
        clientUserCodeDeploymentConfig.addClass(BoostPoors.class);
        clientUserCodeDeploymentConfig.setEnabled(true);
        clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig);
        return clientConfig;
    }
}