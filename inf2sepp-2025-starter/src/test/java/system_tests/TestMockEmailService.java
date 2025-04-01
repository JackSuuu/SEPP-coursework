package system_tests;

import external.MockEmailService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMockEmailService {

    private final MockEmailService emailService = new MockEmailService();

    // Valid email address -> STATUS_SUCCESS
    @Test
    public void testValidEmail() {
        int result = emailService.sendEmail("user@example.com", "recipient@example.com", "Subject", "Hello world!");
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }

    // Invalid sender email -> STATUS_INVALID_SENDER_EMAIL
    @Test
    public void testInvalidSenderEmail() {
        int result = emailService.sendEmail("invalid-email", "recipient@example.com", "Subject", "Hello!");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result);
    }

    // Invalid recipient email -> STATUS_INVALID_RECIPIENT_EMAIL
    @Test
    public void testInvalidRecipientEmail() {
        int result = emailService.sendEmail("user@example.com", "bad@wrong", "Subject", "Hi there!");
        assertEquals(MockEmailService.STATUS_INVALID_RECIPIENT_EMAIL, result);
    }

    // Null sender email -> STATUS_INVALID_SENDER_EMAIL
    @Test
    public void testNullSender() {
        int result = emailService.sendEmail(null, "recipient@example.com", "Subject", "Oops");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result);
    }

    // Null recipient email -> STATUS_INVALID_RECIPIENT_EMAIL
    @Test
    public void testNullRecipient() {
        int result = emailService.sendEmail("user@example.com", null, "Subject", "Oops");
        assertEquals(MockEmailService.STATUS_INVALID_RECIPIENT_EMAIL, result);
    }

    // Edge-case but valid emails -> STATUS_SUCCESS
    @Test
    public void testEdgeCaseValidEmails() {
        int result1 = emailService.sendEmail("first.last+tag@sub.domain.co", "foo_bar-123@domain.ai", "Hello", "Test body");
        int result2 = emailService.sendEmail("x_y+test@uni.edu", "x@x.co", "Short Subject", "Yup");

        assertEquals(MockEmailService.STATUS_SUCCESS, result1);
        assertEquals(MockEmailService.STATUS_SUCCESS, result2);
    }

    // Empty subject and content but valid addresses -> STATUS_SUCCESS
    @Test
    public void testEmptyContentValidEmails() {
        int result = emailService.sendEmail("email1@example.com", "email2@example.com", "", "");
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }

    // Max length subject/body -> STATUS_SUCCESS
    @Test
    public void testMaxLengthFields() {
        String longBody = "A".repeat(5000);
        String longSubject = "S".repeat(200);
        int result = emailService.sendEmail("valid.sender@example.com", "valid.recipient@example.com", longSubject, longBody);
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }

    // Both emails empty strings -> STATUS_INVALID_SENDER_EMAIL (sender check fails first)
    @Test
    public void testEmptySenderAndRecipient() {
        int result = emailService.sendEmail("", "", "Subject", "Content");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result);
    }

    // Unorthodox valid chars in address -> STATUS_SUCCESS
    @Test
    public void testWeirdButValidEmailCharacters() {
        int result = emailService.sendEmail("foo_bar+123@domain.io", "abc.def+xyz@sub-domain.com", "Subject", "Still valid?");
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }

    // Whitespace around sender (invalid) -> STATUS_INVALID_SENDER_EMAIL
    @Test
    public void testSenderWithWhitespace() {
        int result = emailService.sendEmail(" user@example.com ", "recipient@example.com", "Subject", "Spaces?");
        assertEquals(MockEmailService.STATUS_INVALID_SENDER_EMAIL, result);
    }

    // Recipient with Unicode (invalid) -> STATUS_INVALID_RECIPIENT_EMAIL
    @Test
    public void testRecipientWithUnicode() {
        int result = emailService.sendEmail("user@example.com", "あ@ドメイン.jp", "Subject", "Oops!");
        assertEquals(MockEmailService.STATUS_INVALID_RECIPIENT_EMAIL, result);
    }

    // Null subject but valid email -> STATUS_SUCCESS
    @Test
    public void testNullSubject() {
        int result = emailService.sendEmail("user@example.com", "recipient@example.com", null, "Body works");
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }

    // Null content but valid email -> STATUS_SUCCESS
    @Test
    public void testNullContent() {
        int result = emailService.sendEmail("user@example.com", "recipient@example.com", "Subject", null);
        assertEquals(MockEmailService.STATUS_SUCCESS, result);
    }
}
