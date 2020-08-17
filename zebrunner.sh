#!/bin/bash

  setup() {
    # PREREQUISITES: valid values inside ZBR_PROTOCOL, ZBR_HOSTNAME and ZBR_PORT env vars!
    local url="$ZBR_PROTOCOL://$ZBR_HOSTNAME:$ZBR_PORT"

    cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/_common/hosts.env

    cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/reporting-service/variables.env

    cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    sed -i "s#http://localhost:8081#${url}#g" configuration/reporting-ui/variables.env

    cp configuration/_common/secrets.env.original configuration/_common/secrets.env
    sed -i "s#TOKEN_SIGNING_SECRET=AUwMLdWFBtUHVgvjFfMmAEadXqZ6HA4dKCiCmjgCXxaZ4ZO8od#TOKEN_SIGNING_SECRET=${ZBR_TOKEN_SIGNING_SECRET}#g" configuration/_common/secrets.env
    sed -i "s#CRYPTO_SALT=TDkxalR4T3EySGI0T0YyMitScmkxWDlsUXlPV2R4OEZ1b2kyL1VJeFVHST0=#CRYPTO_SALT=${ZBR_CRYPTO_SALT}#g" configuration/_common/secrets.env

    cp configuration/iam-service/variables.env.original configuration/iam-service/variables.env
    sed -i "s#DATABASE_USERNAME=postgres#DATABASE_USERNAME=${ZBR_IAM_POSTGRES_USER}#g" configuration/iam-service/variables.env
    sed -i "s#DATABASE_PASSWORD=postgres#DATABASE_PASSWORD=${ZBR_IAM_POSTGRES_PASSWORD}#g" configuration/iam-service/variables.env

    cp configuration/iam-db/variables.env.original configuration/iam-db/variables.env
    sed -i "s#POSTGRES_USER=postgres#POSTGRES_USER=${ZBR_IAM_POSTGRES_USER}#g" configuration/iam-db/variables.env
    sed -i "s#POSTGRES_PASSWORD=postgres#POSTGRES_PASSWORD=${ZBR_IAM_POSTGRES_PASSWORD}#g" configuration/iam-db/variables.env

    cp configuration/postgres/variables.env.original configuration/postgres/variables.env
    sed -i "s#POSTGRES_USER=postgres#POSTGRES_USER=${ZBR_POSTGRES_USER}#g" configuration/postgres/variables.env
    sed -i "s#POSTGRES_PASSWORD=postgres#POSTGRES_PASSWORD=${ZBR_POSTGRES_PASSWORD}#g" configuration/postgres/variables.env
    sed -i "s#DATABASE_USERNAME=postgres#DATABASE_USERNAME=${ZBR_POSTGRES_USER}#g" configuration/reporting-service/variables.env
    sed -i "s#DATABASE_PASSWORD=postgres#DATABASE_PASSWORD=${ZBR_POSTGRES_PASSWORD}#g" configuration/reporting-service/variables.env

    cp configuration/mail-service/variables.env.original configuration/mail-service/variables.env
    sed -i "s#MAILING_HOST=smtp.gmail.com#MAILING_HOST=${ZBR_SMTP_HOST}#g" configuration/mail-service/variables.env
    sed -i "s#MAILING_PORT=587#MAILING_PORT=${ZBR_SMTP_PORT}#g" configuration/mail-service/variables.env
    sed -i "s#MAILING_SENDER_EMAIL=changeit#MAILING_SENDER_EMAIL=${ZBR_SMTP_EMAIL}#g" configuration/mail-service/variables.env
    sed -i "s#MAILING_USERNAME=changeit#MAILING_USERNAME=${ZBR_SMTP_USER}#g" configuration/mail-service/variables.env
    sed -i "s#MAILING_PASSWORD=changeit#MAILING_PASSWORD=${ZBR_SMTP_PASSWORD}#g" configuration/mail-service/variables.env

    cp configuration/rabbitmq/variables.env.original configuration/rabbitmq/variables.env
    sed -i "s#RABBITMQ_DEFAULT_USER=qpsdemo#RABBITMQ_DEFAULT_USER=${ZBR_RABBITMQ_USER}#g" configuration/rabbitmq/variables.env
    sed -i "s#RABBITMQ_DEFAULT_PASS=qpsdemo#RABBITMQ_DEFAULT_PASS=${ZBR_RABBITMQ_PASSWORD}#g" configuration/rabbitmq/variables.env
    cp configuration/logstash/logstash.conf.original configuration/logstash/logstash.conf
    sed -i "s#user => ""qpsdemo""#user => ""${ZBR_RABBITMQ_USER}""#g" configuration/logstash/logstash.conf
    sed -i "s#password => ""qpsdemo""#password => ""${ZBR_RABBITMQ_PASSWORD}""#g" configuration/logstash/logstash.conf

#    export ZBR_RABBITMQ_USER=$ZBR_RABBITMQ_USER
#    export ZBR_RABBITMQ_PASSWORD=$ZBR_RABBITMQ_PASSWORD


    cp configuration/redis/redis.conf.original configuration/redis/redis.conf
    sed -i "s#requirepass MdXVvJgDdz9Hnau7#requirepass ${ZBR_REDIS_PASSWORD}#g" configuration/redis/redis.conf
    sed -i "s#REDIS_PASSWORD=MdXVvJgDdz9Hnau7#REDIS_PASSWORD=${ZBR_REDIS_PASSWORD}#g" configuration/reporting-service/variables.env

    #TODO: parametrize postgres credentials later
    #configuration/rabbitmq/variables

    echo "reporting setup finished"
  }

  shutdown() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    docker-compose --env-file .env -f docker-compose.yml down -v

    rm configuration/_common/hosts.env
    rm configuration/_common/secrets.env
    rm configuration/iam-service/variables.env
    rm configuration/iam-db/variables.env
    rm configuration/postgres/variables.env
    rm configuration/mail-service/variables.env
    rm configuration/rabbitmq/variables.env
    rm configuration/logstash/logstash.conf
    rm configuration/redis/redis.conf
    rm configuration/reporting-service/variables.env
    rm configuration/reporting-ui/variables.env
    rm configuration/rabbitmq/variables.env

    minio-storage/zebrunner.sh shutdown
  }

  start() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    # create infra network only if not exist
    docker network inspect infra >/dev/null 2>&1 || docker network create infra

    if [[ ! -f configuration/_common/hosts.env ]]; then
      cp configuration/_common/hosts.env.original configuration/_common/hosts.env
    fi

    if [[ ! -f configuration/_common/secrets.env ]]; then
      cp configuration/_common/secrets.env.original configuration/_common/secrets.env
    fi

    if [[ ! -f configuration/iam-service/variables.env ]]; then
      cp configuration/iam-service/variables.env.original configuration/iam-service/variables.env
    fi

    if [[ ! -f configuration/iam-db/variables.env ]]; then
      cp configuration/iam-db/variables.env.original configuration/iam-db/variables.env
    fi

    if [[ ! -f configuration/postgres/variables.env ]]; then
      cp configuration/postgres/variables.env.original configuration/postgres/variables.env
    fi

    if [[ ! -f configuration/mail-service/variables.env ]]; then
      cp configuration/mail-service/variables.env.original configuration/mail-service/variables.env
    fi

    if [[ ! -f configuration/rabbitmq/variables.env ]]; then
      cp configuration/rabbitmq/variables.env.original configuration/rabbitmq/variables.env
    fi

    if [[ ! -f configuration/logstash/logstash.conf ]]; then
      cp configuration/logstash/logstash.conf.original configuration/logstash/logstash.conf
    fi

    if [[ ! -f configuration/redis/redis.conf ]]; then
      cp configuration/redis/redis.conf.original configuration/redis/redis.conf
    fi

    if [[ ! -f configuration/reporting-service/variables.env ]]; then
      cp configuration/reporting-service/variables.env.original configuration/reporting-service/variables.env
    fi

    if [[ ! -f configuration/reporting-ui/variables.env ]]; then
      cp configuration/reporting-ui/variables.env.original configuration/reporting-ui/variables.env
    fi

    minio-storage/zebrunner.sh start
    docker-compose --env-file .env -f docker-compose.yml up -d
  }

  stop() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh stop
    docker-compose --env-file .env -f docker-compose.yml stop
  }

  down() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh down
    docker-compose --env-file .env -f docker-compose.yml down
  }

  backup() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    minio-storage/zebrunner.sh backup

    cp configuration/_common/hosts.env configuration/_common/hosts.env.bak
    cp configuration/_common/secrets.env configuration/_common/secrets.env.bak
    cp configuration/iam-service/variables.env configuration/iam-service/variables.env.bak
    cp configuration/iam-db/variables.env configuration/iam-db/variables.env.bak
    cp configuration/postgres/variables.env configuration/postgres/variables.env.bak
    cp configuration/mail-service/variables.env configuration/mail-service/variables.env.bak
    cp configuration/rabbitmq/variables.env configuration/rabbitmq/variables.env.bak
    cp configuration/logstash/logstash.conf configuration/logstash/logstash.conf.bak
    cp configuration/redis/redis.conf configuration/redis/redis.conf.bak
    cp configuration/reporting-service/variables.env configuration/reporting-service/variables.env.bak
    cp configuration/reporting-ui/variables.env configuration/reporting-ui/variables.env.bak

    docker run --rm --volumes-from postgres -v $(pwd)/backup:/var/backup "ubuntu" tar -czvf /var/backup/postgres.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from iam-db -v $(pwd)/backup:/var/backup "ubuntu" tar -czvf /var/backup/iam-db.tar.gz /var/lib/postgresql/data
    docker run --rm --volumes-from elasticsearch -v $(pwd)/backup:/var/backup "ubuntu" tar -czvf /var/backup/elasticsearch.tar.gz /usr/share/elasticsearch/data
    docker run --rm --volumes-from reporting-service -v $(pwd)/backup:/var/backup "ubuntu" tar -czvf /var/backup/reporting-service.tar.gz /opt/assets
    docker run --rm --volumes-from db-migration-tool -v $(pwd)/backup:/var/backup "ubuntu" tar -czvf /var/backup/db-migration-tool.tar.gz /var/migration-state
  }

  restore() {
    if [[ -f .disabled ]]; then
      exit 0
    fi

    stop
    minio-storage/zebrunner.sh restore

    cp configuration/_common/hosts.env.bak configuration/_common/hosts.env
    cp configuration/_common/secrets.env.bak configuration/_common/secrets.env
    cp configuration/iam-service/variables.env.bak configuration/iam-service/variables.env
    cp configuration/iam-db/variables.env.bak configuration/iam-db/variables.env
    cp configuration/postgres/variables.env.bak configuration/postgres/variables.env
    cp configuration/mail-service/variables.env.bak configuration/mail-service/variables.env
    cp configuration/rabbitmq/variables.env.bak configuration/rabbitmq/variables.env
    cp configuration/logstash/logstash.conf.bak configuration/logstash/logstash.conf
    cp configuration/redis/redis.conf.bak configuration/redis/redis.conf
    cp configuration/reporting-service/variables.env.bak configuration/reporting-service/variables.env
    cp configuration/reporting-ui/variables.env.bak configuration/reporting-ui/variables.env

    docker run --rm --volumes-from postgres -v $(pwd)/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/postgres.tar.gz"
    docker run --rm --volumes-from iam-db -v $(pwd)/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/iam-db.tar.gz"
    docker run --rm --volumes-from elasticsearch -v $(pwd)/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/elasticsearch.tar.gz"
    docker run --rm --volumes-from reporting-service -v $(pwd)/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/reporting-service.tar.gz"
    docker run --rm --volumes-from db-migration-tool -v $(pwd)/backup:/var/backup "ubuntu" bash -c "cd / && tar -xzvf /var/backup/db-migration-tool.tar.gz"
    down
  }

  echo_warning() {
    echo "
      WARNING! $1"
  }

  echo_telegram() {
    echo "
      For more help join telegram channel: https://t.me/zebrunner
      "
  }

  echo_help() {
    echo "
      Usage: ./zebrunner.sh [option]
      Flags:
          --help | -h    Print help
      Arguments:
      	  start          Start container
      	  stop           Stop and keep container
      	  restart        Restart container
      	  down           Stop and remove container
      	  shutdown       Stop and remove container, clear volumes
      	  backup         Backup container
      	  restore        Restore container"
      echo_telegram
      exit 0
  }

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

case "$1" in
    setup)
        if [[ ! -z $ZBR_PROTOCOL || ! -z $ZBR_HOSTNAME || ! -z $ZBR_PORT ]]; then
          setup
        else
          echo_warning "Setup procedure is supported only as part of Zebrunner Server (Community Edition)!"
          echo_telegram
        fi

#        echo WARNING! Increase vm.max_map_count=262144 appending it to /etc/sysctl.conf on Linux Ubuntu
#        echo your current value is `sysctl vm.max_map_count`

        ;;
    start)
	start
        ;;
    stop)
        stop
        ;;
    restart)
        down
        start
        ;;
    down)
        down
        ;;
    shutdown)
        shutdown
        ;;
    backup)
        backup
        ;;
    restore)
        restore
        ;;
    --help | -h)
        echo_help
        ;;
    *)
        echo "Invalid option detected: $1"
        echo_help
        exit 1
        ;;
esac

