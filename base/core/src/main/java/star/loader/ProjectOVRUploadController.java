package star.loader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.bea.wlxt.WLXT;

@Controller
public class ProjectOVRUploadController {
	
	static {
		
	}
	
    @RequestMapping(value="/uploadProjectOVR", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam("file") MultipartFile file) throws IOException, Exception{
        if (!file.isEmpty()) {
        	processMediterranean(file.getInputStream());
        	return "";
//        	
//        	String name = file.getOriginalFilename();
//        	
//            try {
//                byte[] bytes = file.getBytes();
//                BufferedOutputStream stream =
//                        new BufferedOutputStream(new FileOutputStream(new File(name)));
//                stream.write(bytes);
//                stream.close();
//                return "You successfully uploaded " + name + "!";
//            } catch (Exception e) {
//                return "You failed to upload " + name + " => " + e.getMessage();
//            }
        } else {
            return "You failed to upload the file because the file was empty.";
        }
    }	

	public void processMediterranean(InputStream in) throws Exception {
//		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "weblogic.xml.jaxp.RegistryDocumentBuilderFactory");
//		System.setProperty("javax.xml.parsers.SAXParserFactory", "weblogic.xml.jaxp.RegistrySAXParserFactory");
		
		WLXT wlxt = new WLXT();
		URL url = getClass().getResource("/mfl/mediterranean-registry.mfl");
		Document doc = wlxt.parse(url, in, null);
		
		ByteArrayOutputStream mflxml = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(mflxml);
		WLXT.printDOM(doc, out, 0, 2);
		
		System.out.write(mflxml.toByteArray());
	}
    
}
