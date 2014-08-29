package com.common.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;

/**
 * 证书工具类 <BR>
 * 
 * @author chengking
 */
public class CAUtil {
    
    private static String lineSeparator = System.getProperty("line.separator");
    
    public static final String PEM_START_LINE = "-----BEGIN CERTIFICATE-----\n";
    
    public static final String PEM_END_LINE = "\n-----END CERTIFICATE-----";

    public static String getCertsInfo(X509Certificate[] certs) {
        if (certs == null) {
            return "null!";
        }
        final int certCount = certs.length;
        StringBuffer sb = new StringBuffer(80 * certCount);
        sb.append('[').append(certCount).append(" certs]");
        for (int i = 0; i < certCount; i++) {
            sb.append(lineSeparator).append(getCertInfo(certs[i]));
        }
        return sb.toString();
    }

    public static String getCertInfo(X509Certificate cert) {
        if (cert == null) {
            return "null!";
        }
        StringBuffer sb = new StringBuffer(80);
        /*
        // ls@07.0703 JSSE 1.0.3, JDK 1.3 dosen't have getX500Principal() methods!
        sb.append("Issuer:").append(cert.getIssuerX500Principal().getName());
        sb.append(";Subject:").append(cert.getSubjectX500Principal().getName());
        */
        sb.append("Issuer:").append(cert.getIssuerDN().getName());
        sb.append(";Subject:").append(cert.getSubjectDN().getName());
        return sb.toString();
    }
    
    public static String getSubjectName(X509Certificate cert) {
        if (cert == null) {
            return "null!";
        }
        // ls@07.0701 see JIRA TRSIDS-183
        return cert.getSubjectDN().getName();
//        return cert.getSubjectX500Principal().getName();
    }
    
    /**
     * 返回X509证书的PEM�? 实现�?code>return PEM_START_LINE + getPureBase64Pem(cert) + PEM_END_LINE;</code>.
     * @throws CertificateEncodingException
     * @since ls@07.0629
     * @see #getPureBase64Pem(X509Certificate)
     */
    public static String getBase64Pem(X509Certificate cert) throws CertificateEncodingException {
    	return PEM_START_LINE + getPureBase64Pem(cert) + PEM_END_LINE;
    }
    
    /**
     * 返回X509证书的PEM, 不包括{@link #PEM_START_LINE}和{@link #PEM_END_LINE}.
     * @throws CertificateEncodingException
     * @since ls@07.0702
     */
    public static String getPureBase64Pem(X509Certificate cert) throws CertificateEncodingException {
    	if (cert == null) {
    		return null;
    	}
    	byte[] bsX509 = null;
    	try {
			bsX509 = cert.getEncoded();
		} catch (CertificateEncodingException e) {
			throw e;
		}
    	return Base64Util.encode(bsX509);
    }
    
    /**
     * 根据PEM串生成X509证书.
     * @throws CertificateException
     * @see #generateCertificate(byte[])
     */
    public static X509Certificate generateCertificate(String b64Pem) throws CertificateException {
    	if (b64Pem == null) {
    		throw new IllegalArgumentException("the base64 pem is null!");
    	}
    	
    	return generateCertificate(b64Pem.getBytes());
    }
    
    /**
     * 
     * @throws CertificateException
     * @since ls@07.0629 
     * @see #generateCertificate(InputStream)
     */
    public static X509Certificate generateCertificate(byte[] base64Pem) throws CertificateException {
    	InputStream inStream = new ByteArrayInputStream(base64Pem);
    	return generateCertificate(inStream);
    }

	/**
	 * 从输入流生成X509证书.
	 * @throws CertificateException
	 * @since ls@07.0629
	 */
	public static X509Certificate generateCertificate(InputStream inStream) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	Certificate xCert = null;
    	try {
			xCert = cf.generateCertificate(inStream);
		} catch (CertificateException e) {
			throw e;
		}
		
		if (xCert instanceof X509Certificate) {
			return (X509Certificate) xCert;
		} else {
			throw new CertificateException("Not X509! the ertificate is " + xCert);
		}
	}

	/**
	 * 解析证书DN形式的字符串中某�?��取�?的工具方�?
	 * @since ls@07.0701
	 */
	public static String parseDNPart(String dn, String key) {
		if (dn == null || key == null) {
			throw new IllegalArgumentException("(dn, key): " + dn + ", " + key + "!");
		}
		StringTokenizer st = new StringTokenizer(dn, ",");
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			if (s.startsWith(key + "=")) {
				return s.substring(key.length() + 1);
			}
		}
		return null;
	}
	
	/**
	 * 解析证书SubjectDN字符串中某一项的取�?.
	 * @since ls@07.0701
	 */
	public static String getValueOfSubjectDN(X509Certificate cert, String key) {
		if (cert == null) {
			return null;
		}
		
		String dn = cert.getSubjectDN().getName();
		return parseDNPart(dn, key);
	}

}