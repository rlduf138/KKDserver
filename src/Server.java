// Java Chatting Server


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame {
	private JPanel contentPane;
	private JTextField textField; // ����� PORT��ȣ �Է�
	private JButton Start; // ������ �����Ų ��ư
	JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���
	
	private ServerSocket socket; //��������
	private Socket soc; // ������� 
	private int Port; // ��Ʈ��ȣ
	private Vector vc = new Vector(); // ����� ����ڸ� ������ ����

	public static void main(String[] args)
	{	
			Server frame = new Server();
			frame.setVisible(true);			
	}

	public Server() {
		//UserSetting();
		init();
	}
	
	private void init() { // GUI�� �����ϴ� �޼ҵ�		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane js = new JScrollPane();				

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setBounds(0, 0, 264, 254);
		contentPane.add(js);
		js.setViewportView(textArea);

		textField = new JTextField("30000");
		textField.setBounds(98, 264, 154, 37);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(12, 264, 98, 37);
		contentPane.add(lblNewLabel);
		Start = new JButton("���� ����");
		
		Start.setBounds(0, 325, 264, 37);
		contentPane.add(Start);
		server_start();
		textArea.setEditable(false); // textArea�� ����ڰ� ���� ���ϰԲ� ���´�.	
	}
	
	
	private void server_start() {
		try {
			socket = new ServerSocket(30000); // ������ ��Ʈ ���ºκ�
			Start.setText("����������");
			Start.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
			textField.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
			
			if(socket!=null) // socket �� ���������� ��������
			{
				Connection();
			}
			
		} catch (IOException e) {
			textArea.append("������ �̹� ������Դϴ�...\n");

		}

	}

	private void Connection() {
		Thread th = new Thread(new Runnable() { // ����� ������ ���� ������
			@Override
			public void run() {
				while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
					try {
						textArea.append("����� ���� �����...\n");
						soc = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
						textArea.append("����� ����!!\n");
						
						// ���̵� ��й�ȣ üũ�ϱ�
						
						UserInfo user = new UserInfo(soc, vc); // ����� ���� ������ �ݹ� ������Ƿ�, user Ŭ���� ���·� ��ü ����
	                                // �Ű������� ���� ����� ���ϰ�, ���͸� ��Ƶд�
						vc.add(user); // �ش� ���Ϳ� ����� ��ü�� �߰�
						user.start(); // ���� ��ü�� ������ ����
					} catch (IOException e) {
						textArea.append("!!!! accept ���� �߻�... !!!!\n");
					} 
				}
			}
		});
		th.start();
	}

	class UserInfo extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		private Socket user_socket;
		private Vector user_vc;
		private String Nickname = "";
		private User[] users;
		
		public UserInfo(Socket soc, Vector vc) // �����ڸ޼ҵ�
		{
			// �Ű������� �Ѿ�� �ڷ� ����
			this.user_socket = soc;
			this.user_vc = vc;
			UserSetting();
			User_network();
		}
		private void UserSetting(){
			users = new User[4];
			users[0] = new User("user1","1234");
			users[1] = new User("user2","qwer");
			users[2] = new User("user3","asdf");
			users[3] = new User("user4","zxcv");
		}
		public void User_network() {
			int[] array = new int[4];	//�α��� ���� �迭 �ε���
		//	int array_count = 0;		//�α��� ���� ��
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				//Nickname = dis.readUTF(); // ������� �г��� �޴ºκ�
				byte[] b=new byte[128];
				dis.read(b);
				String msg = new String(b);
				msg = msg.trim();
				String temp = msg;
				System.out.println("msg = " + temp);
				
				StringTokenizer s = new StringTokenizer(temp);
				String Command = s.nextToken("$");
				//String Password = s.nextToken(",");
				String temp2;
				System.out.println(Command + Nickname);
				switch(Command){
				case "LOGIN":
					int j=-1;
					String Nickname = s.nextToken("$");
					
					for(int i = 0;i<4;i++){
						if(users[i].getId().equals(Nickname)){
							j = i;
							textArea.append(Protocol.LOGINSUCCESS + Nickname + "\n");
							textArea.setCaretPosition(textArea.getText().length());	
							temp2 = Protocol.LOGINSUCCESS;
							temp2 = temp2 + Nickname;
							//System.out.println(temp2);
							send_Message(temp2); // ����� ����ڿ��� ���������� �˸�
							//send_Message("Login Success");
							//user_socket.close();
						}
					}
					
					if(j == -2){
						textArea.append(Protocol.ALREADY_ACCESS+Nickname + "\n");
						textArea.setCaretPosition(textArea.getText().length());
						temp2 = Protocol.ALREADY_ACCESS;
						temp2 = temp2 + Nickname;
						send_Message(temp2);
						user_socket.close();
					}
					if(j == -1 ){
						//���� ����
					//	textArea.append("ID " + Nickname + " ID ����\n");
						textArea.append(Protocol.IDERROR+Nickname + "\n");
						textArea.setCaretPosition(textArea.getText().length());
						temp2 = Protocol.IDERROR;
						temp2 = temp2 + Nickname;
						send_Message(temp2);
						user_socket.close();
					}
					break;
				
				case "CHAT":
					broad_cast(msg);
					//ä��
					break;
					
				
				}
				
			} catch (Exception e) {
				textArea.append("��Ʈ�� ���� ����\n");
				textArea.setCaretPosition(textArea.getText().length());
			}
		}

		public void InMessage(String str) {
			//textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
			textArea.append(str + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			// ����� �޼��� ó��
			broad_cast(str);
		}

		public void broad_cast(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserInfo imsi = (UserInfo) user_vc.elementAt(i);
				imsi.send_Message(str);
			}
		}

		public void send_Message(String str) {
			try {
				//dos.writeUTF(str);
				byte[] bb;		
				bb = str.getBytes();
				dos.write(bb); //.writeUTF(str);
			} 
			catch (IOException e) {
				textArea.append("�޽��� �۽� ���� �߻�\n");	
				textArea.setCaretPosition(textArea.getText().length());
			}
		}

		public void run() // ������ ����
		{

			while (true) {
				try {
					
					// ����ڿ��� �޴� �޼���
					byte[] b = new byte[128];
					dis.read(b);
					String msg = new String(b);
					msg = msg.trim();
					//String msg = dis.readUTF();
					InMessage(msg);
					
				} 
				catch (IOException e) 
				{
					
					try {
						dos.close();
						dis.close();
						user_socket.close();
						vc.removeElement( this ); // �������� ���� ��ü�� ���Ϳ��� �����
						textArea.append(vc.size() +" : ���� ���Ϳ� ����� ����� ��\n");
						textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
						textArea.setCaretPosition(textArea.getText().length());
						
						break;
					
					} catch (Exception ee) {
					
					}// catch�� ��
				}// �ٱ� catch����

			}
			
			
			
		}// run�޼ҵ� ��

	} // ���� userinfoŬ������

}
