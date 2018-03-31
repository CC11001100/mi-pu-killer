package cc11001100.proxy.mipu.init;

import cc11001100.ocr.OcrUtil;
import cc11001100.ocr.clean.SingleColorFilterClean;

import java.io.File;

/**
 * 用来初始化验证码，生成验证码模板的
 *
 * @author CC11001100
 */
public class InitCaptcha {

	public static void main(String[] args) {

//		OcrUtil ocrUtil = new OcrUtil().setImageClean(new SingleColorFilterClean());
//		ocrUtil.init("https://proxy.mimvp.com/common/ygrcode.php", 10000, "tmp/");

		OcrUtil.genAndPrintDictionaryMap("tmp/char", "dictionaryMap", filename -> filename.substring(0, 1));


	}

}
