package ru.astondevs.learn.vorobev;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import ru.astondevs.learn.vorobev.dto.UserEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanup() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void testSendEmailViaApi() throws Exception {
        UserEvent event = new UserEvent("test@example.com", "CREATE");
        HttpEntity<UserEvent> request = new HttpEntity<>(event);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/notifications/send", request, String.class);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        MimeMessage msg = receivedMessages[0];
        assertEquals("test@example.com", msg.getAllRecipients()[0].toString());
        assertEquals("Уведомление от сайта", msg.getSubject());
        String body = msg.getContent().toString().trim();
        assertTrue(body.contains("Ваш аккаунт на сайте был успешно создан"));
    }

    @Test
    void testSendDeleteEmail() throws Exception {
        UserEvent event = new UserEvent("deleted@example.com", "DELETE");
        restTemplate.postForEntity("/api/v1/notifications/send", new HttpEntity<>(event), String.class);
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        MimeMessage msg = greenMail.getReceivedMessages()[0];
        assertTrue(msg.getContent().toString().contains("Ваш аккаунт был удалён"));
    }
}
