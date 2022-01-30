# Brass Band Results v5 Refresh
The Brass Band Results website has been around in some form since around 2009, and it could do with a technology refresh.  This site documents the approach to this refresh and details how you can get involved to help.

## Architectural Principles
Here's a list of principles that the new code must adhere to:
* Default implementation language for the site is Java, and the Spring Boot framework.
* The site will be hosted on Azure, Microsoft's cloud.
* URLs on the site will match those already in use on the old site, in order to benefit from exiting SEO.
* It'll be deployed as a docker container, onto Azure App Service.
* The data will be stored in an Azure SQL Database.
* During testing, the database will be h2 with spacial extensions.
* Code will be open source, and stored in github.
* There will be an event system, which will utilise Azure Event Hub for message propogation.
* During development, this event hub will be mocked.
* The site will be fully multilingual.
* The approach to code development will be Test Driven Development, with a Continous Delivery Pipeline.
* All code will come from one mono repo.

## URLs
The following urls will be available during development:
* This documentation site will be deployed on docs.brassbandresults.co.uk.  This site will be hosted as an Azure Static Site.
* Static assets will be deployed on static.brassbandresults.co.uk.  This site will be hosted as an Azure Static Site.

## Major Changes over the exiting site
Over the years the following requests have been made:
* It should be possible to follow a band or conductor, and receive notifications when changes are made.
* There should be an extract to google calendar for all dates, with links through to the live site
* The map should be implemented using Open Street Map.

## Stage 1 Milestones
The following are the initial milestones in the development of the bbr5 site.  Once these are done we can get more people involved in development:
* Milestone 1: Static and Docs sites continously deployed from github action
* Milestone 2: Repeatable process to get AWS Postgresql data copied to Azure SQL Server instance, scheduled daily.
* Milestone 3: Initial Spring Boot structure set up
* Milestone 4: Models for all database tables
* Milestone 5: Contiuously deployed bbr5 site