package org.agnitas.cms.utils.preview;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustedHttpsHandler extends sun.net.www.protocol.https.Handler {
    SSLSocketFactory trustedSslSocketFactory = null;
    HostnameVerifier kindHostnameVerifier = null;

    TrustedHttpsHandler() {
        trustedSslSocketFactory = createTrustedSocketFactory();
        kindHostnameVerifier = new PreviewHostnameVerifier();
    }

    public SSLSocketFactory getTrustedSslSocketFactory() {
        return trustedSslSocketFactory;
    }

    private SSLSocketFactory createTrustedSocketFactory() {
        try {
            final SSLContext context = SSLContext.getInstance("ssl");
            final X509TrustManager trustManager = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            context.init(null, new TrustManager[]{trustManager}, null);
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection connection = super.openConnection(url);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection urlConnection = (HttpsURLConnection) connection;
            HostnameVerifier hostnameVerifier = urlConnection.getHostnameVerifier();
            if (hostnameVerifier != kindHostnameVerifier) {
                urlConnection.setHostnameVerifier(kindHostnameVerifier);
            }
            if (urlConnection.getSSLSocketFactory() != trustedSslSocketFactory && trustedSslSocketFactory != null) {
                urlConnection.setSSLSocketFactory(trustedSslSocketFactory);
            }
        }
        return connection;
    }

    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        URLConnection connection = super.openConnection(url, proxy);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection urlConnection = (HttpsURLConnection) connection;
            HostnameVerifier hostnameVerifier = urlConnection.getHostnameVerifier();
            if (hostnameVerifier != kindHostnameVerifier) {
                urlConnection.setHostnameVerifier(kindHostnameVerifier);
            }
            if (trustedSslSocketFactory != null) {
                urlConnection.setSSLSocketFactory(trustedSslSocketFactory);
            }
        }
        return connection;
    }

    class PreviewHostnameVerifier implements HostnameVerifier {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
