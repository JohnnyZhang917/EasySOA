package org.easysoa.template.setters;

import org.apache.log4j.Logger;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateField.TemplateFieldType;

import com.openwide.easysoa.message.InMessage;

public class RestPathParamSetter implements CustomParamSetter {

	// Logger
	private static Logger logger = Logger.getLogger(RestPathParamSetter.class.getName());
	
	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#isOkFor()
	 */
	@Override
	public boolean isOkFor(TemplateField templateField) {
		if(TemplateFieldType.PATH_PARAM.equals(templateField.getParamType())){
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#setParams()
	 */
	@Override
	public void setParam(TemplateField templateField, InMessage inMessage, Map<String, List<String>> params) {
		logger.debug("Set path param for " + templateField.getFieldName());
		String path = inMessage.getPath();
		logger.debug("path = " + path);
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		StringBuffer buffer = new StringBuffer();
		int position = 1;
		String token;
		while(tokenizer.hasMoreTokens()){
			buffer.append("/");
			token = tokenizer.nextToken();
			if(position == templateField.getPathParamPosition()){
				buffer.append(params.get(templateField.getFieldName()).get(0));
			} else {
				buffer.append(token);
			}
			position++;
		}
		inMessage.setPath(buffer.toString());
	}

}