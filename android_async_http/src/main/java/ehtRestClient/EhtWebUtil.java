package ehtRestClient;

public class EhtWebUtil {

	 /**
     * �ͻ���������ǩ������
     * ǩ���㷨����MD5��secret+"appKey"+appKey+"timestamp"+timestamp+secret+token��,
     * ����ԭʼ�ַ�����secret+"app_key"+appKey+"timestamp"+timestamp+secret+token��
     * @param appKey  ������û���appKey
     * @param timestamp �ͻ���ʱ���
     * @param secret ������û�������
     */
    public static String sgin(String appKey,String timestamp,String secret,String token,String param){
       String signature = "";
       try {
            StringBuffer sbf = new StringBuffer();
            sbf.append(secret).append("appKey").append(appKey).append("timestamp")
                .append(timestamp).append(secret).append(token).append(param);
            signature  = Md5Encrypt.MD5(sbf.toString());
        } catch (Exception e) {
        	
        }
        return signature;
    }
}
