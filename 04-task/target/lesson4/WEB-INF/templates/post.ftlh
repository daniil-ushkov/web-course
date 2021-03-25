<#import "./common.ftlh" as c>

<@c.page>
    <#if post_id??>
        <#assign p = c.findBy(posts, "id", post_id)!/>
        <@c.ifHasContent p "post">
            <@c.post p=p full=true/>
        </@c.ifHasContent>
    </#if>
    <#if user_id??>
        <#assign u = c.findBy(users, "id", user_id)!/>
        <@c.ifHasContent u "user">
            <@c.iterate_posts u.posts/>
        </@c.ifHasContent>
    </#if>
</@c.page>