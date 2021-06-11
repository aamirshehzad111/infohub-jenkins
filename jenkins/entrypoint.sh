set -x 
echo "alias python3=python3.6" > /var/jenkins_home/.bashrc && source /var/jenkins_home/.bashrc
/usr/local/bin/jenkins.sh
