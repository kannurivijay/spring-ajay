package id.my.hendisantika.compose.controller;

import id.my.hendisantika.compose.entity.User;
import id.my.hendisantika.compose.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void createDelegatesToServiceAndReturnsCreatedUser() {
        UserService userService = mock(UserService.class);
        UserController controller = new UserController(userService);

        User request = new User("Ajay", "Kumar", "ajay@example.com");
        User expected = new User();
        expected.setId(1L);
        expected.setFirstName("Ajay");
        expected.setLastName("Kumar");
        expected.setEmail("ajay@example.com");

        when(userService.create(request)).thenReturn(expected);

        User actual = controller.create(request);

        assertSame(expected, actual);
        verify(userService).create(request);
    }
}
