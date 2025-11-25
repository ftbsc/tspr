package ftbsc.tspr.api.parser.types;

import com.google.auto.service.AutoService;
import ftbsc.tspr.api.parser.IParser;

@AutoService(IParser.class)
public class StringParser implements IParser<String> {
	@Override
	public Class<String> type() {
		return String.class;
	}

	@Override
	public String parse(String in) {
		return in;
	}
}
