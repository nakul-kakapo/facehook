package hookfb;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

public class ButtonsRequest {

	public String getButtonsJson() {
		String json = "";
		try (FileInputStream inputStream = new FileInputStream(
				"C:/<yourpath>/ButtonsTemplateExample.json")) {

			json = IOUtils.toString(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
}
