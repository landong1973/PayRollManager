package co.uk.humao.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.uk.humao.excelextraction.PayRollDistributor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

	 private Logger log = LogManager.getLogger(UploadController.class.getName());
	 
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = new File("./").getAbsolutePath()+"/../upload/";

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

    	File f = new File("./");
    	System.out.print("current dir="+f.getAbsolutePath());
    	
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            
            PayRollDistributor dist = new PayRollDistributor();
            
            dist.distributeMsg(path);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully send message via WeChat '" + file.getOriginalFilename() + "'");

        }  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 redirectAttributes.addFlashAttribute("message",
	                    "error: '" + e.getMessage()+ "'");
		}

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}