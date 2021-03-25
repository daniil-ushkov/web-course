<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-vs-coronavirus-65.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user true/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href" : "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li>
                        <a <#if item.href == currentPage>class="underlined"</#if> href="${item.href}">${item.name}</a>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#macro sidebar>
    <aside>
        <#list posts?reverse as p>
        <section>
            <div class="header">Post ${p.id}</div>
            <div class="body">
                <@cut p.text false/>
            </div>
            <div class="footer">
                <a href="/post?post_id=${p.id}">View all</a>
            </div>
        </section>
        </#list>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2020 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#macro userlink user nameOnly=false>
    <a class="username<#if !nameOnly> ${user.color}</#if>" href="/user?handle=${user.handle}">${user.handle}</a>
</#macro>

<#function findBy items key value shift=0>
    <#list items as item>
        <#if item[key] == value>
            <#return items[item?index + shift]/>
        </#if>
    </#list>
</#function>

<#macro post p full=false>
    <article>
        <a class="title" href="/post?post_id=${p.id}">${p.title}</a>
        <div class="information">By <@userlink findBy(users, "id", p.user_id)/></div>
        <div class="body">
            <@cut p.text full/>
        </div>
        <ul class="attachment">
            <li>Announcement of <a href="#">${p.title}</a></li>
        </ul>
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
</#macro>

<#macro cut text full>
    <#if full || text?length <= 250>
        ${text}
    <#else>
        ${text?substring(0, 251)}...
    </#if>
</#macro>

<#macro iterate_posts ps>
    <#list ps?reverse as p>
        <@post p/>
    </#list>
</#macro>

<#macro ifHasContent item itemName>
    <#if item?has_content>
        <#nested/>
    <#else>
        No such ${itemName}
    </#if>
</#macro>
