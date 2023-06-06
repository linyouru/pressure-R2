package com.zlg.pressurer2.helper.mqtt;

import com.zlg.pressurer2.pojo.PressureMqttClient;
import com.zlg.pressurer2.service.AsyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SendData implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //数据上报最小周期
    private static final int granule = 100;
    private List<PressureMqttClient> mqttClientList;
    private AsyncTaskService asyncTaskService;
    private Integer period;
    private byte[] send;
    private String topic;

    public void setSend(byte[] send) {
        this.send = send;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public void setAsyncTaskService(AsyncTaskService asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    public void setMqttClientList(List<PressureMqttClient> pressureMqttClient) {
        this.mqttClientList = pressureMqttClient;
    }

    @Override
    public void run() {
//        logger.debug("[线程ID： {}] 定时任务执行时刻: {}", Thread.currentThread().getId(), System.currentTimeMillis());
        int size = mqttClientList.size();
        //一个周期内分为多少份
        int part = period / granule;
        int bucket = size / part;
        int startIndex = 0;
        int endIndex;
        for (int i = 1; i <= part; i++) {
            long startTime = System.currentTimeMillis();
            endIndex = i == part ? size : i * bucket;
            List<PressureMqttClient> pressureMqttClients = mqttClientList.subList(startIndex, endIndex);
            startIndex = endIndex;
//            logger.debug("第{}批任务上报,设备数：{}", i, bucket);
            for (PressureMqttClient pressureMqttClient : pressureMqttClients) {
                asyncTaskService.deviceSendData(pressureMqttClient, send, topic);
            }
            long endTime = System.currentTimeMillis();
            long difference = endTime - startTime;
            if (difference < granule) {
                try {
                    Thread.sleep(granule - difference);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
