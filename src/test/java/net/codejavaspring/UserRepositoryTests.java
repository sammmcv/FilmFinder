package net.codejavaspring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // perfil de tests
@Rollback // limpiar test, aunque no es necesario por la db volatil
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setFirstName("Ravi");
        user.setLastName("Kumar");

        User savedUser = repo.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("ravikumar@gmail.com");
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setEmail("nam@codejava.net");
        user.setPassword("test123");
        user.setFirstName("Nam");
        user.setLastName("Codejava");

        repo.save(user);

        User foundUser = repo.findByEmail("nam@codejava.net");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("nam@codejava.net");
    }
}
