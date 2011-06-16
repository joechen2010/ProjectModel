package com.joe.utilities.common.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * 验证码识别程序
 * @author hadeslee
 */
public class ImgIdent {
    
    private BufferedImage bi;
    private static int[][][] model=new int[5][10][208];
    //静态初始化块
    static{
        initNumModel();
    }
    
    
    /**
     * Creates a new instance of ImageCode
     */
    public ImgIdent() {
        initNumModel();
    }
    

    public String getNumber(InputStream is){
        try{
            bi= ImageIO.read( is );
            final StringBuffer sb=new StringBuffer();
            for(int i=0;i<4;i++){
                int[] data=this.getData(i);
                sb.append(this.doCheck(data));
            }
            return sb.toString();
        } catch(Exception exe){
            exe.printStackTrace();
            return "";
        }
    }
    
    
    /**
     * 重载的方法,根据传进来的参数得到返回的字符串
     * @param bi
     * @return 结果
     */
    public String getNumber(BufferedImage bi){
        try{
            this.bi= bi;
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<4;i++){
                int[] data=this.getData(i);
                sb.append(this.doCheck(data));
            }
            //System.out.println(sb.toString());
            return sb.toString();
        } catch(Exception exe){
            exe.printStackTrace();
            return "";
        }
    }
    
    
    
    /**
     * 静态初始化方法,
     * 用于初始化字模
     */
    private static void initNumModel(){
        try{
            //System.out.println("初始化model");
            for(int i=0;i<10;i++){ 
                StreamTokenizer st=new StreamTokenizer(new InputStreamReader(ImgIdent.class.getResourceAsStream("/com/struts2/mycore/util/image.mod")));
                st.whitespaceChars('#','#');
                st.whitespaceChars(',',',');
                st.eolIsSignificant(false);
                out:while(true){
                    int token=st.nextToken();
                    if(token==StreamTokenizer.TT_WORD){
                        int who=0;
                        int index=0;
                        if(st.sval.equals("center")){
                            who=0;
                        }else if(st.sval.equals("left")){
                            who=1;
                        }else if(st.sval.equals("right")){
                            who=2;
                        }else if(st.sval.equals("up")){
                            who=3;
                        }else if(st.sval.equals("down")){
                            who=4;
                        }
                        while(st.nextToken()==StreamTokenizer.TT_NUMBER){
                            model[who][i][index++]=(int)st.nval;
                        }
                        st.pushBack();
                    }else if(token==StreamTokenizer.TT_EOF){
                        break out;
                    }
                }
            }
            
        } catch(Exception exe){
            exe.printStackTrace();
        }
        //System.out.println("初始化结束model");
    }
    
    
    
    //通过传进来的字符串得到BufferedImage对象
    private BufferedImage getBI(String url){
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    
    /**根据索引得到
     *某一块的图像转为数组
     *的文件
     */
    private int[] getData(int index){
        BufferedImage sub=bi.getSubimage(index*16,0,16,13);
        int iw=sub.getWidth();
        int ih=sub.getHeight();
        int[] demo=new int[iw*ih];
        for(int i=0;i<ih;i++){
            for(int j=0;j<iw;j++){
                demo[i*iw+j]=(sub.getRGB(j,i)==-1?0:1);
            }
        }
        return demo;
    }
    
    
    
    //根据传进来的数组,得到五个位置当中和差别最小的那个
    private int getMin(int who,int[] demo){
        int temp=208;
        for(int i=0;i<5;i++){
            int x=0;
            for(int j=0;j<demo.length;j++){
                x+=(model[i][who][j]==demo[j]?0:1);
            }
            if(x<temp){
                temp=x;
            }
        }
        //System.out.println("比对"+who+"最小值是"+temp);
        return temp;
    }
    
    
    
    
    //分析689或者0的方法,以免这几个数字混淆
    private int get689(int[] demo,int origin){
        boolean isLeft=false,isRight=false;
        int temp=-1;
        if((demo[75]==1&&demo[90]==1)||(demo[76]==1&&demo[91]==1)||
                (demo[58]==1&&demo[74]==1&&demo[90]==1)||(demo[59]==1&&demo[75]==1&&demo[91]==1)||
                (demo[60]==1&&demo[76]==1&&demo[92]==1)||(demo[28]==1&&demo[44]==1&&demo[60]==1)||
                (demo[27]==1&&demo[43]==1&&demo[59]==1)){
            isRight=true;
        }
        if((demo[131]==1&&demo[147]==1)||(demo[132]==1&&demo[148]==1)||(demo[133]==1&&demo[149]==1)){
            isLeft=true;
        }
        if(isLeft&&isRight){
            temp=8;
        }else if(isLeft){
            temp=6;
        }else if(isRight){
            temp=9;
        }else{
            temp=origin;
        }
        if(temp==8&&(!((demo[103]==1&&demo[104]==1&&demo[105]==1&&demo[106]==1)||
                (demo[87]==1&&demo[88]==1&&demo[89]==1&&demo[90]==1)||
                (demo[103]+demo[104]+demo[105]+demo[106]+demo[87]+demo[88]+
                demo[89]+demo[90]>3)))){
            return temp=0;
        }
        return temp;
    }
    
    
    
    
    //比较传入的数据,返回最接近的值
    private int doCheck(int[] demo){
        int number=-1;
        int temp=208;
        for(int i=0;i<10;i++){
            int x=this.getMin(i,demo);
            if(x<temp){
                temp=x;
                number=i;
            }
        }
        //System.out.println("===========================================");
        if(number==6||number==8||number==9){
            number=this.get689(demo,number);
        }
        return number;
    }
    
    
    
    public static void main(String[] args) {
		
    	ImgIdent i = new ImgIdent();
    	System.out.println(i.getNumber(i.getBI("http://p8876.zhidian3g.cn/admin/user/validateCode/")));
    	
	}
    
}
