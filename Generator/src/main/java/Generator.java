import com.github.javafaker.Faker;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.time.ZoneId;
import java.util.Locale;
import java.util.stream.IntStream;

public class Generator {

    public static final int MAX_POCKET_MONEY = 10;

    public static void main(String[] args) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Person> map = client.getMap("people");

        // Put some entries in the map
        IntStream.rangeClosed(1, 100).mapToObj(operand ->
                        new Person(
                                faker.random().nextLong(Integer.MAX_VALUE),
                                faker.name().firstName(),
                                faker.name().lastName(),
                                faker.date().birthday(1, 99).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                faker.address().zipCode(),
                                faker.number().randomDouble(2,0, MAX_POCKET_MONEY))
                )
                .forEach(person -> {
                    System.out.printf("Putting a person with id=%12d person=%s\n", person.id(), person.toString());
                    map.put(person.id(), person);
                });

        client.shutdown();
    }

    static Faker faker = new Faker(Locale.UK);

}
