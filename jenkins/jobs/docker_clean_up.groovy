job('docker_clean_up') {
    triggers {
        cron('H 0 * * *')
    }
    steps {
        shell('docker system info')
        shell('docker system prune -f')
        shell('docker image prune -a -f --filter "until=24h"')
        shell('docker system info')
        shell('df -h')
    }
}