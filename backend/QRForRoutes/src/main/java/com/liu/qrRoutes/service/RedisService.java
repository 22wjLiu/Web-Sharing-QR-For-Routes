package com.liu.qrRoutes.service;

import com.liu.qrRoutes.config.Constants;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate stringRedisTemplate;
  private final MailService mailService;


  public void sendRegisterCode(String email) {
    String key = Constants.REGISTER_CODE_KEY + email;
    String code = stringRedisTemplate.opsForValue().get(key);

    if(StringUtil.isNullOrEmpty(code)) {
      // 生成 4 位数字验证码
      int raw = ThreadLocalRandom.current().nextInt(0, 10000); // 0~9999
      code = String.format("%04d", raw);

      // 存 Redis，5分钟
      stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
    }

    // 发送邮件
    mailService.sendVerifyCode(email, code);
  }

  public boolean validateRegisterCode(String email, String code) {
    String key = Constants.REGISTER_CODE_KEY + email;
    String cached = stringRedisTemplate.opsForValue().get(key);
    return code != null && code.equals(cached);
  }

  public void overrideLogin(Integer id, String jti) {
    String key = Constants.LOGIN_TOKEN_KEY + id;
    stringRedisTemplate.opsForValue().set(
      key, jti, Duration.ofHours(2)   // TTL 与 token 同步
    );
  }

  public void removeUserInfo(Integer id) {
    stringRedisTemplate.delete(Constants.LOGIN_TOKEN_KEY + id);
  }

  public String getUserJti(Integer i) {
    String key = Constants.LOGIN_TOKEN_KEY + i;
    return stringRedisTemplate.opsForValue().get(key);
  }
}

