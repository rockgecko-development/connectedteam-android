//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package au.com.connectedteam.appsapi.generated;

import java.util.ArrayList;
import net.servicestack.client.DataContract;
import net.servicestack.client.DataMember;

@DataContract
public class ResponseStatus {
    @DataMember(
        Order = 1
    )
    public String ErrorCode = null;
    @DataMember(
        Order = 2
    )
    public String Message = null;
    @DataMember(
        Order = 3
    )
    public String StackTrace = null;
    @DataMember(
        Order = 4
    )
    public ArrayList<ResponseError> Errors = null;

    public ResponseStatus() {
    }

    public String getErrorCode() {
        return this.ErrorCode;
    }

    public ResponseStatus setErrorCode(String value) {
        this.ErrorCode = value;
        return this;
    }

    public String getMessage() {
        return this.Message;
    }

    public ResponseStatus setMessage(String value) {
        this.Message = value;
        return this;
    }

    public String getStackTrace() {
        return this.StackTrace;
    }

    public ResponseStatus setStackTrace(String value) {
        this.StackTrace = value;
        return this;
    }

    public ArrayList<ResponseError> getErrors() {
        return this.Errors;
    }

    public ResponseStatus setErrors(ArrayList<ResponseError> value) {
        this.Errors = value;
        return this;
    }
}
