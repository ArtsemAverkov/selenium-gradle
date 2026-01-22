FROM eclipse-temurin:11-jdk

RUN apt-get update && \
    apt-get install -y docker.io curl unzip git && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

CMD ["tail", "-f", "/dev/null"]