<#-- @ftlvariable name="user" type="ru.itmo.wp.domain.User" -->
<#-- @ftlvariable name="users" type="ru.itmo.wp.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <#if user??>
                    <th>Enable/disable</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <#if users?size==0>
                <tr class="noData">
                    <td colspan="4">
                        No data
                    </td>
                </tr>
            <#else>
                <#list users as u>
                    <tr>
                        <td class="id">${u.id}</td>
                        <td class="login"><a href="/user/${u.id}">${u.login}</a></td>
                        <td class="creationTime">${u.creationTime}</td>
                        <#if user??><td class="enableOrDisable">
                            <#if u.id != user.id>
                                <#if u.disabled>
                                    <#assign val = "enable">
                                <#else>
                                    <#assign val = "disable">
                                </#if>
                                <form method="get" action="/${val}/${u.id}">
                                    <intput type="hidden" value=""></intput>
                                    <input type="submit" value="${val}">
                                </form>
                            <#else>
                                YOU
                            </#if>

                            </td></#if>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</@c.page>
