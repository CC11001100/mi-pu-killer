package cc11001100.proxy.mipu;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author CC11001100
 */
public class MiscTest {

	/**
	 * 测试写临时目录
	 */
	@Test
	public void test_001() throws IOException {

		String tempDirectory = System.getProperty("java.io.tmpdir");
		System.out.println(tempDirectory);

		String outputFilePath = tempDirectory + "foo.config";
		String fileContent = "foo bar config fooooooooo";
		FileUtils.writeStringToFile(new File(outputFilePath), fileContent, StandardCharsets.UTF_8);

	}

}
