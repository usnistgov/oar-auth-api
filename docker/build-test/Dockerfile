FROM maven:3-jdk-8-alpine

RUN apk update && apk upgrade && apk add --no-cache netcat-openbsd zip git less \
                                             gnupg python wget nss

RUN apk --no-cache add ca-certificates
RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub

RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-bin-2.29-r0.apk

RUN apk add glibc-2.29-r0.apk glibc-bin-2.29-r0.apk

RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-i18n-2.29-r0.apk
RUN apk add glibc-bin-2.29-r0.apk glibc-i18n-2.29-r0.apk
RUN /usr/glibc-compat/bin/localedef -i en_US -f UTF-8 en_US.UTF-8

# can safely omit next line as Alpine linux doesn't create a mail spool for users by default
# RUN sed --in-place -e '/CREATE_MAIL_SPOOL/ s/=yes/=no/' /etc/default/useradd

ARG devuser=developer
ARG devuid=1000
RUN grep -qs :${devuid}: /etc/group || \
    addgroup -g $devuid $devuser
RUN grep -qs ":${devuid}:[[:digit:]]+:" /etc/passwd || \
    adduser -D -g "OAR Developer" -s /bin/bash \
            -G $devuser -u $devuid $devuser

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
