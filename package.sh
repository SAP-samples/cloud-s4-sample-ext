#!/bin/sh
export destinations='[{"name":"S4HANA_CLOUD","url":"https://destination_url.com","type":"HTTP","username":"<destination_user>","password":"<destination_password>"}]'
mvn -P prod clean package