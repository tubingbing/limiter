package com.tbb.util;


import com.jd.o2o.exception.ZKCacheException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ZKInitUtil {
    private static String ZK_FILE_PATH;
    private static final int CONNECTION_TIMEOUT = 500;
    private static final int READ_TIMEOUT = 500;

    public ZKInitUtil() {
    }

    public static String getUrlByIp(String ip, String port) {
        String[] ips = ip.split(",");
        String[] ports = port.split(",");
        if(ips.length != ports.length) {
            throw new IllegalArgumentException("ip and host don\'t match.");
        } else {
            StringBuilder builder = new StringBuilder();
            int i = 0;

            for(int len = ips.length; i < len; ++i) {
                builder.append(StringUtil.delBlank(ips[i]));
                if(StringUtil.isNotEmpty(ports[i])) {
                    builder.append(":").append(StringUtil.delBlank(ports[i]));
                }

                builder.append(",");
            }

            if(builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }

            return builder.toString();
        }
    }

    public static String getUrlByIndex(String url) {
        String result = "";
        BufferedReader in = null;
        if(url == null) {
            return "";
        } else {
            if(url.indexOf("http") < 0) {
                url = "http://" + url;
            }

            try {
                URLConnection e = (new URL(url)).openConnection();
                e.setConnectTimeout(500);
                e.setReadTimeout(500);
                e.connect();

                String line;
                for(in = new BufferedReader(new InputStreamReader(e.getInputStream())); (line = in.readLine()) != null; result = result + line) {
                    ;
                }
            } catch (Exception var12) {
                throw new ZKCacheException(var12);
            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (Exception var11) {
                }

            }

            return result;
        }
    }

    public static void writeUrlToFile(String str) {
        try {
            getFilePath();
            File ex = new File(ZK_FILE_PATH);
            if(!ex.exists()) {
                ex.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(ex, false);
            StringBuffer sb = new StringBuffer();
            sb.append(str);
            out.write(sb.toString().getBytes("utf-8"));
            out.close();
        } catch (Exception var4) {
        }

    }

    public static String readUrlFromFile() {
        StringBuffer sb = new StringBuffer();
        String tempstr = null;

        try {
            getFilePath();
            File ex = new File(ZK_FILE_PATH);
            if(!ex.exists()) {
                return null;
            }

            FileInputStream fis = new FileInputStream(ex);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            while((tempstr = br.readLine()) != null) {
                sb.append(tempstr);
            }
        } catch (IOException var5) {
        }

        return sb.toString();
    }

    public static void getFilePath() {
        if(ClassLoader.getSystemResource("") != null) {
            ZK_FILE_PATH = ClassLoader.getSystemResource("").getPath() + "zkfile.txt";
        }

        ZK_FILE_PATH = "./zkfile.txt";
    }

}
