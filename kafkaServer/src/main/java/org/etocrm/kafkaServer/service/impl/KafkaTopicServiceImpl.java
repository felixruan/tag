package org.etocrm.kafkaServer.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.etocrm.kafkaServer.service.IKafkaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @Author chengrong.yang
 * @Date 2020/11/4 14:51
 */
@Service
@Slf4j
public class KafkaTopicServiceImpl implements IKafkaTopicService {

    @Autowired
    private AdminClient adminClient;

    @Override
    public boolean createTopic(String topicName, int partions, short broker) {
        try {
            NewTopic topic = new NewTopic(topicName, partions, broker);
            CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(topic));
            topics.all().get();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Set<String> getAllTopic() {
        try {
            ListTopicsResult listTopics = adminClient.listTopics();
            Set<String> topics = listTopics.names().get();
            return topics;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteTopic(String topic){
        try{
            adminClient.deleteTopics(Arrays.asList(topic));
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
