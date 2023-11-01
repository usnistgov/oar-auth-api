FROM maven:3-eclipse-temurin-8-focal

RUN apt-get update && \
    apt-get install -y zip git less netcat-openbsd gnupg python3 wget

RUN update-alternatives --install /usr/bin/python python /usr/bin/python3 1

COPY cacerts/README.md cacerts/*.crt /usr/local/share/ca-certificates/
RUN update-ca-certificates
RUN java_certs=$JAVA_HOME/jre/lib/security/cacerts; \
    add_certs=`ls /usr/local/share/ca-certificates/*.crt` && \
    for crt in $add_certs; do \
        name=`basename -s .crt $crt`; \
        echo -n ${name}: " "; \
        keytool -import -keystore $java_certs -trustcacerts -file $crt \
                -storepass changeit -alias $name -noprompt; \
    done;

RUN sed --in-place -e '/CREATE_MAIL_SPOOL/ s/=yes/=no/' /etc/default/useradd
ARG devuser=developer
ARG devuid=1000
RUN grep -qs :${devuid}: /etc/group || \
    groupadd --gid $devuid $devuser
RUN grep -qs ":${devuid}:[[:digit:]]+:" /etc/passwd || \
    useradd -m --comment "OAR Developer" --shell /bin/bash \
            --gid $devuid --uid $devuid $devuser

RUN mkdir /home/$devuser/.m2

VOLUME /app/dev
VOLUME /app/dist
COPY settings.xml /app/mvn-user-settings.xml
COPY settings.xml /home/$devuser/.m2/settings.xml
RUN chown $devuser:$devuser /home/$devuser/.m2/settings.xml && \
    chmod a+r /home/$devuser/.m2/settings.xml
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod a+rx /app/entrypoint.sh

WORKDIR /app/dev
USER $devuser
ENTRYPOINT [ "/app/entrypoint.sh" ]


