package Setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Base.Base;

public class Setting implements Base{
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	public Setting() {
		
		try {
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost/?serverTimezone=UTC&allowLoadLocalInfile=true", "root", "1234");
			Statement s = c.createStatement();
			
			s.executeUpdate("drop database if exists busticketbooking");
			s.executeUpdate("create database if not exists busticketbooking");
			s.executeUpdate("use busticketbooking");
			s.executeUpdate("create table user(no int primary key auto_increment, id varchar(50) not null, pwd varchar(50) not null, name varchar(50) not null, email varchar(50) not null, point int not null)");
			s.executeUpdate("create table location(no int primary key auto_increment, name varchar(50) not null)");
			s.executeUpdate("create table location2(no int primary key auto_increment, name varchar(50) not null, location_no int not null, foreign key(location_no) references location(no))");
			s.executeUpdate("create table schedule(no int primary key auto_increment, departure_location2_no int not null, arrival_locaion2_no int not null, date datetime not null, elapsed_time time not null, foreign key(departure_location2_no) references location2(no), foreign key(arrival_locaion2_no) references location2(no))");
			s.executeUpdate("create table reservation(no int primary key auto_increment, user_no int not null, schedule_no int not null, foreign key(user_no) references user(no), foreign key(schedule_no) references schedule(no))");
			s.executeUpdate("create table recommend(no int primary key auto_increment, location_no int not null, foreign key(location_no) references location(no))");
			s.executeUpdate("create table recommend_info(recommend_no int not null, title varchar(50) not null, descrption varchar(1000), img longblob not null)");
			s.executeUpdate("drop user if exists user@'localhost'");
			s.executeUpdate("create user if not exists user@'localhost' identified by '1234'");
			s.executeUpdate("grant select, update, insert, delete on busticketbooking.* to user@'localhost'");
			s.executeUpdate("set global local_infile = 1");
			
			String st[] = "user, location, location2, schedule, reservation, recommend, recommend_info".split(", ");
			for (int i = 0; i < st.length; i++) {
				s.executeUpdate("load data local infile '지급파일/" + st[i] + ".csv' into table " + st[i] + " fields terminated by ',' lines terminated by '\r\n' ignore 1 lines;");
			}
			
			Query("select * from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and l.no = r.location_no;", list);
			
			for (int i = 0; i < list.size(); i++) {
				
				String file = list.get(i).get(7).contentEquals("부산") ? "busan" : list.get(i).get(7).contentEquals("강원도") ? "gangwondo" : list.get(i).get(7).contentEquals("광주") ? "gyeongju" : list.get(i).get(7).contentEquals("서울") ? "seoul" : "jeollanam-do";
				String path = file("recommend/" + file + "/" + list.get(i).get(1) + ".jpg");
				
				SaveImg(path, list.get(i).get(0), list.get(i).get(1));
				
			}
			
			JOptionPane.showMessageDialog(null, "세팅 완료");
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	public static void main(String[] args) {
		new Setting();
	}

	@Override
	public void design() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}

}
