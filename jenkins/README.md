# Automated jenkins and jenkins job:

We are going to run jenkins insdie docker. All the jenkins configurations and project dependencies will be get installled through Dockerfile. I'll be explaining each step to get infrasturucture up and running.

# Procedure:

## Step1:

 * We are going to create an ec2 instance with userdata, so that we get docker pre installed when we get insance up and running.

        #!/bin/bash
        set -x
        yum update -y
        sudo yum install -y docker
        sudo service docker start
        sudo usermod -a -G docker ${USER}

## Step 2:
 
 * in this step we will be buliding a docker image that will contains all dependencies. Let me explain that what are we doing in this Dockerfile. We are using base image of jenkins/jenkins:latest.

    1) First of all we dont want to perform intial setup of jenkins, we are going to make our jenkins up and running without adding any information manually. so here is how can we do.

        * In first setup we will make wizard setup to false 
            
                ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

        * in the next step we have a file plugins.txt in which we have defined all the plugins that we are needed in our project.

                COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
                RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt 

        * Now we are going to set number of executor in jenkins. we are setting 2 intially but it can be change in executors.groovy file.

                COPY executors.groovy /usr/share/jenkins/ref/init.groovy.d/

                executor.groovy:
                import jenkins.model.*
                Jenkins.instance.setNumExecutors(2)

        * Here we are going to set github sso for the organization users.Code can be checked in the default-user.groovy file. We are doing one more thing here, we are using plugin authroize-project plugin for script approval of jobs and its code can also be found from default-user.groovy.

                COPY default-user.groovy /usr/share/jenkins/ref/init.groovy.d/


        * In the next step we are installing dependencies, those are docker client, python, docker-compose, aws cli amd ecs-deploy. We have our jenkins up and running without any manually intervention till this step but we have to create jobs manually. In the next step we are going to automate jobs creation as well.

        * To Automate jobs in jenkins we are going to create a seed job. This seed job will have a scm from where it pulls the jobs. We just have to add jobs into the repo' jobs folder and when we run seed job weyou will be getting jobs in jenkins. To look over this check file 
        create-seed-job.groovy.

                COPY create-seed-job.groovy /usr/share/jenkins/ref/init.groovy.d/create-seed-job.groovy

        * In the next step we have ab entrypoint.sh script that is going to set our python3 default version 3.7 and then will execute jenkins.

                set -x 
                echo "alias python3=python3.7" > /var/jenkins_home/.bashrc && source /var/jenkins_home/.bashrc
                /usr/local/bin/jenkins.sh
 

## Step 3:

 * We have to enject env variables for github SSO. We can get values of these variables from github organization that is in github developer setting.

        export CLIENTID=value
        export CLIENTSECRET=value

    
 * In the next step, we are goint to build the image.

        docker build -t jenkins .

    
 * In the last step we are going to run the image.

        docker run --name jenkins -d -v /var/run/docker.sock:/var/run/docker.sock -p 80:8080 -e CLIENTID=${CLIENTID} -e CLIENTSECRET=${CLIENTSECRET} jenkins
