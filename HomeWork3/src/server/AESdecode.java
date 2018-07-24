package server;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author lihui
 * @version 2018.7.17
 */

public class AESdecode {
    public static Cipher initAESDecodeCipher(String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");  // 创建AES的Key生产者
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();  // 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();  // 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  // 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");  // 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);  // 初始化为解密模式的密码器
            return cipher;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
