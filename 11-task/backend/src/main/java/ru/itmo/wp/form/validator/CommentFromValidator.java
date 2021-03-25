package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.CommentForm;
import ru.itmo.wp.service.JwtService;

import javax.annotation.Nonnull;

@Component
public class CommentFromValidator implements Validator {
    private final JwtService jwtService;

    public CommentFromValidator(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public boolean supports(@Nonnull Class<?> clazz) {
        return CommentForm.class.equals(clazz);
    }

    public void validate(@Nonnull Object target, Errors errors) {
        if (!errors.hasErrors()) {
            CommentForm commentForm = (CommentForm) target;
            if (jwtService.find(commentForm.getJwt()) == null) {
                errors.rejectValue("jwt", "jwt.invalid-jwt", "Invalid JWT");
            }
        }
    }
}