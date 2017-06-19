# S/4HANA Cloud Extensions - Sample app for external orders
A sample web application as extension to an SAP S/4HANA Cloud system to list products (material master) for external users. and allows to order them (creation of a sales order in S/4) as external user.

Note, this sample app is based and explained in detail in the [1RW guide](https://rapid.sap.com/bp/#/browse/search?q=1RW). The following README only shows a basic overview.  


Prerequisites
-------------
1. Install JDK7 or 8, Maven and Git  
2. Prepare your S/4HANA Cloud system according to the [1RW guide](https://rapid.sap.com/bp/#/browse/search?q=1RW)


Get the sample app
------------------

```
git clone https://github.wdf.sap.corp/bestpractices/s4-sidebyside-extorderapp-neo.git  
```


Connect it to your S/4HANA Cloud system
---------------------------------------
Adapt [src/main/resources/application.properties](src/main/resources/application.properties) and adjust the connection parameters according to your S/4HANA Cloud system. 


```      
# Base URL of the S/4HANA Cloud System
s4cld.api_host=https://myXXXXXX-api.s4hana.ondemand.com
    
# API User (to be maintained in the Communication System in S/4HANA Cloud)
s4cld.api_user=extorderapp_api_user
    
# API Password 
s4cld.api_pass=QGRlcl9tYXRoaWFz
    
# OData service of the Custom Business Object, created in S/4HANA Cloud to store one-time order data  
s4cld.onetimecustomerrecord_servicepath=/sap/opu/odata/sap/YY1_CUSTOMERRECORD_CDS
s4cld.onetimecustomerrecord_resource=YY1_CUSTOMERRECORD
```


Build it
--------

```
mvn package
```


Deploy to SAP Cloud Platform
----------------------------
1. Login to your SAP Cloud Platform cockpit (trail available https://cloudplatform.sap.com)
2. Goto "Java Applications"
3. Select the freshly built `extorderapp-0.0.1-SNAPSHOT.war` (folder "target")
  
   Choose `Java Web Tomcat 7` (runtime `JRE7`) 


Disclaimer
----------
Note: This sample code is primarily for illustration purposes and is not intended for production. It solely shows basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose of simplicity. For detailed information on development on the SAP Cloud Platform please consult https://cloudplatform.sap.com/developers.html  



### Copyright and License

```
© 2017 [SAP SE](http://www.sap.com/)

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this work except in compliance with the License. 
You may obtain a copy of the License in the LICENSE file, or at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.