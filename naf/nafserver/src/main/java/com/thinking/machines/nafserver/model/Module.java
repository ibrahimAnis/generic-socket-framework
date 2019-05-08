package com.thinking.machines.nafserver.model;
import java.util.*;
public class Module
{
Class serviceClass;
boolean isSessionAware;
boolean isApplicationAware;
LinkedList<Property> autoWiredProperties;
public Module(){}
public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setIsSessionAware(boolean isSessionAware)
{
this.isSessionAware=isSessionAware;
}
public boolean getIsSessionAware()
{
return this.isSessionAware;
}
public void setIsApplicationAware(boolean isApplicationAware)
{
this.isApplicationAware=isApplicationAware;
}
public boolean getIsApplicationAware()
{
return this.isApplicationAware;
}
public void setAutoWiredProperties(LinkedList<Property> autoWiredProperties)
{
this.autoWiredProperties=autoWiredProperties;
}
public LinkedList<Property> getAutoWiredProperties()
{
return this.autoWiredProperties;
}
}

