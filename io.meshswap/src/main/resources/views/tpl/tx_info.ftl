<div class="txdiv">
    <table class="table table-striped" cellpadding="0" cellspacing="0"
           style="padding:0px;float:left;margin:0px;width:100%">
        <tr>
            <th colspan="3" align="left"><a class="hash-link"
                                            href="/tx/${tx.id}">${tx.id}</a>
                <span class="pull-right">2010-12-29 11:57:43</span></th>
        </tr>
        <tr>
            <td class="txtd hidden-phone mobile-f12 stack-mobile">
                <#if tx.inAddresses?size != 0>
                    <#list tx.inAddresses as inAddr>
                        <a href="/address/${inAddr.address}">${inAddr.address}</a><br/>
                    </#list>
                <#else>
                        <b>No Inputs (Newly Generated Coins)</b>
                </#if>
            </td>
            <td class="hidden-phone tx-arrow-col"><img src="/image/arrow_right_green.png"/></td>
            <td class="txtd mobile-f12 stack-mobile">
                <#list tx.voutList as vout>
                    <#list vout.addressList as addr>
                        <a href="/address/${addr.address}">${addr.address}</a>
                    </#list>
                    <span class="pull-right hidden-phone"><span data-c="556000000" data-time="1293623863000">${vout.value} BTC</span></span><br/>
                </#list>
            </td>
        </tr>
    </table>
    <div style="padding-bottom:30px;width:100%;text-align:right;clear:both">
        <button class="btn btn-success cb"><span data-c="5000000000" data-time="1293623863000">50 BTC</span></button>
    </div>
</div>