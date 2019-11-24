package com.thinking.machines.nafserver.tool;
import com.thinking.machines.nafserver.model.*;
import com.thinking.machines.nafserver.annotation.*;
import com.thinking.machines.nafcommon.*;
import java.text.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.regex.*;
public class ApplicationUtility
{
private static Application application=null;

private ApplicationUtility(){}
public static Application getApplication()
{
System.out.println("Get Application method chali");
if(application!=null) return application;
String mainPackage=null;
try
{
throw new RuntimeException();
}catch(RuntimeException re)
{
StackTraceElement e[]=re.getStackTrace();
String className=e[e.length-1].getClassName();
try
{
mainPackage=Class.forName(className).getPackage().getName();
}catch(ClassNotFoundException cnfe)
{
System.out.println("*****************Serious Problem**************");
System.exit(0);
}
}
////////////////////////////
HashMap<String,LinkedList<ModuleMistake>> duplicateServices=new HashMap<>();
HashMap<String,Service> services=new HashMap<>();
LinkedList<ModuleMistake> moduleMistakes=new LinkedList<>();
String packageToAnalyze=mainPackage;
try
{
//URLClassLoader ucl=(URLClassLoader)ClassLoader.getSystemClassLoader();
TMClassLoader tmClassLoader=new TMClassLoader();
URL urls[]=tmClassLoader.getURLs();
String classPathEntry;
ZipInputStream zis;
ZipEntry ze;
String zipEntryName;
String packageName;
String className;
int dotPosition;
String folderName;
File directory;
File files[];
String fileName;
for(URL u:urls)
{
classPathEntry=u.getFile();
if(classPathEntry.endsWith(".jar"))
{
// code to analyze jar file contents
zis=new ZipInputStream(u.openStream());
ze=zis.getNextEntry();
while(ze!=null)
{
zipEntryName=ze.getName();
if(zipEntryName.endsWith(".class"))
{
zipEntryName=zipEntryName.replaceAll("\\\\","\\.");
zipEntryName=zipEntryName.replaceAll("/","\\.");
dotPosition=zipEntryName.lastIndexOf(".",zipEntryName.length()-7);
if(dotPosition==-1)
{
packageName="";
className=zipEntryName;
}
else
{
packageName=zipEntryName.substring(0,dotPosition);
className=zipEntryName.substring(dotPosition+1);
}
System.out.println("ZipEntryName:"+zipEntryName);
System.out.println("Package Name:"+packageName);
System.out.println("Class Name:"+className);
if(packageName.startsWith(packageToAnalyze))
{
System.out.println(zipEntryName);
try
{
Class ccc=Class.forName(zipEntryName.substring(0,zipEntryName.length()-6));
moduleScanner(ccc,services,moduleMistakes,duplicateServices);
}catch(Throwable ee)
{
System.out.println(ee); // remove after testing
}
}
}
ze=zis.getNextEntry();
}
}
else
{
// code to analyze folder
System.out.println("Class Path Entry:"+classPathEntry);
folderName=classPathEntry+packageToAnalyze;
System.out.println("Folder Name:"+folderName);
if(File.separator.equals("\\\\"))
{
folderName=folderName.replaceAll("\\.","\\\\");
} else
{
folderName=folderName.replaceAll("\\.","/");
}
directory=new File(folderName);
if(directory.exists()==false) continue;
Stack<File> stack=new Stack<>();
stack.push(directory);
File fifi;
while(stack.size()>0)
{
fifi=stack.pop();
files=fifi.listFiles();
for(File file:files)
{
if(file.isDirectory())
{
stack.push(file);
continue;
}
if(file.getName().endsWith(".class"))
{
className=file.getName();
packageName=file.getAbsolutePath().substring(classPathEntry.length());
packageName=packageName.substring(0,packageName.length()-className.length()-1);
packageName=packageName.replaceAll("\\\\","\\.");
packageName=packageName.replaceAll("/","\\.");
System.out.println("Package : "+packageName);
System.out.println("Class name : "+className);
try
{
Class ccc=Class.forName(packageName+"."+className.substring(0,className.length()-6));
moduleScanner(ccc,services,moduleMistakes,duplicateServices);
}
catch(Throwable ee)
{
System.out.println(ee); // remove after testing
}
}
}
} // stack.size()>0 wala loop
}
}
}
catch(Exception e)
{
StackTraceElement tt[]=e.getStackTrace();
for(StackTraceElement t:tt)
{
System.out.println(t);
}
}

if(moduleMistakes.size()>0 || duplicateServices.size()>0 || services.size()==0)
{
try
{
SimpleDateFormat sdf=new SimpleDateFormat("dd_MM_yyyy__hh_mm_ss");
String fileName="error_"+sdf.format(new java.util.Date())+".err";
File file=new File(fileName);
if(file.exists()) file.delete();
RandomAccessFile raf=new RandomAccessFile(file,"rw");
if(moduleMistakes.size()==0 && duplicateServices.size()==0 && services.size()==0)
{
raf.writeBytes("No services defined");
raf.close();
System.out.println("Errors in application, see file ("+fileName+") for details.");
System.exit(0);
}
if(moduleMistakes.size()>0)
{
raf.writeBytes("Some module mistakes\r\n");
} 
if(duplicateServices.size()>0)
{
raf.writeBytes("Some service mistakes\r\n");
}
raf.close();
System.out.println("Errors in application, see file ("+fileName+") for details.");
System.exit(0);
}catch(IOException ioException)
{
System.out.println(ioException.getMessage());
System.exit(0);
}// create error pdf at current location
System.exit(0);
}
application=new Application();
try
{
// traverse through services and add each of these services individually to hashMap of application class
for(Map.Entry<String,Service> m:services.entrySet())
{
application.addService(m.getKey(),m.getValue());
}
//Printing all services for fun
Iterator it=services.entrySet().iterator();
while(it.hasNext())
{
Map.Entry pair=(Map.Entry)it.next();
Service s=(Service)pair.getValue();
System.out.println("Path : "+pair.getKey()+" Service : "+s.getMethod().getName());
}
}catch(ApplicationException a)
{
System.out.println(a);
}
return application;
}


private static void moduleScanner(Class ccc,HashMap<String,Service> services,LinkedList<ModuleMistake> moduleMistakes,HashMap<String,LinkedList<ModuleMistake>> duplicateServices)
{
Class sessionScopeClass=SessionScope.class;
Class applicationScopeClass=ApplicationScope.class;
int j;
Class parameterTypes[];
ArrayList<Integer> indicesList;
int indices[];
LinkedList<Property> autoWiredProperties;
Property property;
Field fields[];
Field field;
com.thinking.machines.nafserver.model.Module module=null;
boolean flag;
LinkedList<ModuleMistake> linkedList;
ModuleMistake mm;
ServiceMistake sm;
Service s;
Service service;
String servicePathString;
boolean isValidModulePath,isValidServicePath;
isValidModulePath=false;
isValidServicePath=false;
Class pathClass=Path.class;
Path modulePath=null;
String modulePathString=null;
String methodPathString=null;
ModuleMistake moduleMistake=null;
System.out.println("Analysing "+ccc.getName()+" class");
if(ccc.isAnnotationPresent(pathClass))
{
System.out.println("Annotation is present");
modulePath=(Path)ccc.getAnnotation(pathClass);
modulePathString=modulePath.value();
isValidModulePath=isValidPath(modulePathString);
if(isValidModulePath==false)
{
moduleMistake=new ModuleMistake(ccc.getName());
moduleMistake.addMistake("Invalid path :"+modulePathString);
}
if(Modifier.isPublic(ccc.getModifiers())==false)
{
if(moduleMistake==null) moduleMistake=new ModuleMistake(ccc.getName());
moduleMistake.addMistake("class is not declared as public");
}
}
Method methods[]=ccc.getDeclaredMethods();
Path methodPath=null;
ServiceMistake serviceMistake=null;
for(Method m:methods)
{
if(m.isAnnotationPresent(pathClass))
{
methodPath=(Path)m.getAnnotation(pathClass);
methodPathString=methodPath.value();
isValidServicePath=isValidPath(methodPathString);
if(isValidServicePath==false)
{
serviceMistake=new ServiceMistake(m.toString());
serviceMistake.addMistake("Invalid Path:"+methodPathString);
}
if(!Modifier.isPublic(m.getModifiers()))
{
if(serviceMistake==null) serviceMistake=new ServiceMistake(m.toString());
serviceMistake.addMistake("method not declared as public");
}
if(modulePath==null && methodPath!=null)
{
if(moduleMistake==null) moduleMistake=new ModuleMistake(m.toString());
moduleMistake.addMistake("No annotation is applied on class but,is applied on service");
}
if(isValidModulePath && isValidServicePath)
{
servicePathString=modulePathString+methodPathString;
if(services.containsKey(servicePathString)==false)
{
if(moduleMistake==null && serviceMistake==null)
{
if(duplicateServices.containsKey(servicePathString)==false)
{
service=new Service();
if(module==null)
{
module=new com.thinking.machines.nafserver.model.Module();
module.setServiceClass(ccc);
if(ccc.isAnnotationPresent(ApplicationAware.class))module.setIsApplicationAware(true);
if(ccc.isAnnotationPresent(SessionAware.class))module.setIsApplicationAware(true);
autoWiredProperties=new LinkedList<>();
fields=ccc.getDeclaredFields();
for(Field ff:fields)
{
if(ff.getAnnotation(AutoWired.class)!=null)
{
property=new Property();
property.setName(ff.getName());
property.setType(ff.getGenericType());
autoWiredProperties.add(property);
}
}
module.setAutoWiredProperties(autoWiredProperties);
}
service.setModule(module);
service.setMethod(m);
service.setNumberOfParameters(m.getParameterCount());
if(m.getReturnType().getSimpleName().equalsIgnoreCase("VOID"))
{
service.setIsVoid(true);
}
parameterTypes=m.getParameterTypes();
indicesList=new ArrayList<>();
for(j=0;j<parameterTypes.length;j++)
{
if(parameterTypes[j].equals(sessionScopeClass))
{
indicesList.add(j);
}
}
if(indicesList.size()>0)
{
service.setInjectSession(true);
indices=new int[indicesList.size()];
j=0;
for(Integer iii:indicesList)
{
indices[j]=iii;
j++;
}
service.setSessionParametersIndexes(indices); 
}
indicesList=new ArrayList<>();
for(j=0;j<parameterTypes.length;j++)
{
if(parameterTypes[j].equals(applicationScopeClass))
{
indicesList.add(j);
}
}
if(indicesList.size()>0)
{
service.setInjectApplication(true);
indices=new int[indicesList.size()];
j=0;
for(Integer iii:indicesList)
{
indices[j]=iii;
j++;
}
service.setApplicationParametersIndexes(indices);
}

services.put(servicePathString,service);
}
else
{
linkedList=(LinkedList<ModuleMistake>)duplicateServices.get(servicePathString);
flag=false;
for(ModuleMistake mmm:linkedList)
{
if(mmm.getClassName().equals(ccc.getName()))
{
sm=new ServiceMistake(m.toString());
mmm.addServiceMistake(sm);
flag=true;
break;
}
}
if(!flag)
{
mm=new ModuleMistake(ccc.getName());
sm=new ServiceMistake(m.toString());
mm.addServiceMistake(sm);
linkedList.add(mm);
}
}
}
}
else
{
s=services.remove(servicePathString);
mm=new ModuleMistake(s.getModule().getServiceClass().getName());
sm=new ServiceMistake(s.getMethod().toString());
mm.addServiceMistake(sm);
linkedList=new LinkedList<>();
linkedList.add(mm);
if(ccc.equals(s.getModule().getServiceClass()))
{
sm=new ServiceMistake(m.toString());
mm.addServiceMistake(sm);
}
else
{
mm=new ModuleMistake(ccc.getName());
sm=new ServiceMistake(m.toString());
mm.addServiceMistake(sm);
linkedList.add(mm);
}
duplicateServices.put(servicePathString,linkedList);
}
}
if(serviceMistake!=null)
{
if(moduleMistake==null) moduleMistake=new ModuleMistake(ccc.getName());
moduleMistake.addServiceMistake(serviceMistake);
}
}
}
if(moduleMistake!=null)
{
moduleMistakes.add(moduleMistake);
}
}

private static boolean isValidPath(String path)
{
/*if(path.endsWith("/"))
{
return false;
}
String splits[]=path.split("/");
if(!splits[0].equals(""))
{
return false;
}
for(int i=1;i<splits.length;i++)
{
//if(Pattern.matches("(\\D)((\\w+)([#,$,_,-,&,@,~])(\\w+))|(\\w+)",splits[i]))
if(!Pattern.matches("(\\D)(\\p{Alpha})(([-,_,@,$]?)(\\w+))*",splits[i]))return false;
}*/
return true;
}
}
