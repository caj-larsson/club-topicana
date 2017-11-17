package com.github.ftrossbach.club_topicana.core;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.jupiter.api.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class MultiBrokerIntegrationTest {

    private static String bootstrapServers = null;
    private static EmbeddedKafka embeddedKafkaCluster = null;

    private TopicComparer unitUnderTest;


    @BeforeAll
    public static void initKafka() throws Exception{
        embeddedKafkaCluster = new EmbeddedKafka(2);

        embeddedKafkaCluster.start();
        bootstrapServers = embeddedKafkaCluster.bootstrapServers();

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try(AdminClient ac = AdminClient.create(props)){


            NewTopic testTopic = new NewTopic("test_topic", 1, (short) 2);
            ac.createTopics(Collections.singleton(testTopic)).all().get();

        }
    }

    @BeforeEach
    public void setUp(){
        unitUnderTest = new TopicComparer(bootstrapServers);
    }



    @Nested
    class ReplicationFactor{
        @Test
        public void rf_fits(){

            ExpectedTopicConfiguration expected = new ExpectedTopicConfiguration.ExpectedTopicConfigurationBuilder("test_topic").withReplicationFactor(2).build();

            ComparisonResult result = unitUnderTest.compare(Collections.singleton(expected));

            assertTrue(result.ok());


        }

        @Test
        public void rf_fits_not(){

            ExpectedTopicConfiguration expected = new ExpectedTopicConfiguration.ExpectedTopicConfigurationBuilder("test_topic").withReplicationFactor(1).build();

            ComparisonResult result = unitUnderTest.compare(Collections.singleton(expected));

            assertAll(() -> assertFalse(result.ok()),
                    () -> assertThat(result.getMismatchingReplicationFactor().get("test_topic").getExpectedValue(), is(equalTo(1))),
                    () -> assertThat(result.getMismatchingReplicationFactor().get("test_topic").getActualValue(), is(equalTo(2))));


        }


    }




    @AfterAll
    public static void destroyKafka(){

        embeddedKafkaCluster.stop();
    }


}
