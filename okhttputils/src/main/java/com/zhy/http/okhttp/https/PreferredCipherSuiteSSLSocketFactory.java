package com.zhy.http.okhttp.https;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by cinba on 16/4/7.
 * to override the cipherlist which sent to the server by Android when using HttpsURLConnection
 */
public class PreferredCipherSuiteSSLSocketFactory extends SSLSocketFactory {

    // private static final String PREFERRED_CIPHER_SUITE = "TLS_RSA_WITH_AES_128_CBC_SHA";

    private final SSLSocketFactory delegate;

    public PreferredCipherSuiteSSLSocketFactory(SSLSocketFactory delegate) {

        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return setupPreferredDefaultCipherSuites(this.delegate);
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return setupPreferredSupportedCipherSuites(this.delegate);
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException,
            UnknownHostException {

        Socket socket = this.delegate.createSocket(arg0, arg1);
        String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
        ((SSLSocket) socket).setEnabledCipherSuites(cipherSuites);
        return socket;
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {

        Socket socket = this.delegate.createSocket(arg0, arg1);
        String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
        ((SSLSocket) socket).setEnabledCipherSuites(cipherSuites);
        return socket;
    }

    @Override
    public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3)
            throws IOException {

        Socket socket = this.delegate.createSocket(arg0, arg1, arg2, arg3);
        String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
        ((SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

        return socket;
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
            throws IOException, UnknownHostException {

        Socket socket = this.delegate.createSocket(arg0, arg1, arg2, arg3);
        String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
        ((SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
                               int arg3) throws IOException {

        Socket socket = this.delegate.createSocket(arg0, arg1, arg2, arg3);
        String[] cipherSuites = setupPreferredDefaultCipherSuites(delegate);
        ((SSLSocket) socket).setEnabledCipherSuites(cipherSuites);

        return socket;
    }

    private static String[] setupPreferredDefaultCipherSuites(SSLSocketFactory sslSocketFactory) {

        List<String> suitesList = new LinkedList<String>();

        // 去除DHE的dellman的加密方式，该加密算法在6.0系统上如果后台jdk不是1.8 会出现bad-length的错误
        for (String s : sslSocketFactory.getDefaultCipherSuites()) {
            if (!s.contains("_DHE_")) {
                suitesList.add(s);
            }
        }

        return suitesList.toArray(new String[suitesList.size()]);
    }

    private static String[] setupPreferredSupportedCipherSuites(SSLSocketFactory sslSocketFactory) {

        List<String> suitesList = new LinkedList<String>();

        // 去除DHE的dellman的加密方式，该加密算法在6.0系统上如果后台jdk不是1.8 会出现bad-length的错误
        for (String s : sslSocketFactory.getSupportedCipherSuites()) {
            if (!s.contains("_DHE_")) {
                suitesList.add(s);
            }
        }

        return suitesList.toArray(new String[suitesList.size()]);
    }
}