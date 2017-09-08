import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 本工具用于恢复mysql-binlog文件中内容为insert语句
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
    		System.out.println("以行为单位，读取mysql-binlog文件并转换为insert语句");
    		reader = new BufferedReader(new FileReader(file));
    		String tempString = null;
    		String tempCl = null;
    		int line = 1;
    		int lineRs = 0;
    		//一次读入一行，知道读入null为文件结束
    		while ((tempString = reader.readLine())!= null) {
    			//如果是第一次遇到遇到delete
    			if(flag == 0){
    				if(tempString.contains("### DELETE FROM")) {
        			    tempres = "insert into ";                   //第一次遇到delete，初始化为insert into
        			    int n1 = tempString.indexOf('`');            //根据`截取出表名称
        			    tempres = tempres + tempString.substring(n1) + " values(";//截取之后的表的名称
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
    					//int n2 = tempString.indexOf('=');            //@=截取后面的内容
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
