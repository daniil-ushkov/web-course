<#-- @ftlvariable name="noticeForm" type="ru.itmo.wp.form.NoticeContent" -->
<#import "/spring.ftl" as spring>
<#import "commons.ftlh" as c>

<@c.page>
    <div class="create-notice notice-form-box">
        <div class="header">Create notice</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="createNotice"/>
                <div><textarea name="content" content="${noticeForm.content!}"></textarea></div>
                <@c.error "noticeForm.content"/>
                <div class="button"><input type="submit" value="Create"></div>
            </form>
        </div>
    </div>
</@c.page>