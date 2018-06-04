# S/4HANA Cloud extensions - Quick Time Entry for SAP S/4HANA Time Recording (SAP S/4HANA Cloud SDK)

This web application showcases an extension to an SAP S/4HANA Cloud system.
It allows users to read and write their working time in a fast and efficient way.

> **NOTE:** This app is based on and explained in detail in the [Set-upGuide_Timesheet](https://help.sap.com/viewer/11a6aeea94214fae9fe26afbdb9291fc/SHIP/en-US). There, you will find more details on the end to end steps of the business event handling scenario example, e.g.:
> * Connection setup of the SAP S/4HANA Cloud system and SAP Cloud Platform (via a communication arrangement)
> * Adaption and deployment
> * Identity Provider
>
> The following README only shows a basic overview

Requirements
-------------
1. We assume that you have access to an SAP S/4HANA Cloud system and an SAP Cloud Platform account
2. Install [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [Maven](http://maven.apache.org/download.cgi) and [Git](https://git-scm.com/downloads).
3. Prepare your S/4HANA Cloud system according to the [Set-upGuide_Timesheet](https://help.sap.com/viewer/11a6aeea94214fae9fe26afbdb9291fc/SHIP/en-US).

Connect it to your S/4HANA Cloud system
---------------------------------------

In this scenario, a Java application in front with a SAPUI5 application it is used to read and write timesheet data from SAP S/4HANA Cloud System. For that purpose, we use standard, resource-based APIs of SAP S/4HANA.

To allow inbound communication to the SAP S/4HANA tenant, we need to create a communication arrangement first. The communication arrangement defines which system (communication system) and which user can call which APIs (communication scenarios). 
In this example, you create a communication arrangement and allow access to the standard Manage Workforce Timesheet API (SAP_COM_0027) using a technical user.

> **NOTE:** Please follow the steps described in the [Set-upGuide_Timesheet](https://help.sap.com/viewer/11a6aeea94214fae9fe26afbdb9291fc/SHIP/en-US) to set up a communication system, communication arrangements and communication user in your S/4HANA tenant.

## Downloading the Code

To download this project run this command.
```
git clone https://github.com/SAP/cloud-s4-sample-ext.git  
cd cloud-s4-sample-ext  
git checkout timesheet-neo
```

Or if you want to clone the single branch only:
```
git clone -b timesheet-neo --single-branch git://github.com/SAP/cloud-s4-sample-ext.git  
cd cloud-s4-sample-ext
```

The .war archive is located in /target folder.

## Build it

To package this project to a deployable .war archive run this command for Windows

```sh
package.bat
```

or this one for Unix based operating systems.

```sh
./package.sh
```

The .war archive is located in /target folder.

Deploy to SAP Cloud Platform
----------------------------
1.	Log on to your SAP Cloud Platform cockpit.
2.	Go to Java Applications. Choose Deploy Application. 
3.	Choose Browse to locate the WAR file you have created.
4.	Enter an application name.
5.	Select Java Web Tomcat 8 from the Runtime Name. 
6.	Now, the remaining parts of the configuration are automatically filled in correctly.
7.	Choose Deploy. 
8.	Choose Done.

> **NOTE:** Before you can start the application, you need to create a destination first. Refer to the next section.

Create a Destination
----------------------------
Destinations are used for the outbound communication of your application to a remote system (which is, in this case, the SAP S/4HANA Cloud system). To create a destination, you enter a name, the URL of the SAP S/4HANA Cloud system, the authentication type, and some other configuration data.	Maintain the properties as follows:

Property | Value
------------ | -------------
Name | S4HANA_CLOUD
Type | http
Description | <e.g. the name of your communication arrangement>
URL | <the base URL to your S/4HANA Cloud system; note the “-api”; e.g. https://myXXXXXX-api.s4hana.ondemand.com>
Proxy type | Internet
Authentication | BasicAuthentication
User | <the user you created; e.g. TIMESHEET>
Password | \<the password you created\>


> **NOTE:** The name of the destination should be exactly “S4HANA_CLOUD”.
> The user and password depend on the communication system and user that have been created in your SAP S/4HANA Cloud system.
> The URL depends on the communication arrangement created in your SAP S/4HANA Cloud system.


Troubleshooting
------------

    In case you hit the error page, please go through the exception
    trace for more details. Exception trace can be found under SAP
    CloudPlatform Cockpit > Java applications > time-backend >
    Logging > **Default Traces** .
 
    
    **Known Exceptions:**
    ODataException connectivity : Re-check your Destinations S4HANA_CLOUD and the S/4HANA System.
    FileNotFoundException : Make sure that you don't have directory names that contain whitespaces in the project path. eg. "s4 cld ext timesheet"
    
    If you work with your [SAP Cloud Platform Trial account](https://account.hanatrial.ondemand.com/), you must add the following 2 properties to the destination so that the connection to SAP S/4HANA Cloud works:  

      proxyHost =	proxy-trial.od.sap.biz  
      proxyPort =	8080
    


Limitations / Disclaimer
------------------------
Note: This sample code is primarily for illustration purposes and is not intended for productive usage. It solely shows basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html

How to obtain support
---------------------
File a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND`

### Copyright and License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
