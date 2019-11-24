package com.thinking.machines.nafserver.model;
import java.lang.reflect.*;
public class Property
{
String name;
Type type;
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
public void setType(Type type)
{
this.type=type;
}
public Type getType()
{
return this.type;
}
}

