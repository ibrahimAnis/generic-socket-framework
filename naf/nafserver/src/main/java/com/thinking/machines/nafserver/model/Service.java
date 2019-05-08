package com.thinking.machines.nafserver.model;
import java.lang.reflect.*;
public class Service
{
Module module;
Method method;
int numberOfParameters;
boolean isVoid;
boolean injectSession;
int[] sessionParametersIndexes;
boolean injectApplicaiton;
int[] applicationParametersIndexes;
public Service(){}
public void setModule(Module module)
{
this.module=module;
}
public Module getModule()
{
return this.module;
}
public void setMethod(Method method)
{
this.method=method;
}
public Method getMethod()
{
return this.method;
}
public void setNumberOfParameters(int numberOfParameters)
{
this.numberOfParameters=numberOfParameters;
}
public int getNumberOfParameters()
{
return this.numberOfParameters;
}
public void setIsVoid(boolean isVoid)
{
this.isVoid=isVoid;
}
public boolean getIsVoid()
{
return this.isVoid;
}
public void setInjectSession(boolean injectSession)
{
this.injectSession=injectSession;
}
public boolean getInjectSession()
{
return this.injectSession;
}
public void setSessionParametersIndexes(
int[] sessionParametersIndexes)
{
this.sessionParametersIndexes=sessionParametersIndexes;
}
public 
int[] getSessionParametersIndexes()
{
return this.sessionParametersIndexes;
}
public void setInjectApplication(boolean injectApplicaiton)
{
this.injectApplicaiton=injectApplicaiton;
}
public boolean getInjectApplicaiton()
{
return this.injectApplicaiton;
}
public void setApplicationParametersIndexes(int[] applicationParametersIndexes)
{
this.applicationParametersIndexes=applicationParametersIndexes;
}
public int[] getApplicationParametersIndexes()
{
return this.applicationParametersIndexes;
}
}

