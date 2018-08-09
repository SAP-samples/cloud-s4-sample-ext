# S/4HANA Cloud extensions - Timesheet Data Export

This is a side-by-side application which allows you to read SAP S/4HANA Cloud time-recording data specific to the user that is currently logged on. The time-recording information can be filtered by certain attributes such as customer, project, or work package. Time-recording entries can be exported to Microsoft Excel or printed to a PDF document (optionally along with a signature captured via an SAP UI5 signature pad).

> **NOTE:** This app is based on and explained in detail in the [Set-upGuide_Timesheet Data Export](https://help.sap.com/viewer/c2f4816a64a642528989c94d3fb91d92/SHIP/en-US). There, you will find more details on the end to end steps of the business event handling scenario example, e.g.:
> * Implementation steps on SAP S/4 HANA Cloud and SAP Cloud Platform 
> * Configuration and deployment of the apps
> * Testing the scenario

> **NOTE:** This scenario includes two apps: a Java backend and a SAPUI5 frontend. The Java app is located in the repository's root folder, the SAPUI5 app in the subfolder timesheet-export-frontend.


Set-up Instructions Guide
-------------
https://help.sap.com/viewer/c2f4816a64a642528989c94d3fb91d92/SHIP/en-US

Requirements
-------------
1. We assume that you have access to an SAP S/4HANA Cloud system and an SAP Cloud Platform account
2. Install [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [Maven](http://maven.apache.org/download.cgi) and [Git](https://git-scm.com/downloads).
3. Prepare your S/4HANA Cloud system according to the [Set-upGuide_Timesheet](https://help.sap.com/viewer/c2f4816a64a642528989c94d3fb91d92/SHIP/en-US).

Troubleshooting
------------

If you work with your [SAP Cloud Platform Trial account](https://account.hanatrial.ondemand.com/), you must add the following 2 properties to the destination so that the connection to SAP S/4HANA Cloud works:  

      proxyHost =	proxy-trial.od.sap.biz  
      proxyPort =	8080
    

Limitations / Disclaimer
------------------------
Note: This sample code is primarily for illustration purposes and is not intended for productive usage. It solely shows basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html

How to obtain support
---------------------
File a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND-EXT`


### Copyright and License

Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
