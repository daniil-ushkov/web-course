<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles datatable">
        <div class="caption">Articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Hide/Show</th>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="article_id"></td>
                    <td class="article_title"></td>
                    <td><input class = "hideOrShow" type="button" value="Hide"></td>
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
            const $noData = $(".articles .noData");
            $noData.find("td").text("Loading...");

            $.ajax({
                dataType: "json",
                data: {
                    action: "findAll"
                },
                success: function(response) {
                    $noData.hide();
                    response["articles"].forEach(function(article) {
                        const $tr = $($(".articles template").prop("content")).clone();
                        $tr.find(".article_id").text(article["id"]);
                        $tr.find(".article_title").text(article["title"]);
                        $tr.find(".hideOrShow").attr("value", article["hidden"] ? "Show" : "Hide");
                        $tr.find(".hideOrShow").attr("id", article["id"]);
                        $(".articles tbody").append($tr);
                    });
                    $(".articles .hideOrShow").click(function() {
                        $.ajax({
                            dataType: "json",
                            data: {
                                action: "hideOrShow",
                                hide: $main.find("[id=" + $(this).attr("id") + "]").attr("value"),
                                id: $(this).attr("id")
                            },
                            success: function (response) {
                                $main.find("[id=" + response["id"] + "]")
                                    .attr("value", response["hide"] ? "Show" : "Hide");
                                notify("Privacy changed");
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