package ftbsc.tspr.api.parser.types;

import com.google.auto.service.AutoService;
import ftbsc.tspr.api.parser.IParser;

@AutoService(IParser.class)
public class IntegerParser implements IParser<Integer> {
	@Override
	public Class<Integer> type() {
		return Integer.class;
	}

	@Override
	public Integer parse(String in) {
		return Integer.parseInt(in);
	}
}
