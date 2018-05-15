# SAP S/4HANA Cloud extensions - Sample apps

The sample applications in this repository showcase various side-by-side extension scenarios to an SAP S/4HANA Cloud system. **Please check out the branches of the repository for the diverse sample apps.**
Side-by-side applications are standalone web-based applications, running on the SAP Cloud Platform. Find an overview of [SAP S/4HANA extensions here](https://www.sap.com/documents/2015/07/2ad59b27-347c-0010-82c7-eda71af511fa.html).

Note: All samples require an SAP S/4HANA Cloud system. Detailed instructions about the scenarios, underlying business processes and how to set up of the SAP S/4HANA Cloud system as well as how to implement the apps can be found in the set-up instruction guides. You'll find a link in the readme files of the individual apps in the branches.
Since most of the samples also show a combination of SAP S/4HANA in-app extensions, several roles are required on the SAP S/4HANA Cloud system.

# Scenario Overview

## External Order Scenario
This application shows how to reach external users and allow them to list products and place sales orders.

- It uses the SAP S/4HANA Cloud SDK for easier service consumption and integration with S/4HANA
- It is tightly coupled, and uses a technical user for integration with the S/4HANA system
- It requires no additional persistency but uses an S/4HANA Custom Business Object to store additional data.

[>> extorderapp](https://github.com/SAP/cloud-s4-sample-ext/tree/extorderapp)

There's also another version of this scenario available using the Spring framework (instead of SAP the S/4HANA Cloud SDK)

[>> extorderapp-spring](https://github.com/SAP/cloud-s4-sample-ext/tree/extorderapp-spring)

## Internal Geographical Sales Report Scenario
This applications show cases a reporting extension for internal users. It shows a bar graph of sales orders (created with the extorderapp) and shows their origin on a geographical map.

- It uses SAP Fiori to provide the same UI technology than S/4HANA.
- It uses the SAP S/4HANA Cloud SDK for easier service consumption and integration with S/4HANA
- It shows how to set-up a shared identity provider between S/4HANA and the sample app running  the SAP Cloud Platform to allow single sign-on.

[>> intreportapp](https://github.com/SAP/cloud-s4-sample-ext/tree/intreportapp)





Limitations / Disclaimer
------------------------
Note: Sample extension scenarios are designed to help you get an overall understanding of various extensibility concepts/patterns. SAP  recommends not to use these samples for any productive usage. They show basic interaction with an S/4HANA Cloud system. Topics like authentication, error handling, transactional correctness, security, caching, tests were omitted on purpose for the sake of simplicity. For detailed information on development on the SAP Cloud Platform, please consult https://cloudplatform.sap.com/developers.html



How to obtain support
---------------------
File a message in the [SAP Support Launchpad](https://launchpad.support.sap.com/#/incident/create) under component `CA-GTF-FND`




### Copyright and License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](LICENSE) file

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
