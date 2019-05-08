package com.thinking.machines.nafclient;
import com.thinking.machines.nafcommon.*;
import com.thinking.machines.nafclient.tool.*;
import java.net.*;
import java.io.*;
public class TMNAFClient
{
private String hostname;
private int portNumber;
public TMNAFClient(String hostname,int portNumber)
{
this.hostname=hostname;
this.portNumber=portNumber;
}
public Object process(Object... args) throws ApplicationException
{
String path=(String)args[0];
Request request=new Request();
request.setPath(path);
Object[] arguments=new Object[args.length-1];
for(int i=0;i<args.length-1;i++) arguments[i]=args[i+1];
request.setArguments(arguments);
Object result=null;
Socket socket=null;
try
{
socket=new Socket(hostname,portNumber);
//socket=new Socket("192.168.225.167",portNumber);
}catch(IOException io)
{
System.out.println(io);
throw new ApplicationException(io.getMessage());
}
try
{
ByteArrayOutputStream baos=new ByteArrayOutputStream();
ObjectOutputStream oos=new ObjectOutputStream(baos);
oos.writeObject(request);
oos.flush();
byte requestBytes[]=baos.toByteArray();
int requestSize=requestBytes.length;
byte requestSizeInBytes[]=new byte[4];
requestSizeInBytes[0]=(byte)(requestSize>>24);
requestSizeInBytes[0]=(byte)(requestSize >>24);
requestSizeInBytes[1]=(byte)(requestSize >>16);
requestSizeInBytes[2]=(byte)(requestSize >>8);
requestSizeInBytes[3]=(byte)requestSize;
OutputStream os=socket.getOutputStream();
os.write(requestSizeInBytes,0,4);
os.flush();
InputStream is=socket.getInputStream();
byte ack[]=new byte[1];
int byteCount=is.read(ack);
if(ack[0]!=79)throw new RuntimeException("Unable to receive acknowledgement 1");
int bytesToSend=requestSize;
int chunkSize=1024;
int i=0;
while(bytesToSend>0)
{
if(bytesToSend<chunkSize)chunkSize=bytesToSend;
os.write(requestBytes,i,chunkSize);
os.flush();
i=i+chunkSize;
bytesToSend-=chunkSize;
}
byteCount=is.read(ack);
if(ack[0]!=79)throw new RuntimeException("Unable to receive acknowledgement 2");
byte [] responseLengthInBytes=new byte[4];
byteCount=is.read(responseLengthInBytes);
int responseLength;
responseLength=(responseLengthInBytes[0] & 0xFF) << 24 | (responseLengthInBytes[1] & 0xFF) <<16 | (responseLengthInBytes[2] & 0xFF) << 8 | (responseLengthInBytes[3] & 0xFF);
ack[0]=79;
os.write(ack,0,1);
os.flush();
baos=new ByteArrayOutputStream();
byte chunk[]=new byte[1024];
int bytesToRead=responseLength;
while(bytesToRead>0)
{
byteCount=is.read(chunk);
if(byteCount>0)
{
baos.write(chunk,0,byteCount);
baos.flush();
}
bytesToRead-=byteCount;
}
os.write(ack,0,1);
os.flush();
byte responseBytes[]=baos.toByteArray();
ByteArrayInputStream bais=new ByteArrayInputStream(responseBytes);
ObjectInputStream ois=new ObjectInputStream(bais);
Response response=(Response)ois.readObject();
//socket.close();
if(response.getIsSuccessful())
{
Object[] responseArguments=response.getArguments();
int k=1;
Utility util=new Utility();
for(Object o:responseArguments)
{
if(util.isPrimitive(o.getClass()))
{
args[k]=o;
continue;
}
System.out.println(o.getClass());
util.copyObject(args[k],o);
k++;
}
socket.close();
if(response.getIsVoid()) return null;   // result is null
return response.getResult();
}
socket.close();
if(response.getIsException())
{
System.out.println("We got an exception in response");
throw new ApplicationException(response.getException());
}
if(response.getIsError())
{
throw new ApplicationException(response.getError());
}
}catch(IOException io)
{
//System.out.println("Throwable catches the exception");
//throw new ApplicationException(throwable.getMessage());
throw new ApplicationException(io.getMessage());
}
catch(ClassNotFoundException cnfe)
{
throw new ApplicationException(cnfe.getMessage());
}
return result;
}
}
