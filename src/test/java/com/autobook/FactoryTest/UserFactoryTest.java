package com.autobook.FactoryTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Social.User.User;
import org.junit.jupiter.api.Test;
import com.autobook.Factory.UserFactory;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    private final UserFactory userFactory = new UserFactory();

    @Test
    void create_shouldCreateUserWithCorrectFields() {
        User user = userFactory.create(
                "anton",
                "Anton",
                "anton@gmail.com",
                "encodedPassword"
        );

        assertNotNull(user);
        assertEquals("anton", user.getUsername());
        assertEquals("Anton", user.getVisibleName());
        assertEquals("anton@gmail.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());

        assertEquals(UserRole.USER, user.getRole());
        assertEquals(PrivacyType.PUBLIC, user.getPrivacy());
    }
}