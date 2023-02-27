# bbr5
Brass Band Results 5th Version

This is the source repository for the next incarnation of https://brassbandresults.co.uk, and is a complete rewrite in Java, targetting Azure.

## Platform
* Java Spring Boot application in Docker container deployed on Azure App Service
* Azure SQL Server database
* Azure Storage Account for static assets

## Running Locally
Close the git repository to your local machine.  From here the unit test suite can be run.
In order to run the application locally, you'll need a running sql server database.  The easiest way to do this is to deploy the `mcr.microsoft.com/azure-sql-edge:latest` image using docker.
Specify the `MSSQL_SA_PASSWORD` environment variable to be a password, and then put this password in `site/src/main/resources/application.properties`
You'll also need to change `timpi` in this file to point at the hostname that you are running your database server on.
Startup the server by running the `site/run.sh` script, designed for a mac.  If you'r using Windows, you'll need to write your own. :-)

### Migrating Data
To get data into the this local database, you'll need to run some migration scripts, most likely in this order.  There is output in the console window showing progress.  
* visit http://localhost:8080/migrate/People
* visit http://localhost:8080/migrate/Pieces

* visit http://localhost:8080/migrate/Venues

* visit http://localhost:8080/migrate/Bands

* visit http://localhost:8080/migrate/Tags
* visit http://localhost:8080/migrate/Groups
* visit http://localhost:8080/migrate/Contests

* visit http://localhost:8080/migrate/Results