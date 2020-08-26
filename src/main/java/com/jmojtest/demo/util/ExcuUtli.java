package com.jmojtest.demo.util;

import java.io.*;

public class ExcuUtli {
    public static String[] Exec(String FileID,String testcase) throws IOException,InterruptedException{
        Runtime rt = Runtime.getRuntime();//获得Runtime对象
//        String arr[] = {"CLASSPATH=D:\\warehouse","Path=C:\\Program Files\\Java\\jdk-12.0.2\\bin"};//执行exec时的环境变量
        long startTime = System.currentTimeMillis();    //获取开始时间
        long startMem=rt.freeMemory();
        Process pr = rt.exec("javac Main.java && java Main");
        //exec方法第一个参数是执行的命令，第二个参数是环境变量，第三个参数是工作目录

//        Process pr = rt.exec("cmd /c javac Main.java && java Main", arr, new File("D:\\warehouse"));
        //获取输出流并转换成缓冲区
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
        bout.write(testcase);//输出数据
        bout.close();//关闭流
        long endMem=rt.freeMemory();
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        System.out.println("程序消耗内存: "+String.valueOf((startMem- endMem)/1024)+"KB");
        /**
         * Input和Output都是针对当前调用Process的程序而言的，而不是针对Process！
         * 也就是说如果你要往Process进程中输入数据，那么你要调用Process的getOutputStream方法！
         * 相反，如果你要获取Process进程的输出数据，那么你要调用Process的getInputStream方法！
         * **/
        //SequenceInputStream是一个串联流，能够把两个流结合起来，通过该对象就可以将
        //getInputStream方法和getErrorStream方法获取到的流一起进行查看了，当然也可以单独操作
        SequenceInputStream sis = new SequenceInputStream(pr.getInputStream(), pr.getErrorStream());
        InputStreamReader inst = new InputStreamReader(sis, "GBK");//设置编码格式并转换为输入流
        BufferedReader br = new BufferedReader(inst);//输入流缓冲区

        String res = null;
        StringBuilder sb = new StringBuilder();
        while ((res = br.readLine()) != null) {//循环读取缓冲区中的数据
            sb.append(res);
        }
        br.close();
        pr.waitFor();
        pr.destroy();
        System.out.println("output:"+sb.toString());
        String[] output=new String[3];
        output[0]=sb.toString();//输出
        output[1]=String.valueOf(endTime - startTime);//消耗时间
        output[2]=String.valueOf((startMem- endMem)/1024);//消耗内存
        return output;//输出获取的数据
    }
}
