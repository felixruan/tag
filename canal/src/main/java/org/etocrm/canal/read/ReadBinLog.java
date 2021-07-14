package org.etocrm.canal.read;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.canal.handler.CanalHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Author: dkx
 * @Date: 17:56 2020/11/13
 * @Desc:
 */
@Component
@Slf4j
public class ReadBinLog implements ApplicationRunner {

    @Value("${canal.host}")
    private String host;

    @Value("${canal.port}")
    private int port;

    @Value("${canal.username}")
    private String username;

    @Value("${canal.password}")
    private String password;

    @Value("${canal.instance}")
    private String instance;

    @Value("${ETL.CANAL.NUMBER}")
    private int number;

//    @Value("${schemaName}")
//    private String schemaName;

    @Autowired
    CanalHandler canalHandler;

    /**
     * 获取连接
     */
    public CanalConnector getConn() {
        log.error("host:{},port:{port},instance:{},username:{},password:{}", host, port, instance, username, password);
        return CanalConnectors.newSingleConnector(new InetSocketAddress(host, port), instance, username, password);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CanalConnector conn = this.getConn();
        while (true) {
            try {
                conn.connect();
                //订阅实例中所有的数据库和表
                conn.subscribe(".*\\..*");
                //conn.subscribe(schemaName);
                Message message = conn.getWithoutAck(number);

                long id = message.getId();
                int size = message.getEntries().size();
                if (id != -1 && size > 0) {
                    log.info("读取条数：" + message.getEntries().size());
                    canalHandler.analysis(message.getEntries());
                } else {
                    try {
                        String hostAddress = InetAddress.getLocalHost().getHostAddress();
                        log.info("监控源实例名称：{}，没有数据，休息一会啦，canal所在服务器地址：{}", instance, hostAddress);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 确认消息
                if (id != -1) {
                    conn.ack(id);
                }
            } catch (CanalClientException e) {
                log.error(e.getMessage(), e);
                // 处理失败, 回滚数据
                conn.rollback();
            } finally {
                conn.disconnect();
            }
        }
    }
}
