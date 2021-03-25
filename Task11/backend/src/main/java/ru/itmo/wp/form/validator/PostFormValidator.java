package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.service.JwtService;

import javax.annotation.Nonnull;

@Component
public class PostFormValidator implements Validator {
    private final JwtService jwtService;

    public PostFormValidator(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public boolean supports(@Nonnull Class<?> clazz) {
        return PostForm.class.equals(clazz);
    }

    public void validate(@Nonnull Object target, Errors errors) {
        if (!errors.hasErrors()) {
            PostForm postForm = (PostForm) target;
            if (jwtService.find(postForm.getJwt()) == null) {
                errors.rejectValue("jwt", "jwt.invalid-jwt", "Invalid JWT");
            }
        }
    }
}
