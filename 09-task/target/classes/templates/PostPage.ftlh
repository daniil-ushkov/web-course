<#-- @ftlvariable name="post" type="ru.itmo.wp.domain.Post" -->
<#-- @ftlvariable name="comment" type="ru.itmo.wp.domain.Comment" -->


<#import "/spring.ftl" as spring>
<#import "commons.ftlh" as c>

<@c.page>
    <#if post??>
        <@c.post post=post/>
        <#list post.comments as comment>
            <@showComment comment=comment/>
        </#list>
        <@commentForm/>
    <#else>
        No such post.
    </#if>
</@c.page>

<#macro commentForm>
    <#if user??>
        <div class="comment-form">
            <div class="body">
                <form method="post" action="">
                    <input type="hidden" name="action" value="writePost"/>
                    <div class="field">
                        <div class="name">
                            <label for="text">Write your comment</label>
                        </div>
                        <div class="value">
                            <textarea id="text" name="text">${comment.text!}</textarea>
                        </div>
                        <@c.error "comment.text"/>
                    </div>
                    <div class="button-field">
                        <input type="submit" value="Write">
                    </div>
                </form>
            </div>
        </div>
    </#if>
</#macro>

<#macro showComment comment>
    <div class="comment">
        <div class="author">By ${comment.user.login}</div>
        <div class="text">${comment.text}</div>
    </div>
</#macro>
