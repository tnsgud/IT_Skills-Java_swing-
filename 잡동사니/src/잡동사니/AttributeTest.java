package 잡동사니;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;


public class AttributeTest {
	public AttributeTest() {
	      String attribute = "Bus";
	      String name = "type";
	      try {
	         Path path = FileSystems.getDefault().getPath("C:/images/1.jpg", "");
	         UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
//	         메타데이터 쓰기
//	         view.write("type", Charset.defaultCharset().encode(attribute));

	         // 메타데이터 읽
	         System.out.println(view.size(name));
	         
	         ByteBuffer buf = ByteBuffer.allocate(view.size(name));
	         view.read(name, buf);
	         buf.flip();
	         String value = Charset.defaultCharset().decode(buf).toString();
	         System.out.println(value);
	         
	         
	      } catch (Exception e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	   }
	
	public static void main(String[] args) {
		new AttributeTest();
	}
}
