# bsctest
BSC test

Instructions:
- Java version 8 is needed to run this test application.
- Project uses maven to build and optionaly run the application.
- You can build the application by typing the following command into console:
  mvn package
- You can run the application either by maven or by typical java command line statement:
  - `mvn exec:java -D"exec.mainClass"="com.bsc.App"`
  - `java -cp target/classes com.bsc.App`
- It is possible to load multiple payments (batch mode) on startup via -f <filename> command line parameter:
  - `java -cp target/classes com.bsc.App -f c:\tmp\payments.txt`

Assumptions:
- If the user enters invalid input, error message is displayed, but program keeps running.
- When handling batch payments (via -f parameter), all misleading lines are ignored.
- Exchange rates are defined in properties file within application resources for simplicity (src\main\resources\usdConversionRates.properties)
