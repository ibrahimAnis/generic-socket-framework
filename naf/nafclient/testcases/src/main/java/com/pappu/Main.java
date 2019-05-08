package com.pappu;
import com.thinking.machines.nafclient.*;
import com.thinking.machines.nafcommon.*;
public class Main
{
public static void main(String gg[])
{
try
{
TMNAFClient tmnaf=new TMNAFClient("localhost",5000);
tmnaf.process("/ServiceA/add",10,20);
}catch(ApplicationException ae)
{
System.out.println(ae);
}
}
}
