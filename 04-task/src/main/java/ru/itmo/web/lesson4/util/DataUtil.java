package ru.itmo.web.lesson4.util;

import ru.itmo.web.lesson4.model.Post;
import ru.itmo.web.lesson4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<Post> POSTS = Arrays.asList(
            new Post(1, "Codeforces Test Round",
                    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. " +
                            "Alias debitis dolore dolorum facere hic nisi perferendis rerum sed. " +
                            "Accusamus, delectus ducimus facere fuga illum quaerat ratione voluptatibus! " +
                            "Autem cum, cupiditate dolor dolore, earum excepturi, illum iure molestias " +
                            "nostrum odio perferendis porro quasi qui quo recusandae repellendus " +
                            "similique sint vel. Ab accusantium adipisci debitis distinctio dolorum, " +
                            "eligendi error est fugit inventore labore laudantium nam nesciunt nihil " +
                            "nobis non nulla odio praesentium quae quia quibusdam quidem quod rem " +
                            "repudiandae saepe, sit totam vel voluptatem. ",
                    15),
            new Post(2, "Codeforces Test Round",
                    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. " +
                            "Alias debitis dolore dolorum facere hic nisi perferendis rerum sed. " +
                            "Accusamus, delectus ducimus facere fuga illum quaerat ratione voluptatibus! " +
                            "Autem cum, cupiditate dolor dolore, earum excepturi, illum iure molestias " +
                            "nostrum odio perferendis porro quasi qui quo recusandae repellendus " +
                            "similique sint vel. Ab accusantium adipisci debitis distinctio dolorum, " +
                            "eligendi error est fugit inventore labore laudantium nam nesciunt nihil " +
                            "nobis non nulla odio praesentium quae quia quibusdam quidem quod rem " +
                            "repudiandae saepe, sit totam vel voluptatem. ",
                    13),
            new Post(3, "Codeforces Test Round",
                    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. " +
                            "Alias debitis dolore dolorum facere hic nisi perferendis rerum sed. " +
                            "Accusamus, delectus ducimus facere fuga illum quaerat ratione voluptatibus! " +
                            "Autem cum, cupiditate dolor dolore, earum excepturi, illum iure molestias " +
                            "nostrum odio perferendis porro quasi qui quo recusandae repellendus " +
                            "similique sint vel. Ab accusantium adipisci debitis distinctio dolorum, " +
                            "eligendi error est fugit inventore labore laudantium nam nesciunt nihil " +
                            "nobis non nulla odio praesentium quae quia quibusdam quidem quod rem " +
                            "repudiandae saepe, sit totam vel voluptatem. ",
                    1)
    );

    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov",
                    Arrays.asList(POSTS.get(2)), User.Color.RED),
            new User(6, "pashka", "Pavel Mavrin",
                    List.of(), User.Color.RED),
            new User(9, "geranazarov555", "Georgiy Nazarov",
                    List.of(), User.Color.BLUE),
            new User(11, "tourist", "Gennady Korotkevich",
                    List.of(), User.Color.RED),
            new User(13, "kgeorgiy", "Georgiy Korneev",
                    Arrays.asList(POSTS.get(1)), User.Color.RED),
            new User(15, "_udav_", "Ushkov Daniil",
                    Arrays.asList(POSTS.get(0)), User.Color.GREEN)
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);
        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}
