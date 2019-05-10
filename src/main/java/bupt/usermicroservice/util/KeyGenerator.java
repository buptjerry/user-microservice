package bupt.usermicroservice.util;

import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class KeyGenerator {
    public Key generateKey() {
        String keyString = "simplekey";
        return new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
    }
}