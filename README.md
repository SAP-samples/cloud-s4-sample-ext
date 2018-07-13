# SAP S/4HANA Cloud extensions - Sample scenarios

The sample scenarios/applications in this repository showcase various (side-by-side) extension scenarios to an SAP S/4HANA Cloud system. **Please check out the branches of the repository for the diverse sample apps.**
Side-by-side applications are standalone web-based applications, running on the SAP Cloud Platform. Find an overview and more info on [SAP Extensibility Explorer for SAP S/4HANA Cloud](https://www.sap.com/extends4).

##### Note:
All samples require an SAP S/4HANA Cloud system. Detailed instructions about the scenarios, underlying business processes and how to set up of the SAP S/4HANA Cloud system as well as how to implement the apps can be found in the set-up instruction guides. You'll find a link in the readme files of the individual apps in the branches (or [here](https://help.sap.com/viewer/p/SAP_EXTENSIBILITY_EXPLORER).
Since most of the samples also show a combination of SAP S/4HANA in-app extensions, several roles are required on the SAP S/4HANA Cloud system.

# Scenario Overview

| Scenario        | Overview           | Link  |
| ------------- |:-------------:| -----:|
| External Order Scenario | This application shows how to reach external users and allow them to list products and place sales orders. <br> There are two versions available. One is using the SAP S/4HANA Cloud SDK which helps you to connect to S/4HANA and provides the public APIs directly in JAVA (extorderapp). The other version shows the manual approach not using the SAP S/4HANA Cloud SDK (extorderapp-spring).| [>> extorderapp](https://github.com/SAP/cloud-s4-sample-ext/tree/extorderapp)  <br>   [>> extorderapp-spring](https://github.com/SAP/cloud-s4-sample-ext/tree/extorderapp-spring) |
| Internal Geographical Sales Report Scenario      | This application showcases a reporting extension for internal users. It shows a bar graph of sales orders (created with the extorderapp) and shows their origin on a geographical map.      |   [>> intreportapp](https://github.com/SAP/cloud-s4-sample-ext/tree/intreportapp) |
| Collaborative Address Validation Using SAP Enterprise Eventing Services      | This application  allows users to receive business events from SAP S/4HANA Cloud on the SAP Cloud Platform. The events will be received whenever a Business Partner was changed in the SAP S/4HANA Cloud System. | [>> addressvalidapp](https://github.com/SAP/cloud-s4-sample-ext/tree/addressvalidapp) |
| Display Created or Changed Sales Orders Using Business Event Handling      | This application allows users to display the business event objects in the business event queue of SAP S/4HANA Cloud together with the corresponding sales order object.      |   [>> businesseventapp](https://github.com/SAP/cloud-s4-sample-ext/tree/businesseventapp) |
| Develop a Custom UI and Deploy it to SAP S/4HANA Cloud | This application lists the actual stock movement information (from a custom CDS view) with extended material information (from a custom field). Furthermore, the scenario includes the topic of deploying the custom UI from SAP Cloud Platform to SAP S/4HANA Cloud as well as transporting extensions from a quality to a productive system.      |    [>> deploycustui](https://github.com/SAP/cloud-s4-sample-ext/tree/deploycustui) |
| Display SAP S/4HANA Cloud Products on an iOS Device      | This native iOS application lists down all the products in the S/4HANA Product Master API and displays detailed product data. | [>> iosmobileproductapp](https://github.com/SAP/cloud-s4-sample-ext/tree/iosmobileproductapp) |
| Validate Customer Addresses using a Microservice      | This scenario showcases how to consume outbound HTTP services directly in restricted ABAP coding within SAP S/4HANA Cloud. It also shows how the SAP Data Quality Management microservice from SAP Cloud Platform can be used to check and correct address data stored on SAP S/4HANA Cloud.      |   [>> microserviceext](https://github.com/SAP/cloud-s4-sample-ext/tree/microserviceext) |
| Quick Time Entry for SAP S/4HANA Cloud Time Recording | This application allows users to read and write their working time in a fast and efficient way.|    [>> timesheet-neo](https://github.com/SAP/cloud-s4-sample-ext/tree/timesheet-neo) |




Limitations / Disclaimer
------------------------
Note: Sample extension scenarios are designed to help you get an overall understanding of various extensibility concepts/patterns. SAP  recommends not to use these samples for any productive usage. They show basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html



How to get support
---------------------
Create a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND`



### Copyright and License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
