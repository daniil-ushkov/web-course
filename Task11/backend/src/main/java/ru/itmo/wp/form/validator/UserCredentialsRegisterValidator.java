package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.UserCredentialsRegister;
import ru.itmo.wp.service.UserService;

import javax.annotation.Nonnull;

@Component
public class UserCredentialsRegisterValidator implements Validator {
    private final UserService userService;

    public UserCredentialsRegisterValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(@Nonnull Class<?> clazz) {
        return UserCredentialsRegister.class.equals(clazz);
    }

    public void validate(@Nonnull Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserCredentialsRegister enterForm = (UserCredentialsRegister) target;
            if (userService.findByLogin(enterForm.getLogin()) != null) {
                errors.rejectValue("password", "password.user-already-exists", "User with same login already exists");
            }
        }
    }
}
