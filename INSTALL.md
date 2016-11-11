# Install VICS on Debian or Ubuntu


## Install security and bugfix updates:

```sudo apt-get update; sudo apt-get dist-upgrade```


## Install dependencies:

```sudo apt-get install build-essential tcl8.5 redis-server postgresql postgresql-contrib git-core maven ntpdate curl```

On debian stable (jessie), maven 3.1.0 is required for vics web-client:

```apt-get -t jessie-backports install maven```


## Optionally, set correct time on server (needed for git clone over https):

```ntpdate -u time.nist.gov```


## Download VICS from GitHub:

```
cd /usr/local/src/
sudo git clone https://github.com/celestial-winter/vics.git
cd vics
```


## Create canvassing database:

```
sudo -u postgres createdb canvassapp
sudo -u postgres createuser root
sudo -u postgres createuser ubuntu
sudo -u postgres psql
ALTER USER postgres PASSWORD 'postgres';
\q
```


## Run database creation script:

```
psql -f web-server/src/test/resources/sql/drop-create.sql canvassapp
```


## Install Oracle Java 8:

Download latest version from https://jdk8.java.net/download.html and unpack in /opt

In /etc/environment set JAVA_HOME to point to the jre, for example:

```
JAVA_HOME="/opt/jdk1.8.0_122/jre/"
```

then load with: 

```
. /etc/environment
```


## Check Java version is 1.8.x:

```
java -version
echo $JAVA_HOME
```


## Install Node.js, Bower and Karma and check versions

```
curl -sL https://deb.nodesource.com/setup_4.x | sudo bash -
apt-get install nodejs
node -v

npm install -g bower
bower -v
echo '{ "allow_root": true }' > /root/.bowerrc

cd web-client
npm install --save-dev karma

``` 


## Build VICS:

```
mvn clean install
```
