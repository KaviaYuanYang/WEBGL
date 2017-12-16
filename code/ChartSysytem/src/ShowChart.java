import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class ShowChart extends JFrame {

    //保存展示的数据
    private List<Integer> values;
    //保存展示的处理数据
    private List<Integer> processValues;
    public static String filePath="E:\\abc.txt";
    //声明私有变量读  readBFile  读取二进制文件
    private static ReadBFile readBFile = new ReadBFile() ;

    //声明私有变量读  processBData  读数据进行处理
    private static ProcessBData processBData = new ProcessBData(readBFile.getbData(),1);


    // 所展示数目的个数
    private static final int MAX_COUNT_OF_VALUES = 50;

    // 框架起点坐标
    private final int FREAME_X = 50;
    private final int FREAME_Y = 50;
    private final int FREAME_WIDTH = 600;// 横
    private final int FREAME_HEIGHT = 250;// 纵

    // 原点坐标
    private  int Origin_X = FREAME_X + 50;
    private  int Origin_Y = FREAME_Y + 110;

    // X,Y轴终点坐标
    private  int XAxis_X = FREAME_X + FREAME_WIDTH - 30;
    private  int XAxis_Y = Origin_Y;
    private  int YAxis_X = Origin_X;
    private  int YAxis_Y = FREAME_Y + 30;
    private  int YAxis_NY = FREAME_Y + 190;

    //frame 内部组件
    private MyCanvas trendChartCanvas = new MyCanvas();
    private JButton buttonToINtegral = new JButton("INtegral");
    private JButton buttonToDiffer =new JButton("DSiffer");
    private JButton buttonToFile = new JButton ("选择文件进行处理");
    private int INTEGRAL_DIFFER =1;

    private  MouseEventListener mouseEventListener =new MouseEventListener();


    // X轴上的时间分度值（1分度=40像素）
    private final int TIME_INTERVAL = 50;
    // Y轴上值
    private final int PRESS_INTERVAL = 10;

   //目前处理的数据的索引
    public  static int currData =0;

    //鼠标事件 控制 坐标轴和 数据 上下浮动的参数
    private int  UP_DOWN_Rate =800;
    private int  UP_DOWN_Dis = 0;

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        //以二级制形式读取文件
        readBFile.readFile(filePath);
        //构造ShowChart
         ShowChart showchart =  new ShowChart();
    }

    public ShowChart() {
        super("数据二进制显示：");
        values = Collections.synchronizedList(new ArrayList<Integer>());// 防止引起线程异常
        processValues = Collections.synchronizedList(new ArrayList<Integer>());// 防止引起线程异常

        // 线程对 values 和 processValues 赋值
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        //数据展示完毕后 ，跳出循环
                        if(readBFile.getbData().size()==currData)
                        {
                            repaint();
                            break;
                        }
                        //判断 展示的数据是微分还是积分
                        if(0==INTEGRAL_DIFFER) {

                            addValue(readBFile.getbData().get(currData), processBData.getIntegralValus().get(currData));
                        }
                        else {

                            addValue(readBFile.getbData().get(currData),processBData.getDifferValues().get(currData));
                        }
                        ++currData;
                        repaint();
                        Thread.sleep(100);
                    }
                } catch (InterruptedException b) {
                    b.printStackTrace();
                }
            }

        }).start();


        //设置Frame属性
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(100, 50, 1000, 700);
        this.add(trendChartCanvas, BorderLayout.CENTER);

        //添加两个BUTTON
        this.add(buttonToDiffer,BorderLayout.LINE_START);
        this.add(buttonToINtegral,BorderLayout.LINE_END);
         this.add(buttonToFile,BorderLayout.NORTH);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        buttonToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int index =fileChooser.showOpenDialog(null);
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setAcceptAllFileFilterUsed(false);
                filePath = fileChooser.getSelectedFile()
                        .getAbsolutePath();

                readBFile.readFile(filePath);
                ShowChart showchart =  new ShowChart();

            }
        });


        //Button 添加事件监听器
        this.addMouseListener(mouseEventListener);
        buttonToINtegral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                INTEGRAL_DIFFER=0;
                changerToIntgral();
                repaint();
            }
        });

        buttonToDiffer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                INTEGRAL_DIFFER =1;
                changerToDiffer();
                repaint();
            }
        });
        this.setVisible(true);
    }

    //在循环线程中对数组赋值的函数
    public void addValue(int value,int integralValue) {
        // 循环的使用一个接受数据的空间
        if (values.size() > MAX_COUNT_OF_VALUES) {
            values.remove(0);
        }

        if (processValues.size() > MAX_COUNT_OF_VALUES){
            processValues.remove(0);
        }
        values.add(value);
        processValues.add(integralValue);
    }

    public void changerToDiffer(){
        int tempCurr = currData;
        for(int i=processValues.size()-1;i>0;i--){
            if(0==tempCurr) {
                break;
            }

            processValues.set(i,processBData.getDifferValues().get(tempCurr--));

        }
    }

    //展示积分
    public void changerToIntgral(){
        int tempCurr = currData;
        for(int i=processValues.size()-1;i>0;i--){
            if(0==tempCurr) {
                break;
            }

            processValues.set(i,processBData.getIntegralValus().get(tempCurr--));

        }
    }


    // 画布重绘图
    class MyCanvas extends JPanel {

        public void paintComponent(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;

            super.paintComponent(g);
                //绘制第一个坐标轴展示二进制数据
                drawCoodinate(g2D, Origin_X, Origin_Y, XAxis_X, XAxis_Y ,
                        YAxis_X, YAxis_Y-UP_DOWN_Dis,  YAxis_NY+UP_DOWN_Dis);
                g.drawString("32757", YAxis_X - 35, YAxis_Y -UP_DOWN_Dis);// 血压刻度小箭头值
                g.drawString("-32758", YAxis_X - 37, YAxis_NY+15+UP_DOWN_Dis );// 血压刻度小箭头值
                //画二进制数据的折线图
                drawDateLine(g2D, Origin_X, Origin_Y, XAxis_X, XAxis_Y,
                        YAxis_X, YAxis_Y+UP_DOWN_Dis, YAxis_NY+UP_DOWN_Dis, values,UP_DOWN_Rate);

            //绘制第二个坐标轴展示数据的微分或积分
                drawCoodinate(g2D, Origin_X, Origin_Y+290, XAxis_X, XAxis_Y+290 ,
                        YAxis_X, YAxis_Y+290-UP_DOWN_Dis,  YAxis_NY+290+UP_DOWN_Dis);
                drawDateLine(g2D,Origin_X, Origin_Y+290, XAxis_X, XAxis_Y+290 ,
                        YAxis_X, YAxis_Y+290-UP_DOWN_Dis,  YAxis_NY+290+UP_DOWN_Dis,processValues,UP_DOWN_Rate);


        }

        public void drawDateLine(Graphics2D g,int Origin_X,int Origin_Y,int XAxis_X,int XAxis_Y ,
                                 int YAxis_X,int YAxis_Y, int YAxis_NY,List<Integer> values,int rate){
            Graphics2D g2D = (Graphics2D) g;

            // 绘制平滑点的曲线
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = XAxis_X;// 起始点
            //展示的数据数目是  50
            int xDelta = w / MAX_COUNT_OF_VALUES;
            int length = values.size() - 10;
            //画折线图：
            for (int i = 0; i < length - 1; ++i) {
                g2D.drawLine(xDelta * (MAX_COUNT_OF_VALUES - length + i), Origin_Y-values.get(i)/rate,
                        xDelta * (MAX_COUNT_OF_VALUES - length + i + 1), Origin_Y-values.get(i + 1)/rate);
            }
        }


        public void drawCoodinate(Graphics2D g,int Origin_X,int Origin_Y,int XAxis_X,int XAxis_Y ,
                                  int YAxis_X,int YAxis_Y, int YAxis_NY){
            Graphics2D g2D = (Graphics2D) g;
            Color c = new Color(200, 70, 0);
            g.setColor(c);
            // 画坐标轴
            g2D.setStroke(new BasicStroke(Float.parseFloat("2.0F")));// 轴线粗度
            // X轴以及方向箭头
            g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);// x轴线的轴线
            g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y + 5);// 上边箭头
            g.drawLine(XAxis_X, XAxis_Y, XAxis_X -5, XAxis_Y - 5);// 下边箭头

            // Y轴以及方向箭头
            g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
            g.drawLine(YAxis_X, YAxis_Y, YAxis_X - 5, YAxis_Y + 5);
            g.drawLine(YAxis_X, YAxis_Y, YAxis_X + 5, YAxis_Y + 5);

            //Y轴负半轴及及箭头
            g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_NY);
            g.drawLine(YAxis_X, YAxis_NY, YAxis_X -5, YAxis_NY -5);
            g.drawLine(YAxis_X, YAxis_NY, YAxis_X + 5, YAxis_NY -5);

            // 画X轴上的时间刻度（从坐标轴原点起，每隔TIME_INTERVAL(时间分度)像素画一时间点，到X轴终点止）
            //设置线条的特征情况
            g.setColor(Color.BLUE);
            g2D.setStroke(new BasicStroke(Float.parseFloat("1.0f")));

            g.drawString("幅度/Amplitude", YAxis_X - 5, YAxis_Y - 15);// 血压刻度小箭头值

        }
    }


    class MouseEventListener implements MouseInputListener {

        Point originPoint= new Point();
        Point draggedPoint =new Point();

        @Override
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            originPoint.x = e.getX();
            originPoint.y = e.getY();

        }


        public void mouseReleased(MouseEvent e) {
            draggedPoint.x=e.getX();
            draggedPoint.y=e.getY();
            int moveYDistance = originPoint.y-draggedPoint.y;
            int moveXDistance =  originPoint.x-draggedPoint.x;

            if(-moveXDistance>20&& (Math.abs(moveYDistance)<200)){
                if(currData<20) {
                    currData = 0;
                }

                else {
                    currData = currData - 20;

                }
                    int tempCurr = currData;

                    moveData(tempCurr);
                    repaint();

            }

            if((moveXDistance>20)&& (Math.abs(moveYDistance)<200)) {

                if (currData + 20 > readBFile.getbData().size()) {
                    currData = readBFile.getbData().size() - 1;
                } else {
                    currData = currData + 20;
                }
                int tempCurr = currData;
                moveData(tempCurr);

            }



            if((originPoint.y<Origin_Y&&moveYDistance>20
                    &&Math.abs(moveXDistance)<10)
                    ||(originPoint.y>Origin_Y&&-moveYDistance>20
                    &&Math.abs(moveXDistance)<10)){
                        moveUp();

            }
            if((originPoint.y<Origin_Y&&-moveYDistance>20
                    &&Math.abs(moveXDistance)<10)
                    ||(originPoint.y>Origin_Y&&moveYDistance>20
                    &&Math.abs(moveXDistance)<10)){
                moveDown();

            }
            repaint();



        }



        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}

    }


    public void moveUp(){

        if(UP_DOWN_Dis>59)
        {
            UP_DOWN_Dis=60;
            UP_DOWN_Rate=300;
        }
        else {
            UP_DOWN_Dis = UP_DOWN_Dis + 30;
            UP_DOWN_Rate=UP_DOWN_Rate-250;
        }
        repaint();
    }

    public void moveDown(){

        if(UP_DOWN_Dis<1){
            UP_DOWN_Dis=0;
            UP_DOWN_Rate=800;
        }
        else{
            UP_DOWN_Dis=UP_DOWN_Dis-30;
            UP_DOWN_Rate=UP_DOWN_Rate+250;
        }
        repaint();
    }

    public  void moveData(int tempCurr){
        for (int i = values.size() - 1; i > 0; i--) {
            if (tempCurr < 1) {
                break;
            }
            values.set(i, readBFile.getbData().get(--tempCurr));


            if (0 == INTEGRAL_DIFFER) {
                processValues.set(i, processBData.getIntegralValus().get(tempCurr--));

            } else {
                processValues.set(i, processBData.getDifferValues().get(tempCurr--));
            }
        }
    }

}

