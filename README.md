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
