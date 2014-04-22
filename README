# Roadworks SensorLogger Android application

This is a mobile client application, doing 2 maing things:
 * When the user is driving, and has this app enabled, it collects accelerometer,
   GPS and other sensor readings, processes them and sends to server.
 * It allows looking at the map to determine surface quality, individual potholes etc.

 Please check settings.

 ## DB Connection - system properties
 By default, the application would try to reach `jdbc://postgresql/localhost/slicktest` database, with user
 `postgres` and `password` postgres. If that's not the one you want (the common case for remote and non-playground
 environments, please define some system props to alter this behavior:
  * `ROADWORKS_JDBC_CONNECTION_STRING` - jdbc connection string to a database.
  * `ROADWORKS_JDBC_USERNAME` and `ROADWORKS_JDBC_PASSWORD`. Should be quite straightforward.

In order to define these system properties while working in *IDE*, easiest would be to add command line argument
to a java running tomcat in form of `-DROADWORKS_JDBC_PASSWORD="aaa" -DROADWORKS_JDBC...`

In order to make a standalone tomcat configuration, one needs to edit `conf/catalina.properties` file, adding own
configuration to the end in a form of `=` separated pairs, just like ordinary property files.
