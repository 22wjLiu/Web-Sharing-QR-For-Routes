package com.liu.qrRoutes.config;

public class Constants {
  public static final String REGISTER_CODE_KEY = "email:code:register:";
  public static final String LOGIN_TOKEN_KEY = "login:token:";

  public static class SendCode {
    public static final String REGISTER = "REGISTER";
    public static final String RESET = "RESET";
  }

  public static class ResponseCode {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    public static final int REQUEST_ERROR = 400;
    public static final int AUTH_EXPIRED = 401;
    public static final int DATA_ERROR = 402;
    public static final int NO_PERMISSION = 403;
    public static final int NOTFOUND = 404;
  }
}
