<#-- @ftlvariable name="talks" type="ru.itmo.wp.model.domain.Talk[]" -->
<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="userService" type="ru.itmo.wp.model.service.UserService" -->
<#-- @ftlvariable name="message" type="java.lang.String" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->

<#import "commons.ftlh" as c>
<@c.page>
    <div class="talk-box">
        <form method="post" action="">
            <input type="hidden" name="action" value="submit"/>
            <div>
                <textarea class="text-to-submit" name="message"></textarea>
            </div>
            <div class="user-to-submit">
                <select class="select" name="to">
                    <#list users as user>
                        <option>${user.login}</option>
                    </#list>
                </select>
                <input class="button" type="submit" value="Submit">
            </div>
        </form>
    </div>

    <ul class="talks">
        <#list talks as talk>
            <li class="talk">
                <#assign from = userService.findBy("id", talk.sourceUserId)!>
                <#assign to = userService.findBy("id", talk.targetUserId)!>
                <div class="time">${talk.creationTime}</div>
                <div class="from">
                    <#if from?has_content>
                        ${from.login}
                    <#else>
                        ${talk.sourceUserId}
                    </#if>
                </div>
                <div class="to">
                    <#if to?has_content>
                        ${to.login}
                    <#else>
                        ${talk.targetUserId}
                    </#if>
                </div>
                <div class="text">${talk.text}</div>
            </li>
        </#list>
    </ul>
</@c.page>
