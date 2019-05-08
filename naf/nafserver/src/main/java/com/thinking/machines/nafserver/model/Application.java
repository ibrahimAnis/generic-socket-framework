// Server Side Application
package com.thinking.machines.nafserver.model;
import com.thinking.machines.nafcommon.*;
import java.util.*;
public class Application
{
private HashMap<String,Service> services;
private static HashMap<String,Vector<Object>> attributes;
private static Vector<String> users;
public Application()
{
this.services=new HashMap<>();
Application.attributes=new HashMap<String,Vector<Object>>();
Application.users=new Vector<>();
}
public static void setAttribute(String user,Object object) throws ApplicationException
{
Vector<Object> v=null;
for(Map.Entry m:attributes.entrySet())
{
v=(Vector<Object>)m.getValue();
v.add(object);
}
}
public static void connect(String user)
{
users.add(user);
attributes.put(user,new Vector<>());
}
public static void disconnect(String user)
{
users.remove(user);
if(users.size()==0) attributes.clear();
else attributes.remove(user);
}
public static Vector<String> getUsers() throws ApplicationException
{
if(users.size()==0) throw new ApplicationException("No users");
return users;
}
public static Object getAttribute(String user) throws ApplicationException   // Session Exception
{
if(attributes.size()==0) throw new ApplicationException("No messages");
Vector<Object> v=(Vector<Object>)attributes.get(user);
return v;
}
public void addService(String path,Service service) throws ApplicationException
{
if(services.containsKey(path)) throw new ApplicationException("Yet To Finished");
services.put(path,service);
}
public boolean containsService(String path)
{
return services.containsKey(path);
}
public Service getService(String path) throws ApplicationException
{
Service service=services.get(path);
if(service==null) throw new ApplicationException("Invalid Path:"+path);
return service;
}
public HashMap<String,Service> getServices() throws ApplicationException
{
if(this.services.size()==0) throw new ApplicationException("No Service");
return this.services;
}
}
