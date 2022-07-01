package co.uk.humao.excelextraction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PayRollDistributor {
	
	
	private HashMap<String,String> userIDs = new HashMap<String,String>();
	
	public PayRollDistributor() {
		super();
		
		userIDs.put("03214", "XXXX");
		userIDs.put("03215", "XXXX");
		userIDs.put("A89654", "XXXX");
	}
	
	public String getUserID(String staffNumber) throws Exception {
		if (this.userIDs.containsKey(staffNumber))
		return(this.userIDs.get(staffNumber));
		else throw new Exception("unrecgnized staff number!");
	}


	public static void main(String[] args) {
		
		String filename = "C:\\development\\workspace-2\\excelextraction\\archive\\工资条条目.xls";
		ApachePOIExcelReader reader= new ApachePOIExcelReader(filename);
		PayRollDistributor exec = new PayRollDistributor();
		List<List<String>> table =reader.readExcelFile();
		HashMap<String,String> texts = reader.serializeTable(table);
		
		Set<String> keys =texts.keySet();
		Iterator<String> it = keys.iterator();
		WeChatClient client = new WeChatClient();

		while(it.hasNext()) {
			
			String key = it.next();
			String value = texts.get(key);
			System.out.println(key+"\n"+value);
			String userId;
			try {
				userId = exec.getUserID(key);
				client.sendMessage(value, userId);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}

}
