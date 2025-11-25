package ftbsc.tspr.api.parser.types;

import com.google.auto.service.AutoService;
import ftbsc.tspr.api.parser.IParser;

@AutoService(IParser.class)
public class DoubleParser implements IParser<Double> {
	@Override
	public Class<Double> type() {
		return Double.class;
	}

	@Override
	public Double parse(String in) {
		return Double.parseDouble(in);
	}
}
