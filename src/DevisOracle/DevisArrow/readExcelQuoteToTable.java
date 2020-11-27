package DevisOracle.DevisArrow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DevisOracle.shared.KeyValue;
import DevisOracle.configuration.readProperties;
import DevisOracle.shared.QuoteStructure;
import DevisOracle.shared.Tools;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class readExcelQuoteToTable {
	static final Logger log = LogManager.getLogger(readExcelQuoteToTable.class.getName());

	// private static DecimalFormat df2 = new DecimalFormat("#.##");
	public Workbook workbook = null;
	private FileInputStream fis = null;
	private String designationDevis;
	private String filename;

	public readExcelQuoteToTable(String filename) {
		if (getWorkbook() == null) {
			readWorkbook(filename);
		}
	}

	public Workbook getWorkbook() {
		return workbook;
	}
	public String getDesignationDevis() {
		return designationDevis;
	}

	private void setWorkbook(Workbook wkb) {
		workbook = wkb;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getNumberSheets() {
		int numberOfSheets = getWorkbook().getNumberOfSheets();
		return numberOfSheets;
	}

	public List<KeyValue> getListWorkbookSheets() {
		List<KeyValue> sheets = new ArrayList<>();
		int numberOfSheets = getWorkbook().getNumberOfSheets();
		for(int i=0; i < numberOfSheets; i++){
			String sheetName = getWorkbook().getSheetName(i);
			KeyValue kv = new KeyValue(i, sheetName);
			sheets.add(kv);
		}
		return sheets;
	}

	private void readWorkbook(String fileName) {
		Workbook wkb = null;
		this.setFilename(fileName);
		try {
			//Create the input stream from the xlsx/xls file
			fis = new FileInputStream(fileName);
			//Create Workbook instance for xlsx/xls file input stream
			if (fileName.toLowerCase().endsWith("xlsx")) {
				wkb = new XSSFWorkbook(fis);
			} else if (fileName.toLowerCase().endsWith("xls")) {
				wkb = new HSSFWorkbook(fis);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("readExcelQuoteToTable - readWorkbook - error", e);
		}
		setWorkbook(wkb);
	}

	public void closeWorkbook() throws IOException {
		workbook.close();
		fis.close();
	}

	public void saveWorkboox() throws IOException {
		FileOutputStream out = new FileOutputStream(this.getFilename());
		workbook.write(out);
		out.close();
	}

	public QuoteStructure readSheetIndex(Integer sheetIndex) throws IOException {

		QuoteStructure quoteStructure = new QuoteStructure();
		List<ExcelQuoteRow> myQuote = new ArrayList<ExcelQuoteRow>();
		Sheet sheet = getWorkbook().getSheetAt(sheetIndex);
		quoteStructure.setSheetName(sheet.getSheetName());
		// get first and last row.
		int rowStart = Math.max(0, sheet.getFirstRowNum());
		int rowEnd = Math.max(50, sheet.getLastRowNum());
		System.out.println("rowstart : " + rowStart);
		// rowStart to start at 23. les devis Arrow contiennent des en-tetes.

		Integer start = 22;
		try	{
			String startProperty = readProperties.getInstance().getProperty("DevisArrow.premiereLigne");
			start = Integer.parseInt(startProperty);
		} catch (Exception e) {
			e.printStackTrace();
			start = 22;
			log.error("readExcelQuoteToTable - readSheetIndex - error", e);
		}

		rowStart = rowStart + start  ;
		//System.out.println("rowstart : " + rowStart + "   rowEnd : " + rowEnd);

		Integer section = 0;
		Boolean switchSection=false;

		// valeur par défaut de la ligne contenant la désignation
		String rowDesignationStr = readProperties.getInstance().getProperty("DevisArrow.designationDevis");
		Integer rowDesignationNumber = Integer.parseInt(rowDesignationStr.trim());
		Boolean isFirstLine = true;

		for (int rowNum = rowStart-1; rowNum < rowEnd; rowNum++) {
			//Get the row object
			System.out.println("");
			System.out.println("Row : "  + rowNum);
			Row row;
			try {
				row = sheet.getRow(rowNum);
				// System.out.println("Row : " + rowNum + row.toString());
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			// if row empty
			if (row == null) {
				System.out.println("ReadExcelQuoteTo Table : Row = Null ! row : "  + rowNum);
				continue;
			}

			String cellValue="";
			try {
				DataFormatter hdf = new DataFormatter();

				Cell cellRef = row.getCell(Tools.property2ExcelColNumber("DevisArrow.reference"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowDesignation = row.getCell(Tools.property2ExcelColNumber("DevisArrow.designation"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowQuantite = row.getCell(Tools.property2ExcelColNumber("DevisArrow.quantite"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowPrixPublic = row.getCell(Tools.property2ExcelColNumber("DevisArrow.prixPublic"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowRemise = row.getCell(Tools.property2ExcelColNumber("DevisArrow.remise"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowPrixUnitaire = row.getCell(Tools.property2ExcelColNumber("DevisArrow.prixUnitaire"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowMontantHT = row.getCell(Tools.property2ExcelColNumber("DevisArrow.montantHT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell rowLibelleTotalHT = row.getCell(Tools.property2ExcelColNumber("DevisArrow.libelleTotalHT"), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

				String strCellRef = hdf.formatCellValue(cellRef);
				if (strCellRef.equals("Référence")) {
					quoteStructure.setHeaderIndex(rowNum);
					System.out.println("ReadExcelQuoteToTable - Référence row");
					continue;
				}
				if (Tools.getCellValueAsString(rowLibelleTotalHT).equals("TOTAL HT")) {
					quoteStructure.setTotalHTIndex(rowNum);
				}


				if (Tools.isCellEmpty(cellRef)  ||Tools.isCellEmpty(rowDesignation) || Tools.isCellEmpty(rowQuantite) || Tools.isCellEmpty(rowPrixPublic) || Tools.isCellEmpty(rowRemise) || Tools.isCellEmpty(rowPrixUnitaire) || Tools.isCellEmpty(rowMontantHT) ) {
					// System.out.println(" an empty row ! previous section is : " + section);
					if (!switchSection) {
						section = section + 1;
						switchSection = true;
						System.out.println("switchSection : " + switchSection);
					}
				} else {
					if (isFirstLine) {
						rowDesignationNumber = rowNum;
						quoteStructure.setPremiereLigneIndex(rowNum);
						quoteStructure.setDesignationIndex(rowNum-1);
						isFirstLine=false;
					}

					Double prixPublic = Tools.getCellValueAsNumeric(rowPrixPublic);
					if (prixPublic.equals(Double.valueOf(0)) || prixPublic.isNaN() || prixPublic.isInfinite()) {
						System.out.println("ReadExcelQuoteToTable - Double.valueOf(0) ");
						continue;
					}

					ExcelQuoteRow singleRowQuote = new ExcelQuoteRow(
							section,
							strCellRef,
							null,
							null,
							Tools.getCellValueAsString(rowDesignation),
							Tools.getCellValueAsNumeric(rowQuantite).intValue(),
							Tools.getCellValueAsNumeric(rowPrixPublic) ,
							Tools.getCellValueAsNumeric(rowRemise),
							Tools.getCellValueAsNumeric(rowPrixUnitaire),
							Tools.getCellValueAsNumeric(rowMontantHT),
							Double.valueOf(0), false, false, null);
					myQuote.add(singleRowQuote);
					// System.out.println(singleRowQuote.toString());
					switchSection = false;
				}

			} catch( NullPointerException NPE)	{
				//System.out.println("	NULL cell");
				NPE.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("readExcelQuoteToTable - readSheetIndex - error", e);
			}

		} //end of rows iterator

		// get designation devis
		try {
			Row designRow = sheet.getRow(rowDesignationNumber-1);
			// on récupère la désignation du devis
			if (designRow != null) {
				Integer col = Tools.convertExcelColumName2Number("C");
				Cell cellRef = designRow.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				// System.out.println("Designation matériel : " + Tools.getCellValueAsString(cellRef));
				this.designationDevis = Tools.getCellValueAsString(cellRef);
				quoteStructure.setDesignation(this.designationDevis);
			} else {
				//System.out.println("Row Designation is NULLLL " );
				this.designationDevis = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("readExcelQuoteToTable - readSheetIndex - error", e);
		}

		//workbook.close();
		// closeWorkbook();

		// } //end of sheets for loop

		//close file input stream
		//fis.close();

		//System.out.println("nombre de quotes rows  : " + myQuote.size() );
		quoteStructure.setQuote(myQuote);
		return quoteStructure;
	}

}
