package com.thinking.machines.nafserver.tool;
import java.net.*;
import java.io.*;
public class TMClassLoader 
{
public URL[] getURLs()
{
URL[] urls=new URL[1];
try
{
String s="";
File file=new File("package.cfg");
if(!file.exists()) 
{
System.out.println("Main package doesn't exists");
System.exit(0);
}
RandomAccessFile raf=new RandomAccessFile(file,"rw");
s=raf.readLine();
System.out.println("Line is read by raf");
System.out.println(s);
urls[0]=new URL(s);
}catch(Exception e)
{
System.out.println("TMClassLoader Problem"+e);
}
return urls;
}
}
