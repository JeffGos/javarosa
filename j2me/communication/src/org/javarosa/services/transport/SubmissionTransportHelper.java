/**
 * 
 */
package org.javarosa.services.transport;

import java.io.IOException;

import org.javarosa.core.model.SubmissionProfile;
import org.javarosa.core.model.instance.FormInstance;
import org.javarosa.core.services.transport.payload.IDataPayload;
import org.javarosa.model.xform.SMSSerializingVisitor;
import org.javarosa.model.xform.XFormSerializingVisitor;
import org.javarosa.model.xform.XPathReference;
import org.javarosa.services.transport.impl.simplehttp.SimpleHttpTransportMessage;
import org.javarosa.services.transport.impl.sms.SMSTransportMessage;

/**
 * @author ctsims
 *
 */
public class SubmissionTransportHelper {
	
	public static SubmissionProfile defaultPostSubmission(String url) {
		return new SubmissionProfile(new XPathReference("/"), "post", url, null);
	}
	
	public static TransportMessage createMessage(FormInstance instance, SubmissionProfile profile) {
		//If there is a submission profile, we need to use the relevant portions.
		if(profile.getMethod().toLowerCase().equals("post")) {
			
			//URL
			String url = profile.getAction();
			
			try {
				IDataPayload payload = new XFormSerializingVisitor().createSerializedPayload(instance, profile.getRef());
				return new SimpleHttpTransportMessage(payload.getPayloadStream(),url);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Error Serializing Data to be transported");
			}
		} else if(profile.getMethod().toLowerCase().equals("smspush")) {
			
			//URL
			String phoneUri = profile.getAction();
			
			try {
				String payload = new String(new SMSSerializingVisitor().serializeInstance(instance, profile.getRef()));
				return new SMSTransportMessage(payload,phoneUri);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Error Serializing Data to be transported");
			}
		}
		return null;
	}
}