package com.zlg.pressurer2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest
class PressureR2ApplicationTests {

    @Test
    void test() {
        String base64 = "XDB0b3RhbF9lbmVyZ3lcMDI0LjcyXDB0b2RheV9lbmVyZ3lcMDg5LjU2XDB0ZW1wZXJhdHVyZVwwNzMuNzVcMGdmY2lcMDkyLjEyXDBidXNfdm9sdFwwNDkuNjVcMHBvd2VyXDA1MC4xNVwwcV9wb3dlclwwMTYuMDlcMHBmXDAzNC4xNlwwcHYxX3ZvbHRcMDIzLjkxXDBwdjFfY3VyclwwNjMuNDhcMHB2Ml92b2x0XDA2OC4wMVwwcHYyX2N1cnJcMDE3LjA1XDBwdjNfdm9sdFwwOC4zNFwwcHYzX2N1cnJcMDEzLjMyXDBsMV92b2x0XDA2Ny44MlwwbDFfY3VyclwwMzIuNzJcMGwxX2ZyZXFcMDkzLjk1XDBsMV9kY2lcMDguMzRcMGwxX3Bvd2VyXDA0My43XDBsMV9wZlwwMS4xNFww";
        byte[] decode = Base64.getDecoder().decode(base64);
        System.out.println(new String(decode, StandardCharsets.UTF_8));


    }

}
