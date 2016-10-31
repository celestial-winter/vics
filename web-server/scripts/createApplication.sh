#!/usr/bin/env bash

# Copies the deployment artifacts to the deployment
# and initialise as a linux service (supported by Spring boot by default
# see https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-service)

function error_exit
{
	echo "$1" 1>&2
	exit 1
}

chmod +x /tmp/web-server-0.0.1.jar
mkdir -p /var/tmp
cp -f /tmp/web-server-0.0.1.jar /var/tmp/vics.jar

if [ "$DEPLOYMENT_GROUP_NAME" == "ProductionWebApp" ]
then
    aws s3 cp s3://993854-config-production/vics/production.properties /var/tmp/application.properties
    aws s3 cp s3://993854-config-production/vics/logback.xml /var/tmp/logback.xml
    cp -f /tmp/scripts/web-server-0.0.1.conf /var/tmp/vics.conf
elif [ "$DEPLOYMENT_GROUP_NAME" == "StagingWebApp" ]
then
    aws s3 cp s3://993854-config-staging/vics/staging.properties /var/tmp/application.properties
    aws s3 cp s3://993854-config-staging/vics/logback.xml /var/tmp/logback.xml
    cp -f /tmp/scripts/web-server-0.0.1.conf /var/tmp/vics.conf
elif [ "$DEPLOYMENT_GROUP_NAME" == "StagingWebPDF" ]
then
    aws s3 cp s3://993854-config-staging/vics/staging.properties /var/tmp/application.properties
    aws s3 cp s3://993854-config-staging/vics/logback.xml /var/tmp/logback.xml
    cp -f /tmp/scripts/web-server-pdf-0.0.1.conf /var/tmp/vics.conf
elif [ "$DEPLOYMENT_GROUP_NAME" == "ProductionWebPDF" ]
then
    aws s3 cp s3://993854-config-production/vics/production.properties /var/tmp/application.properties
    aws s3 cp s3://993854-config-production/vics/logback.xml /var/tmp/logback.xml
    cp -f /tmp/scripts/web-server-pdf-0.0.1.conf /var/tmp/vics.conf
elif [ "$DEPLOYMENT_GROUP_NAME" == "DevWebAppDeploymentGroup" ]
then
    aws s3 cp s3://993854-config/vics/development.properties /var/tmp/application.properties
    aws s3 cp s3://993854-config/vics/logback.xml /var/tmp/logback.xml
    cp -f /tmp/scripts/web-server-0.0.1.conf /var/tmp/vics.conf
else
    error_exit "Could not determine environment!  Aborting. DEPLOYMENT_GROUP_NAME=$DEPLOYMENT_GROUP_NAME"
fi

ln -fs /var/tmp/vics.jar /etc/init.d/vics
update-rc.d vics defaults
