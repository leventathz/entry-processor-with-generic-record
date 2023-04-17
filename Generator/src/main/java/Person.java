import java.time.LocalDate;

public record Person(
        Long id,
        String firstName,
        String lastName,
        LocalDate dob,
        String postCode,
        Double balance
) {
}
