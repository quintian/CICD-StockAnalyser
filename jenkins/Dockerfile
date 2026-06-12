# Custom Jenkins image for the StockAnalyser CI pipeline.
FROM jenkins/jenkins:lts-jdk17

USER root

# Jenkins needs:
# - git to clone the app repository
# - ansible/sshpass/openssh-client to deploy to the target container
# - python3/pip/venv to install dependencies and run pytest
# - curl to smoke-test the running app container
# - Docker CLI to build and run images through the mounted docker.sock
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ansible \
        ca-certificates \
        curl \
        git \
        openssh-client \
        python3 \
        python3-pip \
        python3-venv \
        sshpass \
        tar \
    && rm -rf /var/lib/apt/lists/*

RUN arch="$(dpkg --print-architecture)" \
    && case "$arch" in \
        arm64) docker_arch="aarch64" ;; \
        amd64) docker_arch="x86_64" ;; \
        *) echo "Unsupported Docker CLI architecture: $arch" && exit 1 ;; \
    esac \
    && curl -fsSL "https://download.docker.com/linux/static/stable/${docker_arch}/docker-28.5.2.tgz" \
        | tar -xz -C /usr/local/bin --strip-components=1 docker/docker \
    && docker --version

USER jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
