REM Used for destinations to S4-system
SET destinations=[{"name":"S4HANA_CLOUD","url":"https://destination_url.com","type":"HTTP","username":"<destination_user>","password": "<destination_password>"}]
mvn -P dev spring-boot:run
