package chatconsole3;

import java.io.*;
import java.net.*;

public class Server3 {

	public static void main(String[] args) throws Exception{//ném ngoại lệ tùy chỉnh 
		
		//Khởi tạo đối tượng Server socket cổng 3333.
		ServerSocket severskt = new ServerSocket(5577);
		Socket socket = severskt.accept(); /// Khởi tạo socket và chấp nhận kết nối từ đối tượng Socket Server.
		
		BufferedReader Reader = new BufferedReader(new InputStreamReader(System.in));// Tạo luồng đọc dữ liệu vào từ client
		
		OutputStream outputstream = socket.getOutputStream();// Tạo luồng in dữ liệu ra
		PrintWriter printwrite =new PrintWriter(outputstream,true);
		
		InputStream inputstream = socket.getInputStream();// Tạo bộ đệm đọc đọc dữ liệu
		BufferedReader receiveRead = new BufferedReader (new InputStreamReader(inputstream));
		
		String receiveMessage, sendMessage; //Chấp nhận dữ liệu dưới dạng chuỗi
		
		
		while (true) { //Tạo vòng lặp để chấp nhận nhiều kết nối
			
			if((receiveMessage = receiveRead.readLine()) !=null){
				
				System.out.println(receiveMessage);
				}
			
			sendMessage = Reader.readLine();
			printwrite.println("Server: "+sendMessage);
			printwrite.flush();
			
			}

}}
