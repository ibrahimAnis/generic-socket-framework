package com.thinking.machines.nafserver;
import com.thinking.machines.nafcommon.*;
import com.thinking.machines.nafserver.model.*;
import static com.thinking.machines.nafcommon.Protocol.*;
import com.thinking.machines.nafserver.annotation.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
public class RequestProcessor extends Thread
{
private Application application;
private Socket client;
public RequestProcessor(Socket client,Application application)
{
this.client=client;
this.application=application;
start();
}
public void run()
{
try
{
InputStream is;
OutputStream os;
byte[] requestLengthInBytes=new byte[4];
int requestLength;
int byteCount;
int bytesToRead;
int bytesToWrite;
byte ack[]=new byte[1];
ByteArrayOutputStream baos;
byte requestBytes[];
byte chunk[]=new byte[1024];
ByteArrayInputStream bais;
ObjectInputStream ois;
ObjectOutputStream oos;
byte responseBytes[];
byte responseLengthInBytes[]=new byte[4];
Request request;
Response response;
int responseLength;
int chunkSize;
//$
is=client.getInputStream();
byteCount=is.read(requestLengthInBytes);
requestLength=(requestLengthInBytes[0] & 0xFF)<<24 | (requestLengthInBytes[1] & 0xFF)<<16 | (requestLengthInBytes[2] & 0xFF)<<8 | (requestLengthInBytes[3] & 0xFF);
ack[0]=ACKNOWLEDGEMENT_CODE;
os=client.getOutputStream();
os.write(ack,0,1);
os.flush();
baos=new ByteArrayOutputStream();
bytesToRead=requestLength;
while(bytesToRead>0)
{
byteCount=is.read(chunk);
if(byteCount>0)
{
baos.write(chunk,0,byteCount);
}
bytesToRead-=byteCount;
}
ack[0]=ACKNOWLEDGEMENT_CODE;
os.write(ack,0,1);
os.flush();
requestBytes=baos.toByteArray();
bais=new ByteArrayInputStream(requestBytes);
ois=new ObjectInputStream(bais);
request=(Request)ois.readObject();
response=processHandler(request);
baos=new ByteArrayOutputStream();
oos=new ObjectOutputStream(baos);
oos.writeObject(response);
oos.flush();
responseBytes=baos.toByteArray();
responseLength=responseBytes.length;
responseLengthInBytes[0]=(byte)(responseLength>>24);
responseLengthInBytes[1]=(byte)(responseLength>>16);
responseLengthInBytes[2]=(byte)(responseLength>>8);
responseLengthInBytes[3]=(byte)responseLength;
os.write(responseLengthInBytes,0,4);
os.flush();
byteCount=is.read(ack);
if(ack[0]!=ACKNOWLEDGEMENT_CODE)throw new RuntimeException("(TMSERVER)Unable to receive acknowledgement 1");
bytesToWrite=responseLength;
chunkSize=1024;
int i=0;
while(bytesToWrite>0)
{
if(bytesToWrite<chunkSize)chunkSize=bytesToWrite;
os.write(responseBytes,i,chunkSize);
os.flush();
i+=chunkSize;
bytesToWrite-=chunkSize;
}
byteCount=is.read(ack);
if(ack[0]!=ACKNOWLEDGEMENT_CODE)throw new RuntimeException("(TMSERVER)Unable to receive acknowledgement 2");
client.close();
//$
}catch(IOException ioe)
{
System.out.println("IOException"+ioe);
Thread.currentThread().interrupt();
}
catch(Exception e)
{
System.out.println(e);
}
}
public Response processHandler(Request request)
{
String path=request.getPath();
Response response=new Response();
Object[] arguments=request.getArguments();
boolean isVoid;
Method serviceMethod=null;
try
{
Service service=application.getService(path);
response.setIsVoid(service.getIsVoid());
Class serviceClass=service.getModule().getServiceClass();
serviceMethod=service.getMethod();
System.out.println(serviceMethod.getName()+" is invoked");
if(Modifier.isStatic(serviceMethod.getModifiers())) response.setResult(serviceMethod.invoke(null,arguments));
else response.setResult(serviceMethod.invoke(serviceClass.newInstance(),arguments));
response.setArguments(arguments);
response.setIsSuccessful(true);
}catch(InvocationTargetException ite)
{
System.out.println("Invocation Target Exception raised");
response.setIsSuccessful(false);
response.setIsException(true);
response.setException(ite.toString());
//Throwable cause=ite.getCause();
//System.out.println("Method:"+serviceMethod+" Cause:"+cause.getMessage());
//ite.printStackTrace();
}
catch(Exception t)
{
System.out.println("Exception generated:"+t);
response.setIsSuccessful(false);
response.setIsException(true);
response.setException(t.toString());
}
catch(Throwable e)
{
System.out.println("Throwable error"+e);
response.setIsSuccessful(false);
response.setIsError(true);
response.setError(e.toString());
}
//System.out.println("Value of response after being processed in Application Utility Class is "+response.getResult());
return response;
}
}
