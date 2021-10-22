# A basic date planning tool
This repository contains a basic date planning tool, in which different users can create different dates with each other. This can be modelled via an Entity-Relationship-model with entities Users and Dates, which are connected via an n-to-m-relationship. Internally, this is realized via a database with three tables, User, Date and DateUser. The UserManager class manages the User table and the DateManager class manages the other two tables.

### Installation and testing
Clone this repo and download the JDBC-Driver for SQLite from
https://github.com/xerial/sqlite-jdbc
Putting their .jar-file in the same directory allows you to compile and test this tool via
```
javac Terminplanung.java UserManager.java DateManager.java DateCollisionException.java
java -classpath ".:sqlite-jdbc-3.36.0.3.jar" Terminplanung
```
Terminplanung contains the main test routines and uses SQLite as DBMS. We tested this on Ubuntu 16.04.

### Future Plans
* We want to rename date to appointment - whenever we write date, we mean appointment.
* Start and endtime of a date are currently stored as integers, ie. as UNIX-Time. We plan on using a proper date format in the future.
* When changing dates, internally we remove the date to re-add it again. This saves code lines, but requires the user to pass every date parameter. We would like to allow for changing parameters one by one, for example only changing the start time of a date while leaving end time and title of the date fixed, as is already done for changing users.
* When deleting a user, dates are removed where the deleted user was the only participant. This is not being tested, so we want to add a test for this functionality.
* Currently, the UserManager class is solely responsible for the User table and the DateManager class is solely responsible for the Date table and the DateUser table. However we provide functionality operating on all three tables simultaneously, for example returning all users and dates, thus the DateManager has to be passed to member functions of UserManager in the test routines. We would like to write a wrapper encapsuling User- and DateManager, which is responsible for providing functionality which uses both Managers and which otherwise just calls the underlying methods in User- or DateManager.
* Our methods are mostly void - they return text on the terminal, and the testing has to be done manually. We want to change this.
* Rename methods so they sound more like the corresponding SQL-commands they execute.

