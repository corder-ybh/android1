import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ���������ڻָ�mysql-binlog�ļ�������Ϊinsert���
 * @author yangbohua
 *
 */
public class BinRes {
    public static void main(String[] args) throws IOException {
    	File file = new File("E:\\rst");
    	BufferedReader reader = null;
    	int flag = 0;
    	String tempres = null;
    	String[] res = new String[100];
    	String[] cl = new String[100];
    	
    	try {
    		System.out.println("����Ϊ��λ����ȡmysql-binlog�ļ���ת��Ϊinsert��䣺\n");
    		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
    		String tempString = null;
    		String tempCl = null;
    		int line = 1;
    		int lineRs = 0;
    		//һ�ζ���һ�У�֪������nullΪ�ļ�����
    		while ((tempString = reader.readLine())!= null) {
    			//����ǵ�һ����������delete
    			if(flag == 0){
    				if(tempString.contains("### DELETE FROM")) {
        			    tempres = "insert into ";                   //��һ������delete����ʼ��Ϊinsert into
        			    int n1 = tempString.indexOf('`');            //����`��ȡ��������
        			    tempres = tempres + tempString.substring(n1) + " values(";//��ȡ֮��ı������
        			    //System.out.print("insert ��ʱ��䣺 " + tempres + "\n");
        			    flag = 1;
        			}
    				        			
        			//System.out.print("flag=0ʱ����ʱ��䣬line" + line + ": " + tempString + "\n");
        			line++;
    			}else if(flag == 1) {
    				int n2 = tempString.indexOf('='); 
    				//System.out.println("�Ƿ���=�ţ�������ô��Ҫ��ȡ��"+n2);
    				if(n2 != -1) {        //��������Ⱥţ���ô��Ҫ׼�����н�ȡ
    					//int n2 = tempString.indexOf('=');            //@=��ȡ���������
        			    String tempSub = tempString.substring(n2+1);
        			    //System.out.println("��һ�ν�ȡ���������ݣ�"+tempSub+"-- \n");
        			    int n3 = tempSub.indexOf(" /*");
        			    tempSub = tempSub.substring(0, n3);
        			    //System.out.println("�ڶ��ν�ȡ���������ݣ�"+tempSub +"--line:" +line + "----- \n");
        			    tempres = tempres + tempSub + ",";
    				}
        			if(tempString.contains("### DELETE FROM")) {
        				tempres = tempres + ");";
        				//System.out.print("�õ�����ʱ��䣺 " + tempres + "\n");
        				res[lineRs] = tempres;       //�������ֵ��res����
        				//System.out.print("res����䣺 " + res[lineRs] + " lineRs:" + lineRs + "\n");
        				lineRs++;
        				flag = 0;
        				//�ٴν���ʱ��������г�ʼ��
        				tempres = "insert into ";                   //��һ������delete����ʼ��Ϊinsert into
        			    int n1 = tempString.indexOf('`');            //����`��ȡ��������
        			    tempres = tempres + tempString.substring(n1) + " values(";//��ȡ֮��ı������
        			    //System.out.print("insert ��ʱ��䣺 " + tempres + "\n");
        			    flag = 1;
        			}
        			
        			line++;
    			}
    			if(lineRs>0){
    			    System.out.print("res������䣺 " + res[lineRs-1] + " lineRs:" + (lineRs-1) + "\n");
    			}
    		}
    		reader.close();
    	} finally {
    		if (reader != null) {
    			try {
    				reader.close();
    			} catch (IOException e1) {
    				
    			}
    		}
    	}
    }
}
