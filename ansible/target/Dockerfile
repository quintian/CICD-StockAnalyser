# Local Ansible target for the StockAnalyser deployment stage.
# This container is not the app. It is a small SSH-accessible target that lets
# Ansible run Docker commands against the local Docker engine.
FROM debian:bookworm-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        openssh-server \
        procps \
        python3 \
        sudo \
        tar \
    && rm -rf /var/lib/apt/lists/* \
    && arch="$(dpkg --print-architecture)" \
    && case "$arch" in \
        arm64) docker_arch="aarch64" ;; \
        amd64) docker_arch="x86_64" ;; \
        *) echo "Unsupported Docker CLI architecture: $arch" && exit 1 ;; \
    esac \
    && curl -fsSL "https://download.docker.com/linux/static/stable/${docker_arch}/docker-28.5.2.tgz" \
        | tar -xz -C /usr/local/bin --strip-components=1 docker/docker \
    && useradd -m -s /bin/bash appuser \
    && echo "appuser:appuser" | chpasswd \
    && echo "appuser ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/appuser \
    && chmod 0440 /etc/sudoers.d/appuser \
    && mkdir -p /run/sshd \
    && sed -i 's/^#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config \
    && sed -i 's/^PasswordAuthentication no/PasswordAuthentication yes/' /etc/ssh/sshd_config

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D", "-e"]
