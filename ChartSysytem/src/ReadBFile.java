import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;



//读取二进制问价，将数据存在数组中
public class ReadBFile {
    //文件路径
   // public  String fileName = "E:\\abc.txt";

    //声明数组存储
    private static  List<Integer >  bData = new ArrayList<Integer>() ;
    //声明静态


    public  void readFile(String fileName){
        File file = new File(fileName);
        if(file.exists()){
            try {
                //读取二进制文件
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream binaryData = new DataInputStream(inputStream);
                //short型数组保证以两个字节读取数据流

                short[] itemBuf = new short[1024];
                short  isEnd;
                int inputByte = 0;
                while(binaryData.read()!=-1) {
                    // System.out.println(itemBuf[inputByte]);
                    int fistByte = binaryData.read();
                    int nextByte = binaryData.read();
                    //保证16为读取二进制数据
                    if(0==(fistByte&128)) {
                        bData.add(fistByte * 256 + nextByte);
                    }
                    else{
                        bData.add(((fistByte -128)* 256 + nextByte)*(-1));
                    }
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                //close
            }
        }

    }

    public List<Integer> getbData() {
        return bData;
    }

    public void setbData(List<Integer> bData) {
        this.bData = bData;
    }

}
