package com.argo.core.component;

import com.argo.core.utils.TokenUtil;
import com.argo.core.web.session.SessionCookieHolder;
import com.github.cage.Cage;
import com.github.cage.token.RandomTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/12/23
 * Time: 14:51
 */
public class CaptchaComponent {

    protected static Logger logger = LoggerFactory.getLogger(CaptchaComponent.class);

    private static Cage cage = null;
    static {
        RandomTokenGenerator generator = new RandomTokenGenerator(new Random(), 4);
        cage = new Cage(null, null, null, null, Cage.DEFAULT_COMPRESS_RATIO, generator, null);
    }

    /**
     * 读取Cookie Token
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request){
        Cookie cookie = SessionCookieHolder.getCookie(request, "c");
        if (cookie != null){
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 校验验证码
     * @param request
     * @param token
     * @return
     */
    public static boolean verifyToken(HttpServletRequest request, String token){
        String token0 = getToken(request);
        if (token0 == null){
            logger.error("Can't Get Token From Cookie. {}", request.getHeader("User-Agent"));
            return false;
        }
        String token1 = TokenUtil.generate(token.trim().toLowerCase(), TokenUtil.getCookieSecretSalt());
        boolean flag = token1.equalsIgnoreCase(token0);
        if (!flag){
            logger.error("Token is not correct. expect {}, but got {} ({})", token0, token1, token);
        }
        return flag;
    }

    /**
     * Generates a captcha token and stores it in the session.
     *
     * @param response
     *            where to store the captcha.
     */
    public static String generateToken(HttpServletResponse response) {
        String token = cage.getTokenGenerator().next().toLowerCase();
        String token0 = TokenUtil.generate(token, TokenUtil.getCookieSecretSalt());
        if (logger.isDebugEnabled()){
            logger.debug("captcha token: {} -> {}", token0, token);
        }
        SessionCookieHolder.setCookie(response, "c", token0);
        return token;
    }

    public static void draw(String token, OutputStream outputStream) throws IOException {
        cage.draw(token, outputStream);
    }

    public static String getFormat(){
        return cage.getFormat();
    }
}
