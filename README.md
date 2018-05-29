# Quick Time Entry for SAP S/4HANA Cloud Time Recording

Java Backend Application of TimeSheet.


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


## Packaging the Application

To package this project to a deployable .war archive run this command for Windows

```sh
package.bat
```

or this one for Unix based operating systems.

```sh
./package.sh
```

The .war archive is located in /target folder.
