package org.etocrm.kafkaServer.service;

/**
 * @Author chengrong.yang
 * @Date 2020/11/4 14:35
 */
public interface IKafkaProducerService {

    void sendMessage(String topic, Object obj, int listIndex);
    void sendMessage(String topic, Object obj);


}
