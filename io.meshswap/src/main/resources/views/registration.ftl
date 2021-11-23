<#import "/spring.ftl" as spring/>
<html>
<head>
    <#include "/tpl/head.ftl">
</head>
<body>
<h1>Register new user</h1>
<#if spring.status??>
<#if spring.status.error>
<div class="errors">
    There were problems with the data you entered:
    <ul>
        <#list spring.status.errorMessages as error>
        <li>${error}</li>
    </#list>
</ul>
</div>
<#else>
<div class="errors">
    There are no errors.
</div>
</#if>
</#if>
<form action="" method="POST" enctype="utf8">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div>Email:
        <@spring.formInput "user.email" "" "text"/>
        <@spring.showErrors "<br>" />
    </div>
    <div>Name:
        <@spring.formInput "user.name" "" "text"/>
        <@spring.showErrors "<br>" />
    </div>
    <div>Password:
        <@spring.formInput "user.password" "" "password"/>
        <@spring.showErrors "<br>" />
    </div>
    <div>Repeat password:
        <@spring.formInput "user.matchingPassword" "" "password"/>
        <@spring.showErrors "<br>" />
    </div>

    <button type="submit">submit</button>
</form>
<a href="/login">login</a>
</body>
</html>