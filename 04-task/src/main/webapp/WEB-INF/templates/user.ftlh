<#import "./common.ftlh" as c>

<@c.page>
    <#if user_id??>
        <#assign u = c.findBy(users, "id", user_id)!/>
    </#if>
    <#if handle??>
        <#assign u = c.findBy(users, "handle", handle)!/>
    </#if>
    <@c.ifHasContent u "user">
        <div class="user-menu">
            <div class="header">
                <@c.userlink u/>
            </div>
            <ul>
                <li>id: ${u.id}</li>
                <li>handle: ${u.handle}</li>
                <li>name: ${u.name}</li>
                <li><a href="/post?user_id=${u.id}">posts</a>: ${u.posts?size}</li>
            </ul>
            <img class="user-img" src="../../img/no-title.jpg" alt="no-title">
        </div>
        <div>
            <@arrow u.handle, -1>⟵</@arrow>
            <@arrow u.handle, 1>⟶</@arrow>
        </div>
    </@c.ifHasContent>
</@c.page>

<#macro arrow handle shift>
    <#assign other_user = c.findBy(users, "handle", handle, shift)!/>
    <#if other_user?has_content>
        <a class="arrow able" href="/user?handle=${other_user.handle}"><#nested/></a>
    <#else>
        <div class="arrow disabled"><#nested/></div>
    </#if>
</#macro>
