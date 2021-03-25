<#import "commons.ftlh" as c>

<@c.page>
    <div class="wall">
        No data
    </div>

    <template class="blog">
        <div>
            <article>
                <div class="title"></div>
                <div class="by"></div>
                <div class="date"></div>
                <div class="body"></div>
                <div class="footer">
                    <div class="left">
                        <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                        <span class="positive-score">+173</span>
                        <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                    </div>
                    <div class="right">
                        <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                        2 days ago
                        <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                        <a href="#">68</a>
                    </div>
                </div>
            </article>
        </div>
    </template>


    <script>
    $(function () {
        const $wall = $(".wall");
        $wall.text("Loading...");
        $.ajax({
            dataType: "json",
            data: {
                action: "findAll"
            },
            success: function(response) {
                $wall.text("");
                response["articles"].forEach(function(article) {
                    const $tpl = $($(".blog").prop("content")).clone();
                    $tpl.find(".title").text(article["article"]["title"]);
                    $tpl.find(".by").text(article["userName"]);
                    $tpl.find(".date").text(article["article"]["creationTime"]);
                    $tpl.find(".body").text(article["article"]["text"]);
                    $wall.append($tpl);
                });
            },
            type: "POST"
        });
    })
</script>
</@c.page>