package com.library;
import com.thinking.machines.nafserver.annotation.*;
@Path("/ServiceA")
public class ServiceA
{
@Path("/add")
public void add(int a,int b)
{
System.out.println(a+b);
}
}
