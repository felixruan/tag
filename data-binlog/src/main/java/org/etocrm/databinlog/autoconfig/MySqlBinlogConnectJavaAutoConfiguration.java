package org.etocrm.databinlog.autoconfig;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.databinlog.autoconfig.properties.MySqlBinlogConnectJavaProperties;
import org.etocrm.databinlog.autoconfig.service.BinlogInfoService;
import org.etocrm.databinlog.autoconfig.service.DataCollectionService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;


/**
 * mysql-binlog-connector-java 配置类
 */
@Configuration
@EnableConfigurationProperties({MySqlBinlogConnectJavaProperties.class})
@ConditionalOnBean({DataCollectionService.class, BinlogInfoService.class})
@ConditionalOnProperty("mysql-binlog-connect-java.datasource.hostname")
@AllArgsConstructor
@Slf4j
public class MySqlBinlogConnectJavaAutoConfiguration {

    private final MySqlBinlogConnectJavaProperties mySqlBinlogConnectJavaProperties;

    private final DataCollectionService dataCollectionService;

    private final ApplicationContext context;

    private final BinlogInfoService binlogInfoService;

    @PostConstruct
    public void init() throws IOException {

        String hostname = mySqlBinlogConnectJavaProperties.getHostname();
        String schema = mySqlBinlogConnectJavaProperties.getSchema();
        Integer port = mySqlBinlogConnectJavaProperties.getPort();
        String username = mySqlBinlogConnectJavaProperties.getUsername();
        String password = mySqlBinlogConnectJavaProperties.getPassword();

        // 配置客户端连接
        BinaryLogClient client = new BinaryLogClient(hostname, port, schema, username, password);

        // 配置事件序列化策略
        EventDeserializer eventDeserializer = getEventDeserializer();
        if (eventDeserializer != null) {
            client.setEventDeserializer(eventDeserializer);
        }

        // 添加事件处理器
        client.registerEventListener(dataCollectionService::collectionIncrementalData);

        String filename = binlogInfoService.getBinlogFileName();
        if (StringUtils.isNotEmpty(filename)) {
            // 设置 Binlog 文件名
            client.setBinlogFilename(filename);

            Long position = binlogInfoService.getBinlogNextPosition();
            if (position != null) {
                // 设置 Binlog 起始位置
                client.setBinlogPosition(position);
                log.error("读取binlog文件={},偏移量={}", filename, position);
            }
        }
        client.connect();
    }

    /**
     * 获取事件序列化规则
     *
     * @return {@link EventDeserializer}
     * @author nza
     * @createTime 2020/12/21 13:28
     */
    private EventDeserializer getEventDeserializer() {
        EventDeserializer eventDeserializer;
        try {
            // 先从容器中获取
            eventDeserializer = context.getBean(EventDeserializer.class);
        } catch (NoSuchBeanDefinitionException e) {
            eventDeserializer = null;
            // 设置字段序列化规则
//            eventDeserializer = new EventDeserializer();
//            eventDeserializer.setCompatibilityMode(
//                    EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//                    EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
//            );
        }
        return eventDeserializer;
    }
}
