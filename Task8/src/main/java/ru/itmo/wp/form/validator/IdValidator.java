package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.Id;

@Component
public class IdValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return Id.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!(target instanceof Id)) {
            return;
        }
        if (!errors.hasErrors()) {
            Id id = (Id) target;
            try {
                id.value();
            } catch (NumberFormatException e) {
                errors.rejectValue("id", "id.not-an-id", "not an id");
            }
        }
    }
}
