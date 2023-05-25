# oar-auth-api

This is Authentication service provider, a service based on SAML protocol to connect to identity provider. Once the users tries to connect the system, it will get redirected to the central authentication provider and authenticated users gets redirected to desired application. The service provider related settings are configured in the configuration service.

# Technology base:

JAVA Spring Framework  https://spring.io/
Spring Boot https://spring.io/projects/spring-boot/#overview
SAML 2.0 security extension plugin https://spring.io/projects/spring-security-saml/
To configure the project and use appropriate security two main items are used.

[Federation metadata in this case, is provided by NIST.]
security key which is generated specifically for this service.
There are several security configuration to build the communication between the SAML service provider and SAML identity provider (NIST)


# Running the service:

### To run service on production: 
One has to make sure the it is registered with the identity providers and all the configurations and security keys are in place.

### To Run service locally :
To run the service with sample users locally without connecting to actual identity provider but mock the service behaviour set following ointhe application settings (in this case bootstrap.yml)
  samlauth.enabled: false
