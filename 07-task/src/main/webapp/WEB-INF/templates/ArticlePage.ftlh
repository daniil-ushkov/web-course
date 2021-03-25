<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->

<#import "commons.ftlh" as c>

<@c.page>
    <div class="article-form-box">
        <form method="post" action="">
            <input type="hidden" name="action" value="submit"/>
            <div class="title"><input name="title"/></div>
            <div class="text"><textarea name="text"></textarea></div>
            <div class="button-field">
                <input type="submit" value="Submit">
            </div>
        </form>
    </div>
    <script>
        $(function () {
            $(".article-form-box form").submit(function () {
                const $form = $(this);
                const title = $form.find("input[name = title]").val();
                const text = $form.find("textarea[name = text]").val();
                $.ajax({
                    type: "POST",
                    url: "",
                    dataType: "json",
                    data: {action: "submit", userId: ${user.id}, title, text},
                    success: function (response) {
                        notify("Okay");
                        // No operations.
                    }
                });
                <#--ajax($form, {action: "submit", userId: ${user.id}, title, text});-->
                return false;
            })
        })
    </script>
</@c.page>
