package chatconsole3;

import java.io.*;
import java.net.*;

public class Client3 {
public static void main(String[] agrs) throws Exception{ //ném ngoại lệ tùy chỉnh 
	
	Socket socket =new Socket ("localhost",5577);//mở socket gửi yêu cầu đến server ở cổng port 3333.
	
	//mở luồng outputstream và inputstream để gửi nhận dữ liệu với sever.
	BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
	
	OutputStream outputstream = socket.getOutputStream();
	PrintWriter printwrite =new PrintWriter(outputstream, true);
	
	InputStream inputstream=socket.getInputStream();
	BufferedReader receiveRead = new BufferedReader(new InputStreamReader(inputstream));
	
	System.out.println("Hi there!"); //Hệ thống sẽ in ra chuỗi"Hi there!" khi kết nối thành công
	
	String receiveMessage, sendMessage; //Chấp nhận dữ liệu dạng chuỗi.
	while(true) { //vòng lặp để client có thể gửi yêu cầu nhiều lần.
		
		sendMessage = reader.readLine();
		printwrite.println("Client: "+ sendMessage);
		printwrite.flush();
		
		if((receiveMessage = receiveRead.readLine()) != null) {
			System.out.println(receiveMessage);
		}

	}
	
	 
	} 

}
