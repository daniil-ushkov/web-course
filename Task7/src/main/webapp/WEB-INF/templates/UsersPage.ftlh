<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <template class = "admin_head_col">
                    <th>Admin</th>
                </template>
            </tr>
            </thead>
            <tbody>
            <template class="line">
                <tr>
                    <td class="user_id"></td>
                    <td class="user_login"></td>
                    <template class="admin_col">
                        <td class="user_admin"><a></a></td>
                    </template>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $main = $(this);
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");

            $.ajax({
                dataType: "json",
                data: {
                    action: "findAll"
                },
                success: function(response) {
                    $noData.hide();
                    if (response["admin"]) {
                        const $tr_head = $main.find(".users thead tr");
                        const $th = $($main.find(".users thead .admin_head_col").prop("content")).clone();
                        $tr_head.append($th);
                    }
                    response["users"].forEach(function(user) {
                        const $tr = $($(".users .line").prop("content")).clone();
                        $tr.find(".user_id").text(user["id"]);
                        if (response["admin"]) {
                            $tr.find(".user_login").text(user["login"] + (user["admin"] ? " (admin)" : ""))
                        } else {
                            $tr.find(".user_login").text(user["login"]);
                        }
                        $tr.find("tr").attr("data-userId", user["id"]);
                        $(".users tbody").append($tr);

                        if (response["admin"]) {
                            const $td = $($(".users .admin_col").prop("content")).clone();
                            const $a = $td.find("a");
                            if (user["id"] !== response["userId"]) {
                                $a.attr("href", "#");
                                $a.text(user["admin"] ? "disable" : "enable");
                                $a.click(function () {
                                    $.ajax({
                                        dataType: "json",
                                        data: {
                                            action: "updateAdmin",
                                            admin: $main.find("[data-userId=" + user["id"] + "]").find("a").text(),
                                            userId: user["id"]
                                        },
                                        success: function (response) {
                                            const $found_tr = $main.find("[data-userId=" + user["id"] + "]");
                                            $found_tr.find(".user_login").text(user["login"] + (response["admin"] ? " (admin)" : ""))
                                            $found_tr.find("a").text(response["admin"] ? "disable" : "enable");
                                        },
                                        type: "POST"
                                    })
                                })
                            } else {
                                $a.text("YOU");
                            }
                            $main.find("[data-userId=" + user["id"] + "]").append($td);
                        }
                    });
                    $(".users tr").click(function() {
                        $.ajax({
                            dataType: "json",
                            data: {
                                action: "findUser",
                                userId: $(this).attr("data-userId")
                            },
                            success: function (response) {
                                notify(response["user"].creationTime);
                            },
                            type: "POST"
                        })
                    });
                },
                type: "POST"
            });
        })
    </script>
</@c.page>
