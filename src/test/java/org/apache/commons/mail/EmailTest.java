package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//Class containing test cases for the functionality of the org.apache.commons.mail.Email class
public class EmailTest {
	
	private static final String[] TEST_EMAILS = {"aaa@aaa.com", "bbb@bbb.org", "ccc@ccc.edu" };
	
	// EmailConcrete instance
	private EmailConcrete email;
	
	// Setup method: initialize the EmailConcrete object before each test
	@Before
	public void setUpEmailTest() throws Exception {
		email = new EmailConcrete();
	}
	
	// Tear down method: clean up the EmailConcrete object after each test
	@After
	public void tearDownEmailTest() throws Exception {
		email = null;
	}
	
	// Test case: check addBcc() behavior with valid input
	@Test
	public void testAddBccWithValidInput() throws Exception { 
		email.addBcc(TEST_EMAILS);
	    List<InternetAddress> bccList = email.getBccAddresses();
		assertEquals(3, bccList.size());
	}
	
	// Test case: check addBcc() when passing null
	@Test
	public void testAddBccWithNull() {
	    String[] emailobj = null;
		try {
	        email.addBcc(emailobj);
	        fail("EmailException was expected to be thrown when the email array is null");
	    } catch (EmailException e) {
	        assertEquals("Address List provided was invalid", e.getMessage());
	    }
	}

	// Test case: check addBcc() when passing empty array
	@Test
	public void testAddBccWithEmptyArray() {
	    String[] emailobj = {};
		try {
	        email.addBcc(emailobj);
	        fail("EmailException was expected to be thrown when the email array is empty");
	    } catch (EmailException e) {
	        assertEquals("Address List provided was invalid", e.getMessage());
	    }
	}
    
	// Test case: check addCc() with valid input
	@Test
    public void testAddCcWithValidInput() throws Exception { 
        email.addCc("aaa@bbb.com");
        List<InternetAddress> ccList = email.getCcAddresses();
        assertEquals(1, ccList.size());
    }
	
	// Test case: check addHeader() with null name
	@Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullName() {
        email.addHeader(null, "1");
    }
    
	// Test case: check addHeader() with empty name
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyName() {
        email.addHeader("", "2");
    }

	// Test case: check addHeader() with null value
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullValue() {
        email.addHeader("test header", null);
    }

	// Test case: check addHeader() with empty value
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyValue() {
        email.addHeader("test header", "");
    }
    
	// Test case: check addHeader() with valid name and value
    @Test
    public void testAddHeaderWithValidNameAndValue() {
    	String name = "test header";
    	String value = "123";
    	email.addHeader(name, value);
    	assertEquals("test header", name);
    	assertEquals("123", value);
    }
	
	// Test case: check addReplyTo() behavior with valid input
    @Test
    public void testAddReplyToWithValidInput() throws EmailException {
        String emailobj = "aaa@ccc.com";
        String name = "Aidil";
        email.addReplyTo(emailobj, name);
        
        List<InternetAddress> replyToList = email.getReplyToAddresses();
        boolean hasReplyTo = false;
        for (InternetAddress address : replyToList) {
            if (address.getAddress().equals(emailobj) && address.getPersonal().equals(name)) {
                hasReplyTo = true;
                break;
            }
        }
        
        assertTrue(hasReplyTo);
    }
    
    // Test case: check buildMimeMessage() when being called twice
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageNull() throws Exception {
    		email.setHostName("localhost");
    		email.setSmtpPort(1234);
    		email.setFrom("aaa@bbb.com");
    		email.setSubject("test mail");
    		email.setCharset("ISO-8859-1");
    		email.setContent("test content", "text/plain");
    		email.buildMimeMessage();
    		email.buildMimeMessage();
    }
    
	// Test case: check buildMimeMessage() successfully built
    @Test
    public void testBuildMimeMessageSuccessBuilt() throws Exception {
        email.setHostName("localhost");
        email.setFrom("aaa@bbb.com");
        email.addTo("ccc@ddd.com");
        
        email.buildMimeMessage();
        assertNotNull("MimeMessage should not be null", email.getMimeMessage());
    }
    
    // Test case: check buildMimeMessage() without From being set
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFrom() throws Exception {
        email.buildMimeMessage();
    }
    
    // Test case: check buildMimeMessage() without Recipients
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutRecipients() throws Exception {
        email.setFrom("aaa@bbb.com");
        email.buildMimeMessage();
    }
    
    // Test case: check buildMimeMessage() with empty From
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageEmptyFrom() throws Exception {
        email.setFrom("");
        email.buildMimeMessage();
    }

    // Test case: check buildMimeMessage() with header being set
    @Test
    public void testBuildMimeMessageWithHeader() throws Exception {
    	email.setHostName("localhost");
    	email.setFrom("aaa@bbb.com");
        email.addHeader("Header Test", "2");
        email.addTo("ccc@ddd.com");
        email.buildMimeMessage();

        MimeMessage mimeMessage = email.getMimeMessage();
        assertEquals("2", mimeMessage.getHeader("Header Test")[0]);
    }
    
    // Test case: check buildMimeMessage() without ReplyTO
    @Test
    public void testBuildMimeMessageWithReplyTo() throws EmailException, MessagingException {
        email.setHostName("smtp.example.com");
        email.setFrom("aaa@bbb.com");
        email.addTo("ccc@ddd.com");
        email.addReplyTo("replyto@ddd.com");

        email.buildMimeMessage();
        
        MimeMessage mimeMessage = email.getMimeMessage();
        assertNotNull(mimeMessage.getReplyTo());
        assertEquals("replyto@ddd.com", ((InternetAddress)mimeMessage.getReplyTo()[0]).getAddress());
    }
    
    // Test case: check buildMimeMessage() without ReplyTo
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutReplyTo() throws EmailException {
        email.setFrom("aaa@bbb.com");
        email.buildMimeMessage();
    }
    
    // Test case: check buildMimeMessage() with Subject
    @Test
    public void testBuildMimeMessageWithSubject() throws Exception {

        email.setHostName("localhost");
        email.setFrom("aaa@bbb.com");
        email.addTo("ccc@ddd.com");
        email.setSubject("test subject");

        email.buildMimeMessage();
        assertEquals("test subject", email.getMimeMessage().getSubject());
    }
    
    // Test case: check buildMimeMessage() with Content and its type
    @Test
    public void testBuildMimeMessageWithContent() throws Exception {

        email.setHostName("localhost");
        email.setFrom("aaa@bbb.com");
        email.addTo("ccc@ddd.com");
        email.setSubject("test mail");
        email.setContent("content test", "text/plain");

        email.buildMimeMessage();
        assertEquals("content test", email.getMimeMessage().getContent());
    }
    
    // Test case: check buildMimeMessage() with Sent Date
    @Test
    public void testBuildMimeMessageWithSentDate() throws Exception {
        Date date = new Date();

        email.setHostName("localhost");
        email.setFrom("aaa@bbb.com");
        email.addTo("ccc@ddd.com");
        email.setSentDate(date);

        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage().getSentDate());
    }
    
    // Test case: check buildMimeMessage() without From and throws Exception
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFromThrowsException() throws Exception {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        Session session = Session.getInstance(properties);

        email.setMailSession(session);
        email.buildMimeMessage();
    }
    
    // Test case: check buildMimeMessage() with Cc
    @Test
    public void testBuildMimeMessageWithCc() throws Exception {
        String ccEmail = "ccc@ddd.com";

        email.setHostName("localhost");
        email.setFrom("aa@aa.com");
        email.addTo("bb@bb.com");
        email.addCc(ccEmail);
        email.buildMimeMessage();
        
        InternetAddress[] cc = (InternetAddress[]) email.getMimeMessage().getRecipients(Message.RecipientType.CC);
        assertNotNull(cc);
        assertEquals(1, cc.length);
        assertEquals(ccEmail, cc[0].getAddress());
    }
    
    // Test case: check buildMimeMessage() with Bcc
    @Test
    public void testBuildMimeMessageWithBcc() throws Exception {
        String bccEmail = "ccc@ddd.com";

        email.setHostName("localhost");
        email.setFrom("aa@aa.com");
        email.addTo("bb@bb.com");
        email.addBcc(bccEmail);
        email.buildMimeMessage();
        
        InternetAddress[] bcc = (InternetAddress[]) email.getMimeMessage().getRecipients(Message.RecipientType.BCC);
        assertNotNull(bcc);
        assertEquals(1, bcc.length);
        assertEquals(bccEmail, bcc[0].getAddress());
    }
    
    // Test case: check getHostName() from Session
    public void testGetHostNameFromSession() throws Exception {
        Properties prop = new Properties();
        prop.put(EmailConstants.MAIL_HOST, "mail.smtp.host");
        Session session = Session.getInstance(prop);

        email.setMailSession(session);

        String hostName = email.getHostName();
        assertEquals("mail.smtp.host", hostName);
    }
    
    // Test case: check getHostName() with null session, but Host Name is set
    @Test
    public void testGetHostNameWithNullSessionAndHostNameIsSet() {
        String hostNameObj = "mail.smtp.host";
        email.setHostName(hostNameObj);
        String hostName = email.getHostName();

        assertEquals(hostNameObj, hostName);
    }
    
    // Test case: check getHostName() with null Host Name
    @Test
    public void testGetHostNameWithHostNameIsNotSet() {
        email.setHostName(null);

        String hostName = email.getHostName();

        assertNull("Return null when session and host name are not set", hostName);
    }
    
    // Test case: check getHostName() with empty Host Name
    @Test
    public void testGetHostNameWithEmptyHostName() {
        email.setHostName("");

        String hostName = email.getHostName();

        assertNull("Return null when hostName is empty", hostName);
    }
    
    // Test case: check getMailSession() with valid input
    @Test
    public void testGetSession() throws EmailException {
    	Properties prop = new Properties();
    	prop.setProperty("testprop", "email prop");
    	
    	Session sessionobj = Session.getInstance(prop);
    	email.setMailSession(sessionobj);
    	Session gsession = email.getMailSession();
    	
    	Properties getprop = gsession.getProperties();
    	String myteststr = (String) getprop.get("testprop");
    	
    	assertEquals(myteststr, "email prop");
    }
    
    // Test case: check getMailSession() with null
	@Test
   public void testGetMailSessionWithNull() throws EmailException {
    	email.setHostName("localhost");
    	Session session = email.getMailSession();
    	
    	Properties getprop = session.getProperties();
    	String protocol = (String) getprop.get("mail.transport.protocol");
    	assertEquals(protocol, EmailConstants.SMTP);
    }
    
    // Test case: check getMailSession() with SSL on connect
	@Test
    public void testGetMailSessionWithSSLOnConnect() throws EmailException {
    	email.setHostName("localhost");
    	email.setSSLOnConnect(true);
    	Session session = email.getMailSession();
    	
    	Properties getprop = session.getProperties();
    	String protocol = (String) getprop.get("mail.transport.protocol");
    	assertEquals(protocol, EmailConstants.SMTP);
    }
	
    // Test case: check getMailSession() with empty Host Name
	@Test(expected = EmailException.class)
    public void testGetMailSessionWithoutHostname() throws EmailException {
        email.setHostName("");
        email.getMailSession();
    }
	
    // Test case: check getMailSession() with SmtpPort being set
	@Test
    public void testGetMailSessionWithSmtpPort() throws Exception {
        email.setHostName("localhost");
        email.setSmtpPort(25);
        Session session = email.getMailSession();
        
        Properties prop = session.getProperties();
        assertEquals("25", prop.get(EmailConstants.MAIL_PORT));
    }
	
    // Test case: check getMailSession() with Authenticator
	@Test
    public void testGetMailSessionWithAuthenticator() throws Exception {
        email.setHostName("localhost");
        email.setAuthentication("userName", "password");
        Session session = email.getMailSession();
        
        Properties prop = session.getProperties();
        assertEquals("true", prop.get(EmailConstants.MAIL_SMTP_AUTH));
    }
	
    // Test case: check getMailSession() with StartTls enabled
	@Test
    public void testGetMailSessionWithStartTlsEnabled() throws Exception {
        email.setHostName("localhost");
        email.setStartTLSEnabled(true);
        Session session = email.getMailSession();
        
        Properties prop = session.getProperties();
        assertEquals("true", prop.get(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE));
    }
    
    // Test case: check getSentDate()
    @Test
    public void testGetSentDate() throws Exception {
    	  Date expectedDate = new Date();
          email.setSentDate(expectedDate);

          Date actualSentDate = email.getSentDate();

          assertNotSame(expectedDate, actualSentDate);
          assertEquals(expectedDate.getTime(), actualSentDate.getTime());
    }
    
    // Test case: check getSentDate() without setting the date
    @Test
    public void testGetSentDateWithDateNotSet() {
        Date currentDate = new Date();
        Date sentDate = email.getSentDate();

        assertNotNull(sentDate);
        long timeDiff = Math.abs(sentDate.getTime() - currentDate.getTime());
        assertTrue(timeDiff < 1000);
    }
    
    // Test case: check getSocketConnectionTimeout() with default value
    @Test
    public void testGetSocketConnectionTimeoutDefaultValue() throws Exception {
        assertEquals(EmailConstants.SOCKET_TIMEOUT_MS, email.getSocketConnectionTimeout());
    }

    // Test case: check getSocketConnectionTimeout() with value being set
    @Test
    public void testGetSocketConnectionTimeoutSetValue() throws Exception {
        email.setSocketConnectionTimeout(30000);
       
        assertEquals(30000, email.getSocketConnectionTimeout());
    }
    
    // Test case: check setFrom()
    @Test
    public void testSetFrom() throws EmailException {
    	String emailobj = "aaa@aaa.com";
    	email.setFrom(emailobj);
    	
    	assertEquals("aaa@aaa.com", emailobj);
    }
    
    // Test case: check setFrom() with invalid email address
    @Test(expected = EmailException.class)
    public void testSetFromInvalidEmail() throws EmailException {
    	String invalidEmail = "aaa_bbb";
    	email.setFrom(invalidEmail);
    }
    
}
