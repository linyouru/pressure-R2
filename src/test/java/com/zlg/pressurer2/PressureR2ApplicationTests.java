package com.zlg.pressurer2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class PressureR2ApplicationTests {

    AtomicInteger flag = new AtomicInteger(0);
    int localAddressTotal = 5;

    @Test
    void test() {

        for (int i = 0; i < 10; i++) {
            System.out.println(polling());
        }
    }

    int polling(){
        if(flag.get() < localAddressTotal - 1){
            return flag.incrementAndGet();
        }else{
            flag.set(0);
            return 0;
        }
    }

    void getSendData() {
        String raw = " total_energy 24.72 today_energy 89.56 temperature 73.75 gfci 92.12 bus_volt 49.65 power 50.15 q_power 16.09 pf 34.16 pv1_volt 23.91 pv1_curr 63.48 pv2_volt 68.01 pv2_curr 17.05 pv3_volt 8.34 pv3_curr 13.32 l1_volt 67.82 l1_curr 32.72 l1_freq 93.95 l1_dci 8.34 l1_power 43.7 l1_pf 1.14 ";
        String replace = raw.replace(" ", "\0");
        byte[] bytes = replace.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        System.out.println(base64);
    }

}
