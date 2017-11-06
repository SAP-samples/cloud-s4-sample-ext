# S/4HANA Cloud extensions - Sales Geo Report sample app
This sample web application showcases an extension to an SAP S/4HANA Cloud system. It shows a bar graph about sales orders and shows the geographic origin on a map.

> **NOTE:** This sample app is based on and explained in detail in the [1RW set-up instructions guide](https://rapid.sap.com/bp/#/browse/scopeitems/1RW). There, you will find more details on the end to end steps of the ordering scenario example, e.g.:
> * Creation of a custom business object in SAP S/4HANA Cloud
> * Connection setup of the SAP S/4HANA Cloud system and SAP Cloud Platform (via a communication arrangement)
> * Adaption and deployment of the sample app

> The following README only shows a basic overview.


Prerequisites
-------------
1. We assume that you have access to an SAP S/4HANA Cloud system and an SAP Cloud Platform account
2. Install [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [Maven](http://maven.apache.org/download.cgi) and [Git](https://git-scm.com/downloads).
3. Prepare your S/4HANA Cloud system according to the [1RW set-up instructions guide](https://rapid.sap.com/bp/#/browse/scopeitems/1RW).
**Note:** to access the guide you need to have [an account for SAP Service Marketplace](https://websmp103.sap-ag.de/~sapidp/012002523100007691892016E/)


Download the sample app
------------------

```
git clone https://github.com/SAP/cloud-s4-sample-ext.git
cd cloud-s4-sample-ext/intereportapp
```


Connect it to your S/4HANA Cloud system
---------------------------------------

The sample app uses the Cloud Platform's Destination service to connect to your S/4HANA Cloud system. In your SAP Cloud Platform Cockpit create a  new destinations named `S4HANACloudEndpoint`:

Property		 | Value
-----------------|----------------------------------------------
Name:			 | `S4HANACloudEndpoint`
Type:			 | `HTTP`
URL:			 | `https://myXXXXXX-api.s4hana.ondemand.com`
Proxy type:		 | `Internet`
Authentication:	 | `BasicAuthentication`
User:			 | `EXTORDERD_API_USER`
Password:		 | `QGRlcl9tYXRoaWFz`

Note: User and password depends on the Communication Arrangement created in your S/4HANA Cloud system (see 1RW guide)

You might need to adapt the name of the Custom Business Object in [src/main/resources/application.properties](src/main/resources/application.properties) and adjust the service and resource information according to your S/4HANA Cloud system, in case you deviated from [1RW guide](https://rapid.sap.com/bp/#/browse/scopeitems/1RW).

```
# OData service of the Custom Business Object, created in S/4HANA Cloud to store one-time order data
s4cld.onetimecustomerrecord_servicepath=/sap/opu/odata/sap/YY1_CUSTOMERRECORD_CDS
s4cld.onetimecustomerrecord_resource=YY1_CUSTOMERRECORD
```


Deploy to SAP Cloud Platform (Neo)
----------------------------------
1. Package the application
   `mvn package`
2. Login to your SAP Cloud Platform cockpit (trail available https://cloudplatform.sap.com).
3. Goto "Java Applications".
4. Select the freshly built `intreportapp-0.0.1.war` (folder "target").
5. Choose `Java Web Tomcat 8` (runtime `JRE8`).


Run locally
--------
Set a new environment variable named `DESTINATIONS` as follows and supply the connection details and credentials for your S/4HANA Cloud system:
```
set DESTINATIONS=[{name: "S4HANACloudEndpoint", url: "https://myXXXXXX-api.s4hana.ondemand.com", username: "EXTORDERD_API_USER", password: "QGRlcl9tYXRoaWFzQGRlcl9tYXRoaWFz!"}]
```

Then run
```
mvn spring-boot:run -P local
```
Access `http://localhost:8080` with your browser of choice.


Limitations / Disclaimer
------------------------
Note: This sample code is primarily for illustration purposes and is not intended for productive usage. It solely shows basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html


Known Issues
------------
This sample application reads data from the SAP S/4HANA Cloud backend system and hence requires appropriate data maintained via the 'Sample app for external orders' (`extorderapp`).


How to obtain support
---------------------
File a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND`


### Copyright and License

Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.