package com.argo;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.utils.TokenUtil;

/**
 * Created by Yaming on 2014/10/31.
 */
public class TokenUtilTest {

    public static void main( String[] args ) throws Exception {
        new SiteConfig().afterPropertiesSet();
        String value = TokenUtil.createSignedValue("x-auth", "1");
        System.out.print(value);
    }

}
