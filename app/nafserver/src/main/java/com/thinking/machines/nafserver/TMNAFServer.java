package com.thinking.machines.nafserver;
import com.thinking.machines.nafserver.model.*;
import com.thinking.machines.nafserver.tool.*;
import java.net.*;
public class TMNAFServer
{
private ServerSocket serverSocket;
private Application application;
public TMNAFServer()
{
initialize();
}
private void initialize()
{
application=ApplicationUtility.getApplication();
}
public void startServer()
{
startServer(5000);
}

public void startServer(int portNumber)
{
try
{
serverSocket=new ServerSocket(portNumber);
Socket ck;
while(true)
{
System.out.println("Server is ready and is listening on port "+portNumber);
ck=serverSocket.accept();
System.out.println("Request arrived");
new RequestProcessor(ck,application);
}
}catch(Exception e)
{
System.out.println("TMNAFServer Problem:"+e);
e.printStackTrace();
}
}
}
