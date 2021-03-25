package ru.itmo.wp.form.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Post.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (!errors.hasErrors()) {
            Post post = (Post) o;
            if (post.getNotParsedTags().isEmpty()) {
                post.setTags(List.of());
                return;
            }
            post.setTags(Arrays.stream(post.getNotParsedTags().split("\\s+"))
                    .distinct()
                    .map(s -> {
                        Tag tag = new Tag();
                        tag.setName(s);
                        return tag;
                    }).collect(Collectors.toList()));
            if (!post.getTags().stream().allMatch(t -> t.getName().matches("[a-zA-Z]{1,10}"))) {
                errors.rejectValue("notParsedTags", "notParsedTags.invalid-tags", "invalid tags");
            }
        }
    }
}
