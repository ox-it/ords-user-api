# ords-user-api

The user API for the Online Research Database Services (ORDS)

![build status image](https://travis-ci.org/ox-it/ords-user-api.svg?branch=master) [![Download](https://api.bintray.com/packages/scottbw/ords/ords-user-api/images/download.svg) ](https://bintray.com/scottbw/ords/ords-user-api/_latestVersion)

## API Documentation

When deployed, documentation can be found at /api/1.0/user/swagger.json

## Configuration Properties

The following properties can be set using the ords.properties file (as defined in config.xml)

    ords.allow.signups=false

If set to TRUE, anonymous users can self-register for new accounts

    ords.localsuffix=ox.ac.uk

The localuser suffix is used to automatically map a principal to the localuser role; a localuser has permission to create new projects.

Generally you will set this to be your organisation's domain as used in email addresses.

    ords.mail.send = false

Whether to enable account signup verification emails.

    ords.mail.verification.address=http://localhost/app/verify/%s
    
The verification URL to send the user. The %s token will be replaced with the verification UUID.

    ords.mail.verification.subject=Message from ORDS
    
The subject line to use for verification emails.

    ords.mail.verification.message=Hi %s\n\nIn order to ensure you are able to receive emails from us\, please click the following link (if the link below is not clickable\, then please copy and paste the URL into a web browser). This will complete the registration process.\n\n%s\n\nThe ORDS Team

The verification message. The first %s token is replaced with user.name; the second with the verification URL.

    ords.mail.contact.subject=Message from ORDS
    
The subject line to use for contact request emails.

    ords.mail.contact.message=You have been sent a message by a user with email address of <%s> and name %s. They are interested in your project <%s> and have sent you the following message\n\n%s

The contact request message. Tokens are replaced with sender email and name, project name, and message.

### Database and security configuration

    ords.hibernate.configuration=hibernate.cfg.xml

Optional; the location of the hibernate configuration file.

    ords.shiro.configuration=file:/etc/ords/shiro.ini

Optional; the location of the Shiro INI file

     ords.server.configuration=serverConfig.xml

Optional; the location of the Server configuration file

### Mail server configuration

The following properties are used for the email server connection

    mail.smtp.auth=true
    mail.smtp.starttls.enable=true
    mail.smtp.host=localhost
    mail.smtp.port=587
    mail.smtp.from=daemons@sysdev.oucs.ox.ac.uk
    mail.smtp.username=
    mail.smtp.password=

