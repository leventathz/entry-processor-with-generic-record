package org.example

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.nio.serialization.genericrecord.GenericRecord
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

object BoostPoorsInKotlin : Serializable {
    @JvmStatic
    fun main(args: Array<String>) {
        val client = HazelcastClient.newHazelcastClient(config)
        go(client)
        client.shutdown()
    }

    private fun go(client: HazelcastInstance) {
        val map = client.getMap<Long, Any>("people")
        val keys: Set<Long> = map.keys
        val count = AtomicInteger(1)
        map.executeOnEntries({
            val genericRecord = it.value as GenericRecord
            val newBalance = genericRecord.getFloat64("balance") + 10.0
            val modifiedGenericRecord = genericRecord.newBuilderWithClone()
                .setNullableFloat64("balance", newBalance)
                .build()
            it.setValue(modifiedGenericRecord)
            System.out.printf("key=%d new balance=%5.2f\n", it.key, newBalance)
        }, {
            val record = it.value as GenericRecord
            record.getFloat64("balance") < 2L
        })
    }

    val config: ClientConfig
        get() {
            val clientConfig = ClientConfig()
            val clientUserCodeDeploymentConfig = ClientUserCodeDeploymentConfig()
            clientUserCodeDeploymentConfig.addClass(BoostPoorsInKotlin::class.java)
            clientUserCodeDeploymentConfig.setEnabled(true)
            clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig)
            //        clientConfig.getNetworkConfig().addAddress("localhost:5701","localhost:5702");
            return clientConfig
        }
}