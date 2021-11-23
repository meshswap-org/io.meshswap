<html>
    <head>
        <title>Block explorer</title>
        <link href="/css/style.css" rel="stylesheet"/>
        <link href="/css/bootstrap.min.css" rel="stylesheet"/>
    </head>
    <body>
        <#include "/tpl/header.ftl">
        <section class="network-info">
            <div class="container">
                <div class="row border-btm">
                    <form role="form" action="/swap/init/create" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="row">
                        <div class="col-md-6 col-md-offset-3">
                            <div class="row">
                                <div class="col-md-6">
                                    <label for="initiator">Initiator Address</label>
                                    <input type="text" name="initiator" id="initiator" required autofocus/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <label for="participant">Participant Address</label>
                                    <input type="text" name="participant" id="participant" required/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <label for="amount">Amount</label>
                                    <input type="text" name="amount" id="amount" required/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <button type="submit">Generate TX</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <#if initTx??>
                <div class="row border-btm">
                    <div class="col">Init TX </div>
                </div>
                <div>
                    <div><#if initTx.secret??>Secret: ${initTx.secret}</#if></div>
                    <div><#if initTx.secretHash??>Hash of secret: ${initTx.secretHash}</#if></div>
                    <div><#if initTx.contractScript??>Script: ${initTx.contractScript}</#if></div>
                    <div><#if initTx.contractTx??>Tx: ${initTx.contractTx}</#if></div>
                    <div>Signed tx: ${initTx.signed?c}</div>
                </div>
            </div>
        </#if>
        </section>
        <section class="block-info">
            <div class="container">
            </div>
        </section>
    </body>
</html>