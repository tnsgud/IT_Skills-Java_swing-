package 잡동사니;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class SpreadOperator {
	static Connection con;
	static PreparedStatement ps;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
					"root", "1234");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public SpreadOperator() {
		for (var s : Stream.of("a,b".split(","), "c,d".split(",")).flatMap(a->Stream.of(a)).toArray()) {
			System.out.println(s);
		}
	}

	public static void main(String[] args) {
		new SpreadOperator();
	}
}
