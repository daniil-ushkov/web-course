package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.UserCredentialsEnter;
import ru.itmo.wp.service.UserService;

import javax.annotation.Nonnull;

@Component
public class UserCredentialsEnterValidator implements Validator {
    private final UserService userService;

    public UserCredentialsEnterValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(@Nonnull Class<?> clazz) {
        return UserCredentialsEnter.class.equals(clazz);
    }

    public void validate(@Nonnull Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserCredentialsEnter enterForm = (UserCredentialsEnter) target;
            if (userService.findByLoginAndPassword(enterForm.getLogin(), enterForm.getPassword()) == null) {
                errors.rejectValue("password", "password.invalid-login-or-password", "Invalid login or password");
            }
        }
    }
}
