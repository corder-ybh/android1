import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    		System.out.println("以行为单位，读取mysql-binlog文件并转换为insert语句：\n");
    		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
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
        			    //System.out.print("insert 临时语句： " + tempres + "\n");
        			    flag = 1;
        			}
    				        			
        			//System.out.print("flag=0时的临时语句，line" + line + ": " + tempString + "\n");
        			line++;
    			}else if(flag == 1) {
    				int n2 = tempString.indexOf('='); 
    				//System.out.println("是否有=号，若有那么就要截取："+n2);
    				if(n2 != -1) {        //如果包含等号，那么就要准备进行截取
    					//int n2 = tempString.indexOf('=');            //@=截取后面的内容
        			    String tempSub = tempString.substring(n2+1);
        			    //System.out.println("第一次截取出来的内容："+tempSub+"-- \n");
        			    int n3 = tempSub.indexOf(" /*");
        			    tempSub = tempSub.substring(0, n3);
        			    //System.out.println("第二次截取出来的内容："+tempSub +"--line:" +line + "----- \n");
        			    tempres = tempres + tempSub + ",";
    				}
        			if(tempString.contains("### DELETE FROM")) {
        				tempres = tempres + ");";
        				//System.out.print("得到的临时语句： " + tempres + "\n");
        				res[lineRs] = tempres;       //将结果赋值给res数组
        				//System.out.print("res中语句： " + res[lineRs] + " lineRs:" + lineRs + "\n");
        				lineRs++;
        				flag = 0;
        				//再次将临时结果语句进行初始化
        				tempres = "insert into ";                   //第一次遇到delete，初始化为insert into
        			    int n1 = tempString.indexOf('`');            //根据`截取出表名称
        			    tempres = tempres + tempString.substring(n1) + " values(";//截取之后的表的名称
        			    //System.out.print("insert 临时语句： " + tempres + "\n");
        			    flag = 1;
        			}
        			
        			line++;
    			}
    			if(lineRs>0){
    			    System.out.print("res最终语句： " + res[lineRs-1] + " lineRs:" + (lineRs-1) + "\n");
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
