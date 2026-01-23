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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.astondevs.learn.vorobev.dto.UserEvent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events"},
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" }
)
class KafkaConsumerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(true);

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @BeforeEach
    void cleanup() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void shouldConsumeKafkaMessageAndSendEmail() {
        String email = "kafka-user@example.com";
        UserEvent event = new UserEvent(email, "CREATE");
        kafkaTemplate.send("user-events", event);
        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
                    assertEquals(1, receivedMessages.length, "Должно быть получено 1 письмо");
                    MimeMessage msg = receivedMessages[0];
                    assertEquals(email, msg.getAllRecipients()[0].toString());
                    assertTrue(msg.getContent().toString().contains("Ваш аккаунт на сайте был успешно создан"));
                });
    }
}