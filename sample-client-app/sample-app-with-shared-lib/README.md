Singleton Java Client Sample Application with Shared Library
============

This application can be used to demonstrate usage of a shared library that uses the Singleton Java client.

Prerequisites
------------
 * Build the Singleton Java client library by following the instructions in [here](https://github.com/vmware/singleton/blob/g11n-java-client/README.md).

How to build and run this sample application
------------
 * Go to the root directory of this sample application
   ```
   g11n-java-client/sample/-client-app/sample-app-with-shared-lib
   ```
 * Create a lib folder.
   ```
   mkdir lib
   ```
 * Put the Singleton Java client library into the lib folder.
   ```
   cp ../../build/libs/vip4java-0.1.0.jar ./lib/
   ```
 * Put the sample shared library into the lib folder. See how to build the sample shared library in [here](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/sample-shared-library/README.md)
   ```
   cp ../sample-shared-library/build/libs/sample-shared-library-1.0.jar ./lib/
   ```
 * Build this sample application.
   ```
   gradle build
   ```
 * Run this sample application.
   ```
   java -jar ./build/libs/sample-with-shared-lib-1.0.jar
   ```
Note: The library jar will be created under "build/libs" directory.
