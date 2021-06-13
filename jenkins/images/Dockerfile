FROM jenkins/jenkins:2.257

ENV   CLIENTID CLIENTID
ENV   CLIENTSECRET CLIENTSECRET
ENV   PYTHON_VERSION="3.6.10"
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc

USER root
RUN apt-get update -qq \
    && apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common 
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"
RUN apt-get update  -qq \
    && apt-get install docker-ce -y
RUN usermod -aG docker jenkins

COPY jenkins/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt 

COPY jenkins/groovy/*.groovy /usr/share/jenkins/ref/init.groovy.d/

USER root

RUN apt-get update
RUN apt-get install -y build-essential checkinstall software-properties-common llvm cmake wget git nano nasm yasm zip unzip pkg-config \
    libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev mysql-client default-libmysqlclient-dev


RUN wget https://www.python.org/ftp/python/${PYTHON_VERSION}/Python-${PYTHON_VERSION}.tar.xz \
    && tar xvf Python-${PYTHON_VERSION}.tar.xz \
    && rm Python-${PYTHON_VERSION}.tar.xz \
    && cd Python-${PYTHON_VERSION} \
    && ./configure \
    && make altinstall \
    && cd / \
    && rm -rf Python-${PYTHON_VERSION}
RUN apt install python3-pip -y
RUN apt-get update && apt-get install make
RUN apt-get install jq -y
RUN curl -L https://github.com/docker/compose/releases/download/1.21.0/docker-compose-`uname -s`-`uname -m` \
    | tee /usr/local/bin/docker-compose > /dev/null 
RUN chmod +x /usr/local/bin/docker-compose 
RUN ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose 
RUN pip3 install awscli==1.16.312
RUN pip3 install ecs-deploy
RUN wget "https://raw.githubusercontent.com/silinternational/ecs-deploy/develop/ecs-deploy" -O /usr/local/bin/ecs-deploy 
RUN chmod +x /usr/local/bin/ecs-deploy 

COPY jenkins/config /usr/share/jenkins/ref/config/
COPY jenkins/casc /var/jenkins_home/casc
WORKDIR /var/jenkins_home
COPY jenkins/entrypoint.sh entrypoint.sh
USER jenkins 
CMD ["/bin/bash", "entrypoint.sh"]