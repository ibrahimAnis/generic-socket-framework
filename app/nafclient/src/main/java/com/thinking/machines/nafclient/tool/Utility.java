package com.thinking.machines.nafclient.tool;
import java.util.*;
import java.lang.reflect.*;
class Pair<T1,T2>
{
public T1 first;
public T2 second;
Pair(T1 first,T2 second)
{
this.first=first;
this.second=second;
}
}
public class Utility
{
private static Set<String> primitiveDataTypes;
static
{
primitiveDataTypes=new HashSet<String>();
primitiveDataTypes.add("long");
primitiveDataTypes.add("int");
primitiveDataTypes.add("short");
primitiveDataTypes.add("byte");
primitiveDataTypes.add("char");
primitiveDataTypes.add("boolean");
primitiveDataTypes.add("float");
primitiveDataTypes.add("double");
primitiveDataTypes.add("java.lang.Long");
primitiveDataTypes.add("java.lang.Integer");
primitiveDataTypes.add("java.lang.Short");
primitiveDataTypes.add("java.lang.Byte");
primitiveDataTypes.add("java.lang.Character");
primitiveDataTypes.add("java.lang.Float");
primitiveDataTypes.add("java.lang.Double");
primitiveDataTypes.add("java.lang.Boolean");
}
public static void copyObject(Object target,Object source)
{
Class sourceClass=null;
Class targetClass=null;
try
{
sourceClass=source.getClass();
targetClass=target.getClass();
}catch(NullPointerException npe)
{
if(source==null && target==null)  System.out.println("both are null");
else if(source==null) System.out.println("source is null");
else if(target==null) System.out.println("target is null");
System.out.println("null pointer exception:"+npe);
}
if(sourceClass.isArray())
{
int i=0;
Object[] src=(Object[])source;
Object[] trg=(Object[])target;
for(Object o:src)
{
trg[i]=o;
i++;
}
return;
}
Method targetMethods[];
targetMethods=targetClass.getMethods();
Method sourceMethods[];
sourceMethods=sourceClass.getMethods();
LinkedList<Pair<Method,Method>> setterGetters=new LinkedList<Pair<Method,Method>>();
LinkedList<Method> sourceGetterMethods=new LinkedList<>();
String setterName,getterName;
Method getterMethod;
Class cls;
Object tmp2;
Object targetObject;
Object tmp1;
Class propertyType;
String propertyName;
Class componentType; // All the elements(component) of an array having same type known as component type
Object sourceObject;
Field targetField;
Class fieldType;
int sourceObjectLength;
for(Method method:sourceMethods)
{
if(isGetter(method))
{
System.out.println("Source getter method : "+method.getName());
sourceGetterMethods.add(method);
}
}
for(Method method:targetMethods)
{
if(!isSetter(method)) continue;
getterMethod=getGetterOf(method,sourceGetterMethods);
if(getterMethod!=null) setterGetters.add(new Pair(method,getterMethod));
}
// Information extraction about setter / getter complete
for(Pair<Method,Method> pair:setterGetters)
{
try
{
System.out.println(pair.first.getName()+"---->"+pair.second.getName());
propertyType=pair.second.getReturnType();
propertyName=pair.first.getName().substring(3).toLowerCase();
System.out.println("propertyName:"+propertyName);
System.out.println("propertyType:"+propertyType.getName());
pair.first.invoke(target,pair.second.invoke(source));
}catch(IllegalAccessException iae)
{

}
catch(InvocationTargetException ite)
{

}
catch(Throwable t)
{

}
}
}// ObjectCopier ends
public static Method getGetterOf(Method setterMethod,LinkedList<Method> getterMethods)
{
String setterPropertyName="";
String setterName=setterMethod.getName();
Class setterPropertyType;
Class getterPropertyType;
if(setterName.length()>3) setterPropertyName=setterName.substring(3);
String getterName;
setterPropertyType=setterMethod.getParameterTypes()[0];
String getterPropertyName;
for(Method method:getterMethods)
{
getterPropertyName="";
getterName=method.getName();
if(getterName.length()>3) getterPropertyName=getterName.substring(3);
getterPropertyType=method.getReturnType();
if(setterPropertyName.equals(getterPropertyName) && setterPropertyType.equals(getterPropertyType))
{
return method;
}
}
return null;
}
public boolean isPrimitive(Class type)
{
return primitiveDataTypes.contains(type.getName());
}
public static boolean isSetter(Method method)
{
return method.getName().startsWith("set") && method.getParameterCount()==1;
}
public static boolean isGetter(Method method)
{
if(method.getName().startsWith("get")==false) return false;
if(method.getReturnType().getName().toUpperCase().equals("VOID")) return false;
if(method.getParameterCount()>0) return false;
return true;
}
}


// DEEP COPY IMPLEMENTATION IS NOT REQUIRED BECAUSE WHEN WE RECEIVE THE RESPONSE OBJECT THROUGH NETWORK A NEW OBJECT WILL BE CREATED ON OUR MACHINE.SO WE DO NOT NEED TO BOTHER FOR COMPLEX OBJECTS.WE CAN DIRECTLY ASSIGN ADDRESSES TO OUR POINTER.JVM WILL TAKE CARE OF IT.






