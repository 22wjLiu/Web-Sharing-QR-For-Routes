package com.liu.qrRoutes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  @Value("${spring.mail.username}")
  private String from;

  public void sendVerifyCode(String to, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject("【戏曲地图】邮箱验证码");
    message.setText("您本次的验证码是：" + code + "，5分钟内有效，请勿告知他人。");
    mailSender.send(message);
  }
}
