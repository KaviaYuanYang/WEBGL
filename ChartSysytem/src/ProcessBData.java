import java.util.ArrayList;
import java.util.List;

public class ProcessBData {
    private  static List<Integer> bData;
    private  static List<Integer> integralValus = new ArrayList<Integer>();
    private  static List<Integer> differValues = new ArrayList<>();
    private static int deta =1;

    ProcessBData(List<Integer> bData,int deta){
        this.bData=bData;

        this.deta=deta;
    }

    public List<Integer> getIntegralValus() {
        integralValus.add(0,0);
        for(int i=0;i<bData.size()-1;i++){
            integralValus.add(i+1,(bData.get(i)+bData.get(i+1))*deta/2);
        }

        for(int i=0;i<bData.size()-1;i++){
            int tempData1= bData.get(i);
            int tempData2 = bData.get(i+1);
            integralValus.set(i+1,tempData1+tempData2);
        }
        return integralValus;
    }


    public  List<Integer> getDifferValues(){
        for(int i=0;i<bData.size()-1;i++){

            differValues.add(i,(bData.get(i+1)-bData.get(i))/deta);
        }
        return differValues;
    }
}
