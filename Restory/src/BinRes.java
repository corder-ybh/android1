import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    		System.out.println("����Ϊ��λ����ȡmysql-binlog�ļ���ת��Ϊinsert���");
    		reader = new BufferedReader(new FileReader(file));
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
        			    System.out.print(tempres);
        			    flag = 1;
        			}
    				        			
        			System.out.print("line" + line + ": " + tempString + "\n");
        			line++;
    			}
    			if(flag == 1) {
    				int n2 = tempString.indexOf('='); 
    				System.out.println(n2);
    				if(n2 != -1) {
    					//int n2 = tempString.indexOf('=');            //@=��ȡ���������
        			    String tempSub = tempString.substring(n2+1);
        			    System.out.println(tempSub+"1 \n");
        			    int n3 = tempSub.indexOf(' ');
        			    tempSub = tempSub.substring(0, n3);
        			    System.out.println(tempSub + "----- \n");
    				}
        			
        			System.out.print("line" + line + ": " + tempString + "\n");
        			line++;
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
