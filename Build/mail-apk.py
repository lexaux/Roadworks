#!/usr/bin/env python2
import smtplib, os, sys
from email.MIMEMultipart import MIMEMultipart
from email.MIMEBase import MIMEBase
from email.MIMEText import MIMEText
from email.Utils import COMMASPACE, formatdate
from email import Encoders

smtpServer = 'smtp.gmail.com'
smtpPort = 465
smtpUsername = 'poseidontestuser1'
smtpPassword = 'poseidontestuser1'


pathToCurrentScript = os.path.dirname(os.path.abspath(__file__))
pathToAttachment = pathToCurrentScript + '/../SensorLogger/target/roadworks-sensorlogger-android.apk'
emailRecipients = ['lexaux@gmail.com', 'krasnovegorinc@gmail.com']

if sys.argv:
    emailRecipients = sys.argv[1:]


def send_mail(send_from, send_to, subject, text, files=[]):
    assert type(send_to)==list
    assert type(files)==list

    msg = MIMEMultipart()
    msg['From'] = send_from
    msg['To'] = COMMASPACE.join(send_to)
    msg['Date'] = formatdate(localtime=True)
    msg['Subject'] = subject

    msg.attach( MIMEText(text) )

    for f in files:
        part = MIMEBase('application', "octet-stream")
        part.set_payload( open(f,"rb").read() )
        Encoders.encode_base64(part)
        part.add_header('Content-Disposition', 'attachment; filename="%s"' % os.path.basename(f))
        msg.attach(part)

    smtp = smtplib.SMTP_SSL(smtpServer, smtpPort)
    smtp.login(smtpUsername, smtpPassword)
    smtp.sendmail(send_from, send_to, msg.as_string())
    smtp.close()

if os.path.exists(pathToAttachment):
    send_mail('poseidontestuser@gmail.com', emailRecipients , 'Roadworks SensorLogger: new version attached.', 'Please install the attached file', [pathToAttachment])
else:
    print 'No apk found. Looks like build did not complete well?'
    os._exit(2)