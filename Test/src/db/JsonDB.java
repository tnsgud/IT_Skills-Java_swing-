package db;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngineManager;

public class JsonDB {
	public JsonDB() throws Exception {
		var sem = new ScriptEngineManager();
		var engine = sem.getEngineByName("javascript");

		var json = Files.readString(Paths.get("./datafiles/test.json"), Charset.forName("UTF-8"));

		for (var map : (List<Map<String, Object>>) engine.eval("Java.asJSONCompatible(" + json + ")")) {
			System.out.println(map.keySet());
		}
	}

	public static void main(String[] args) throws Exception {
		new JsonDB();
	}
}
