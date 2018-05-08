# S/4HANA Cloud extensions - Sample apps
 
The sample applications in this repository showcase various side-by-side extension scenarios to an SAP S/4HANA (Cloud) system. 
Side-by-side applications are standalone web-based applications, running on the SAP Cloud Platform. Find an overview of [S/4HANA extensions here](https://www.sap.com/documents/2015/07/2ad59b27-347c-0010-82c7-eda71af511fa.html).

Note: All samples require an S/4HANA (Cloud) system. Detailed instructions about the scenarios, underlying business process and set-up of the S/4HANA system can be found in the [1RW set-up instructions guide](https://rapid.sap.com/bp/#/browse/scopeitems/1RW) on how to prepare your S/4HANA Cloud system according to the scenario. 
Since most of the samples also show a combination of S/4HANA in-app extensions several roles are required on the S/4HANA system. 



## External Order Scenario
This application shows how to reach external users and allow them to list products and place sales orders. 

- It is solely based on open source components, not using/requiring SAP proprietary technology. 
- It is tightly coupled, and uses a technical user for integration with the S/4HANA system
- It requires no additional persistency but uses an S/4HANA Custom Business Object to store additional data.

[>> extorderapp](extorderapp)

 

## Internal Geographical Sales Report Scenario
This applications show cases a reporting extension for internal users. It shows a bar graph of sales orders (created with the extorderapp) and shows their origin on a geopraphical map.

- It is using SAP Fiori to provide the same UI technology than S/4HANA.
- It is using the SAP S/4HANA Cloud SDK for easier service consumption and integration with S/4HANA 
- It shows how to set-up a shared identity provider between S/4HANA and the sample app running  the SAP Cloud Platform to allow single sign-on. 

[>> intreportapp](intreportapp)





Limitations / Disclaimer
------------------------
Note: This sample code is primarily for illustration purposes and is not intended for productive usage. It solely shows basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html  



How to obtain support
---------------------
File a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND`




### Copyright and License

Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
