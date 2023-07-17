# bbr5
Brass Band Results 5th Version

This is the source repository for the next incarnation of https://brassbandresults.co.uk, and is a complete rewrite in Java, targetting Azure.

## Platform
* Java Spring Boot application in Docker container deployed on Azure App Service
* Azure SQL Server database
* Azure Storage Account for static assets

## Environment Variables
The following environment variables are used by the site.
* BBR_DATABASE_URL - url for the sqlserver database, something like __`jdbc:sqlserver://timpi;database=bbr;trustServerCertificate=true`__
* BBR_DATABASE_USERNAME - database username
* BBR_DATABASE_PASSWORD - database password
* BBR_SMTP_SERVER_HOST - hostname for the SMTP server to use, probably __`smtp.sendgrid.net`__
* BBR_SMTP_SERVER_USERNAME - username for SMTP server
* BBR_SMTP_SERVER_PASSWORD - password for SMTP server
* BBR_STATIC_FILES_HOST - hostname for static files, probably __`static.brassbandresults.co.uk`__
* BBR_WEB_SITE_PREFIX - website domain prefix, this provides the ability to have per-environment urls in emails
* BBR_STRIPE_PUBLIC_BUY_BUTTON - Stripe buy button, starts with buy_btn_
* BBR_STRIPE_PUBLIC_PUBLISHABLE_KEY - Stripe publishable key, starts with pk_
* BBR_STRIPE_PRIVATE_API_KEY - Stripe private key, starts with sk_

## Running Locally
Clone the git repository to your local machine.  From here the unit test suite can be run.
In order to run the application locally, you'll need a running sql server database.  The easiest way to do this is to deploy the `mcr.microsoft.com/azure-sql-edge:latest` image using docker.
Specify the `MSSQL_SA_PASSWORD` environment variable to be a password, and then put this password in `site/src/main/resources/application.properties`
You'll also need to change `timpi` in this file to point at the hostname that you are running your database server on.
Startup the server by running the `site/run.sh` script, designed for a mac, or `run.bat` designed for Windows.
It is also possible to run the app locally with docker compose:-
```bash
cd site
docker-compose up
```

### Exporting Data
Run the following extract commands on the old bbr4 server, from the ~/bbr4/web/site directory
* ./extract people
* ./extract pieces
* ./extract venues
* ./extract bands
* ./extract tags
* ./extract groups
* ./extract contests

Next we need to extract the users.
Get a psql prompt to the old bbr4 database, then run

__`\copy
(SELECT u.id, u.username, u.email, u.password, u.last_login, u.date_joined, p.id, points, contest_history_visibility, enhanced_functionality, superuser, pro_member, stripe_email, stripe_token, stripe_customer
FROM auth_user u
INNER JOIN users_userprofile p ON p.user_id = u.id
WHERE u.is_active = true) to '~/bbr-users.csv' WITH (FORMAT CSV, HEADER);`__

Copy this file into /tmp/bbr-users.csv on your local machine.

This gets the base data.  We now need to get the results, this is split into five sections:

* ./extract results 1
* ./extract results 2
* ./extract results 3
* ./extract results 4
* ./extract results 5

### Importing Data
The import data process is split into three stages, each with different mechanisms.

#### Stage 1 - Base Data
To get data into the this local database, you'll need to run some migration scripts, in this order.  The order is important because Pieces have composers and arrangers who are People etc.  There is output in the console window showing progress.  
* visit http://localhost:8080/migrate/People
* visit http://localhost:8080/migrate/Pieces
* visit http://localhost:8080/migrate/Venues
* visit http://localhost:8080/migrate/Bands
* visit http://localhost:8080/migrate/Tags
* visit http://localhost:8080/migrate/Groups
* visit http://localhost:8080/migrate/Contests

#### Stage 2 - Users
At this point we have the base data in place, with a subset of the users - just those ones required to create the things we've already created.  

The results import relies on generated SQL rather than XML, so we need to at this point import the user details.

    \copy (SELECT u.id, u.username, u.email, u.password, u.last_login, u.date_joined, p.id, points, contest_history_visibility, enhanced_functionality, superuser, pro_member, stripe_email,  stripe_token, stripe_customer
    FROM auth_user u
    INNER JOIN users_userprofile p ON p.user_id = u.id
    WHERE u.is_active = true) to ‘~/bbr-users.csv’ WITH (FORMAT CSV, HEADER);

Place the extracted users csv file into /tmp/bbr-users.csv

* visit http://localhost:8080/migrate/Users

#### Stage 3 - Results
Now we can import the results.  This takes a while, there's a lot of data.
* visit http://localhost:8080/migrate/Results