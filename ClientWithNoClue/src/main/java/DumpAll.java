import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.genericrecord.GenericRecord;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DumpAll {

    public static void main(String[] args) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Object> map = client.getMap("people");
        dump(map);
        client.shutdown();
    }

    private static void dump(IMap<Long, Object> map) {
        System.out.println("Dumping the map:");
        Set<Long> keys = map.keySet();
        AtomicInteger count = new AtomicInteger(0);
        keys.forEach(key -> {
            GenericRecord record = (GenericRecord) map.get(key);
            Long id = record.getNullableInt64("id");
            String firstName = record.getString("firstName");
            String lastName = record.getString("lastName");
            LocalDate dob = record.getDate("dob");
            String postCode = record.getString("postCode");
            Double balance = record.getNullableFloat64("balance");
            System.out.printf("%4d - id=%20d name=%-12s balance=%5.2f\n", count.incrementAndGet(), id, firstName, balance);
        });
        System.out.printf("Dumped %d entries.\n", count.get());
    }
}