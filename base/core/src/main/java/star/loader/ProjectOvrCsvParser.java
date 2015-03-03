package star.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.common.base.Throwables;

import eu.europa.emsa.schemas.cdf.v_0_5_8_1.projectovr.ObjectFactory;
import eu.europa.emsa.schemas.cdf.v_0_5_8_1.projectovr.ProjectOvrMessageType;
import eu.europa.emsa.schemas.cdf.v_0_5_8_1.projectovr.ProjectOvrRootType;

public class ProjectOvrCsvParser {
	
	public enum Fields {
		MMSI,
		IR,
		IRCS,
		
		
	}
	
	private static final ObjectFactory of = new ObjectFactory();
	private static DatatypeFactory dtf;
	
	{
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static JAXBElement<ProjectOvrRootType> parse(
			InputStream in,
			String id,
			String source
	) {
		try {
			
			ProjectOvrRootType root = of.createProjectOvrRootType();
			
			root.setTimestamp(dtf.newXMLGregorianCalendar(new GregorianCalendar()));
			root.setId(id);
			root.setSource(source);
			
			CSVParser parser = new CSVParser(new InputStreamReader(in), CSVFormat.EXCEL);

			try {
				for (CSVRecord record : parser) {
					
					ProjectOvrMessageType msg = of.createProjectOvrMessageType();
					root.getProjectOvrMessage().add(msg);
					
					msg.setMMSI(getInteger(record, Fields.MMSI));
					
				}
			} finally {
				parser.close();
			}
			
			
			return of.createEMSA(root);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static Integer getInteger(CSVRecord record, Enum<?> fieldName) {
		String value = getString(record, fieldName);
		if (value==null) {
			return null;
		} else {
			return Integer.parseInt(value);
		}
	}
	
	public static String getString(CSVRecord record, Enum<?> fieldName) {
		return record.get(fieldName);
	}
	
}
