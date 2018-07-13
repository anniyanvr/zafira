# Client setup 

#### Access token
Zafira provides REST API to track test automation results (Swagger is avaible by http://localhost:8080/zafira-ws/swagger-ui.html). You have diffent options of integration of your test client. Carina automation framework has integration with Zafira under the hood. Regardless of integration flow you selected, first of all you will need access token generated. Navigate to **Username** > **Profile** in top navigation menu, scroll down and generate new access token.

<p align="center">
  <img src="../img/access_token.png">
</p>

Zafira uses stateless authenticatication using [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token) technology. In general clients stores refresh token that is used to generate access token with 5-hours expiration term. All HTTP calls that requires authorization context should contain haeder:
```
Authorizarion: Bearer <access_token>
```

#### Integration with test frameworks
The easiest option to use Zafira with test automation is Carina framework. Carina tracks automation results under the hood, all you need to do is setting up of correct access token in [zafira.properties](https://github.com/qaprosoft/carina-demo/blob/master/src/main/resources/zafira.properties) file.
```
zafira_enabled=true
zafira_service_url=http://localhost:8080/zafira-ws
zafira_access_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3IiwicGFzc3dvcmQiOiJiMDdSaG5pZVk2NTlncU04VEFrOWtkK2F4ZXE3U...
zafira_project=UNKNOWN
zafira_rerun_failures=false
zafira_report_emails=
zafira_configurator=com.qaprosoft.carina.core.foundation.report.ZafiraConfigurator
```
Make sure that **zafira_enabled=true** and **zafira_service_url** specifies correct zafira URL. By default **zafira_project=UNKNOWN** but using admin user you are capable to create multiple projects via Zafira UI (top menu > Project > + Create). When new project created you may override **zafira_project** property and track resutls in appropriate context.

<p align="center">
  <img src="../img/flow_diagram.png">
</p>