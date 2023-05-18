package com.zlg.pressurer2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@SpringBootTest
class PressureR2ApplicationTests {

    @Test
    void test() {
        String raw = " total_energy 24.72 today_energy 89.56 temperature 73.75 gfci 92.12 bus_volt 49.65 power 50.15 q_power 16.09 pf 34.16 pv1_volt 23.91 pv1_curr 63.48 pv2_volt 68.01 pv2_curr 17.05 pv3_volt 8.34 pv3_curr 13.32 l1_volt 67.82 l1_curr 32.72 l1_freq 93.95 l1_dci 8.34 l1_power 43.7 l1_pf 1.14 ";
        String replace = raw.replace(" ", "\0");
        byte[] bytes = replace.getBytes();
//        System.out.println(Arrays.toString(bytes));
        String base64 = Base64.getEncoder().encodeToString(bytes);
        System.out.println(base64);


//        String base64 = "XDB0b3RhbF9lbmVyZ3lcMDI0LjcyXDB0b2RheV9lbmVyZ3lcMDg5LjU2XDB0ZW1wZXJhdHVyZVwwNzMuNzVcMGdmY2lcMDkyLjEyXDBidXNfdm9sdFwwNDkuNjVcMHBvd2VyXDA1MC4xNVwwcV9wb3dlclwwMTYuMDlcMHBmXDAzNC4xNlwwcHYxX3ZvbHRcMDIzLjkxXDBwdjFfY3VyclwwNjMuNDhcMHB2Ml92b2x0XDA2OC4wMVwwcHYyX2N1cnJcMDE3LjA1XDBwdjNfdm9sdFwwOC4zNFwwcHYzX2N1cnJcMDEzLjMyXDBsMV92b2x0XDA2Ny44MlwwbDFfY3VyclwwMzIuNzJcMGwxX2ZyZXFcMDkzLjk1XDBsMV9kY2lcMDguMzRcMGwxX3Bvd2VyXDA0My43XDBsMV9wZlwwMS4xNFww";
//        byte[] decode = Base64.getDecoder().decode(base64);
//        System.out.println(new String(decode, StandardCharsets.UTF_8));


    }

}
