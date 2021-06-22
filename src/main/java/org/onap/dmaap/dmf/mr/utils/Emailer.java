/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
*  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *  
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *  
 *******************************************************************************/
package org.onap.dmaap.dmf.mr.utils;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Send an email from a message.
 * 
 * @author peter
 */
public class Emailer
{
	public static final String kField_To = "to";
	public static final String kField_Subject = "subject";
	public static final String kField_Message = "message";

	public Emailer()
	{
		fExec = Executors.newCachedThreadPool ();
	
	}

	public void send ( String to, String subj, String body ) throws IOException
	{
		final String[] addrs = to.split ( "," );

		if ( to.length () > 0 )
		{
			final MailTask mt = new MailTask ( addrs, subj, body );
			fExec.submit ( mt );
		}
		else
		{
			log.warn ( "At least one address is required." );
		}
	}

	public void close ()
	{
		fExec.shutdown ();
	}

	private final ExecutorService fExec;
	

	

	private static final EELFLogger log = EELFManager.getInstance().getLogger(Emailer.class);
	
	public static final String kSetting_MailAuthUser = "mailLogin";
	public static final String kSetting_MailFromEmail = "mailFromEmail";
	public static final String kSetting_MailFromName = "mailFromName";
	public static final String kSetting_SmtpServer = "mailSmtpServer";
	public static final String kSetting_SmtpServerPort = "mailSmtpServerPort";
	public static final String kSetting_SmtpServerSsl = "mailSmtpServerSsl";
	public static final String kSetting_SmtpServerUseAuth = "mailSmtpServerUseAuth";

	private class MailTask implements Runnable
	{
		public MailTask ( String[] to, String subject, String msgBody )
		{
			fToAddrs = to;
			fSubject = subject;
			fBody = msgBody;
		}

		private String getSetting ( String settingKey, String defval )
		{
			
			String strSet = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,settingKey);
			if(strSet==null)strSet=defval;
			return strSet;
		}

		// we need to get setting values from the evaluator but also the channel config
		private void makeSetting ( Properties props, String propKey, String settingKey, String defval )
		{
			props.put ( propKey, getSetting ( settingKey, defval ) );
		}

		private void makeSetting ( Properties props, String propKey, String settingKey, int defval )
		{
			makeSetting ( props, propKey, settingKey, "" + defval );
		}

		private void makeSetting ( Properties props, String propKey, String settingKey, boolean defval )
		{
			makeSetting ( props, propKey, settingKey, "" + defval );
		}

		@Override
		public void run ()
		{
			final StringBuffer tag = new StringBuffer ();
			final StringBuffer addrList = new StringBuffer ();
			tag.append ( "(" );
			for ( String to : fToAddrs )
			{
				if ( addrList.length () > 0 )
				{
					addrList.append ( ", " );
				}
				addrList.append ( to );
			}
			tag.append ( addrList.toString () );
			tag.append ( ") \"" );
			tag.append ( fSubject );
			tag.append ( "\"" );
			
			log.info ( "sending mail to " + tag );

			try
			{
				final Properties prop = new Properties ();
				makeSetting ( prop, "mail.smtp.port", kSetting_SmtpServerPort, 587 );
				prop.put ( "mail.smtp.socketFactory.fallback", "false" );
				prop.put ( "mail.smtp.quitwait", "false" );
				makeSetting ( prop, "mail.smtp.host", kSetting_SmtpServer, "smtp.it.onap.com" );
				makeSetting ( prop, "mail.smtp.auth", kSetting_SmtpServerUseAuth, true );
				makeSetting ( prop, "mail.smtp.starttls.enable", kSetting_SmtpServerSsl, true );

				final String un = getSetting ( kSetting_MailAuthUser, "" );
				final String value=(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"mailPassword")!=null)?AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"mailPassword"):"";
				final Session session = Session.getInstance ( prop,
					new javax.mail.Authenticator()
					{
						@Override
						protected PasswordAuthentication getPasswordAuthentication()
						{
							return new PasswordAuthentication ( un, value );
						}
					}
				);
				
				final Message msg = new MimeMessage ( session );

				final InternetAddress from = new InternetAddress (
					getSetting ( kSetting_MailFromEmail, "team@dmaap.mr.onap.com" ),
					getSetting ( kSetting_MailFromName, "The GFP/SA2020 Team" ) );
				msg.setFrom ( from );
				msg.setReplyTo ( new InternetAddress[] { from } );
				msg.setSubject ( fSubject );

				for ( String toAddr : fToAddrs )
				{
					final InternetAddress to = new InternetAddress ( toAddr );
					msg.addRecipient ( Message.RecipientType.TO, to );
				}

				final Multipart multipart = new MimeMultipart ( "related" );
				final BodyPart htmlPart = new MimeBodyPart ();
				htmlPart.setContent ( fBody, "text/plain" );
				multipart.addBodyPart ( htmlPart );
				msg.setContent ( multipart );

				Transport.send ( msg );

				log.info ( "mailing " + tag + " off without error" );
			}
			catch ( Exception e )
			{
				log.warn ( "Exception caught for " + tag, e );
			}
		}

		private final String[] fToAddrs;
		private final String fSubject;
		private final String fBody;
	}
}
