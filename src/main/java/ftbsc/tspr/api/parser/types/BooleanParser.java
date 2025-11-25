package ftbsc.tspr.api.parser.types;

import com.google.auto.service.AutoService;
import ftbsc.tspr.api.parser.IParser;

@AutoService(IParser.class)
public class BooleanParser implements IParser<Boolean> {
	@Override
	public Class<Boolean> type() {
		return Boolean.class;
	}

	@Override
	public Boolean parse(String in) {
		return Boolean.parseBoolean(in);
	}
}
