package hookfb;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ListRequest {

	public String getListJson() {
		String json = "";
		try (FileInputStream inputStream = new FileInputStream(
				"C:/2_OWN_TECH/Projects-Java/SANDBOX/src/preiss/facebookmessenger/ListTemplateExample.json")) {

			json = IOUtils.toString(inputStream);
			

		} catch (IOException e) {

			e.printStackTrace();
		}
		return json;
	}
}
