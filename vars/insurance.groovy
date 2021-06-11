def call(Map pipelineParams) {
pipeline {
    agent {
        label "${pipelineParams.NODE_LABEL}"
    }
    options {
        timeout(time: 90, unit: 'MINUTES')
    }
    environment {
        gitUser = sh(script: "git log -1 --pretty=format:'%an'", , returnStdout: true).trim()
        gitUserEmail = sh(script: "git log -1 --pretty=format:'%ce'", , returnStdout: true).trim()
        jenkinsUserToken = credentials('jenkins-user-token')
    }
    parameters {
        choice(name: 'OPTION', choices: ['test','deploy'], description: 'The build type')
    }

    stages {
        stage('To Fail First Build') {
            when {
                expression {
                    return env.BUILD_NUMBER.equals("1")
                }
            }
            steps {
                script {
                    validateParams("${env.jenkinsUserToken}")
                }
            }
        }

        stage (' To stop previous running builds') {
            steps {
                  script {
		            sh "echo 'stopping previous build'"
                    abortPreviousBuild()
                  }
            }
        }

        stage ('Test') {
            steps {
                sh "make run-unit-tests BRANCHNAME=${env.BRANCH_NAME}"                
            }
        }
        

        stage('ECS Build & Scale') {
            when {
                expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev' }
            }
            parallel {
                stage ('Build & Push') {
                    when {
                        expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev' }
                    }
                    steps {
                        sh "make docker-build-image MEM_INSURANCE=${pipelineParams.MEM_INSURANCE} ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL}"
                        sh "make publish ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL}"
                    }
                } 
                stage ('ECS Scale Out') {
                    steps {
                        sh "make ecs-scaling-out REGION=${pipelineParams.AWS_REGION} INSURANCE_ASG=${pipelineParams.INSURANCE_ASG} INSURANCE_DESIRED=${pipelineParams.INSURANCE_DESIRED} CLUSTER_INSURANCE=${pipelineParams.CLUSTER_INSURANCE}"
                    }
                }
            }
        }


    
        stage ('ECS Deploy') {
            when {
                expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev' }
            }
            steps {
                sh "make ecs-deploy ENV=${pipelineParams.ENV} ECR_REPO=${pipelineParams.ECR_REPO} AWS_ECR_ACCOUNT_URL=${pipelineParams.AWS_ECR_ACCOUNT_URL} REGION=${pipelineParams.AWS_REGION} SERVICE_INSURANCE=${pipelineParams.SERVICE_INSURANCE} CLUSTER_INSURANCE=${pipelineParams.CLUSTER_INSURANCE}"
            }
       }
        stage ('ECS Scale In') {
            when {
                expression { params.OPTION == 'deploy' || env.BRANCH_NAME == 'dev'}
            }
            steps {
                sh "make ecs-scaling-in REGION=${pipelineParams.AWS_REGION} INSURANCE_ASG=${pipelineParams.INSURANCE_ASG} INSURANCE_DESIRED=${pipelineParams.INSURANCE_DESIRED} CLUSTER_INSURANCE=${pipelineParams.CLUSTER_INSURANCE}"
            }
        }
    }
 
    post {
     always {
       script{
        sh "make docker-clean-up"
       }
       slacknotifier("${env.gitUser}")
       sendEmail("${env.gitUserEmail}")
     }
   }
    
}
}