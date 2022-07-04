package co.uk.humao.excelextraction;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ApachePOIExcelReader {
	
	private String filename = "C:\\development\\workspace-2\\PayRollManager\\archive\\工资条条目.xls";

	public String getFilename() {
		return filename;
	}




	public void setFilename(String filename) {
		this.filename = filename;
	}




	public ApachePOIExcelReader(String filename) {
		super();
		this.filename = filename;
	}




	public List<List<String>> readExcelFile()
	    {
	    	
            FileInputStream file = null;
            HSSFWorkbook workbook = null;
            List<List<String>> table = new ArrayList<List<String>>();
 
	        try
	        {
	           file = new FileInputStream(new File(this.filename));
	 
	            //Create Workbook instance holding reference to .xlsx file
	           workbook = new HSSFWorkbook(file);
	 
	            //Get first/desired sheet from the workbook
	            HSSFSheet sheet = workbook.getSheetAt(0);
	 
	            //Iterate through each rows one by one
	            Iterator<Row> rowIterator = sheet.iterator();
	            while (rowIterator.hasNext()) 
	            {
		            List<String> rowlist = new ArrayList<String>();

	                Row row = rowIterator.next();
	                //For each row, iterate through all the columns
	                Iterator<Cell> cellIterator = row.cellIterator();
	                 
	                while (cellIterator.hasNext()) 
	                {
	                    Cell cell = cellIterator.next();
	                    String value = null ;
	                    switch (cell.getCellType()) 
	                    {
	                        case NUMERIC:
	                            double dv= cell.getNumericCellValue();
	                            value = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance( Locale.ENGLISH )).format(dv);
	                            
	                            break;
	                        case STRING:
	                            value = cell.getStringCellValue();
	                            break;
						default:
							break;
	                    }
	                    		
	                            
	                    rowlist.add(value);
	                   
	                }
	                table.add(rowlist);
	            }

	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        
	        finally {
	        	try {
	        	if (file!=null) {
	        		file.close();
	        	}
	        	
	        	if (workbook!=null) {
	        		workbook.close();
	        		
	        	}
	        	}catch(Exception e ) {
	        		e.printStackTrace();
	        	}
	        	
	        }
	        
	        return table;
	    }
	

	public HashMap<String,String> serializeTable(List<List<String>> table, String year, String month) {
		int rownumber= 0;
		HashMap<String,String> result = new HashMap<String,String>();
		List<String> header= new ArrayList<String> ();
		for (List<String> row: table) {
			String text = String.format("您%s年%s月的工资明细如下： \n",year,month);
			String name = "";
			String id = "";
			if (rownumber == 0) {
				for (String value : row) {
					header.add(value);
				}
				
			}else {
				for (int i=0;i<row.size();i++) {
					if (row.get(i)!=null && (!row.get(i).equals("0.00"))){
						text+= header.get(i)+"  "+row.get(i)+"\n";
						if (header.get(i).equals("职员代码")) {
							id = row.get(i);
						}
						
						if (header.get(i).equals("职员姓名")) {
							name = row.get(i);
						}
					}
					
				}
				
				result.put(id, name+" "+ text);
			}
			rownumber++;
		}
		
		
		return result;
		
		
	}
	
	public static void main (String[] args) {
		
		String filename = "C:\\development\\workspace-2\\PayRollManager\\archive\\工资条条目-2022-07.xls";
		Pattern p = Pattern.compile("\\\\[^\\\\]+-(\\d{4})-(\\d{2})\\.xls$");
		Matcher m = p.matcher(filename);
		String year = null;
		String month = null;
		if (m.find()) {
			year= m.group(1);
			month = m.group(2);
			
		}
		
		if (month != null && month != null) {
			ApachePOIExcelReader reader= new ApachePOIExcelReader(filename);
			List<List<String>> table =reader.readExcelFile();
			HashMap<String,String> texts = reader.serializeTable(table, year , month);
			
			Set<String> keys =texts.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()) {
				
				String key = it.next();
				String value = texts.get(key);
				System.out.println(key+"\n"+value);
			}
		}
		
		
	}

}
