<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<#-- @ftlvariable name="error" type="java.util.Optional<String>" -->
<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "/tpl/head.ftl">
</head>
<body>
<h1>Log in</h1>
    <form role="form" action="/login" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="row">
            <div class="col-md-6 col-md-offset-3">
                <div class="row">
                    <div class="col-md-6">
                        <label for="username">User</label>
                        <input type="text" name="username" id="username" required autofocus/>
                    </div>
                </div>
            <div class="row">
                    <div class="col-md-6">
                        <label for="password">Password</label>
                        <input type="password" name="password" id="password" required/>
                    </div>
            </div>
                <div class="row">
                    <div class="col-md-6">
                        <label for="remember-me">Remember me</label>
                        <input type="checkbox" name="remember-me" id="remember-me"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <button type="submit">Sign in</button>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </form>
    <#if error??>
    <p>The email or password you have entered is invalid, try again.</p>
    </#if>
<a href="/user/registration">Register</a>
</div>
</body>
</html>