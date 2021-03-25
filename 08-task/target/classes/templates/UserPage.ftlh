<#-- @ftlvariable name="user" type="ru.itmo.wp.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <div>id=${user.id}</div>
        <div>login=${user.login}</div>
        <div>creationTime=${user.creationTime}</div>
    <#else>
        <div>No such user.</div>
    </#if>
</@c.page>