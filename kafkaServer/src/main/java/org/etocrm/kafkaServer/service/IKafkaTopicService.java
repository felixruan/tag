package org.etocrm.kafkaServer.service;

import java.util.Set;

/**
 * @Author chengrong.yang
 * @Date 2020/11/4 14:50
 */
public interface IKafkaTopicService {

    boolean createTopic(String topicName, int partions, short broker);

    Set<String> getAllTopic();

    boolean deleteTopic(String topic);
}
