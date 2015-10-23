//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package au.com.connectedteam.appsapi.generated;

import net.servicestack.client.DataContract;
import net.servicestack.client.DataMember;

@DataContract
public class ResponseError {
    @DataMember(
        Order = 1,
        EmitDefaultValue = false
    )
    public String ErrorCode = null;
    @DataMember(
        Order = 2,
        EmitDefaultValue = false
    )
    public String FieldName = null;
    @DataMember(
        Order = 3,
        EmitDefaultValue = false
    )
    public String Message = null;

    public ResponseError() {
    }

    public String getErrorCode() {
        return this.ErrorCode;
    }

    public ResponseError setErrorCode(String value) {
        this.ErrorCode = value;
        return this;
    }

    public String getFieldName() {
        return this.FieldName;
    }

    public ResponseError setFieldName(String value) {
        this.FieldName = value;
        return this;
    }

    public String getMessage() {
        return this.Message;
    }

    public ResponseError setMessage(String value) {
        this.Message = value;
        return this;
    }
}
