#{if session.username && controllers.GAESecure.GAESecurity.invoke("check", _arg)}
    #{doBody /}
#{/if}