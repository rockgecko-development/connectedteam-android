/* Options:
Date: 2015-08-25 13:05:28
Version: 1
BaseUrl: http://192.168.5.29/EQPay.ServiceStack

Package: au.com.connectedteam.appsapi.generated
GlobalNamespace: dto
//AddPropertyAccessors: True
//SettersReturnThis: True
//AddServiceStackTypes: True
//AddResponseStatus: False
//AddImplicitVersion: 
//IncludeTypes: 
//ExcludeTypes: 
//DefaultImports: java.math.*,java.util.*,net.servicestack.client.*,com.google.gson.annotations.SerializedName
*/

package au.com.connectedteam.appsapi.generated;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.*;
import java.util.*;
import net.servicestack.client.*;

public class dto
{


    public static class ErrorInfo implements Serializable
    {
        public Integer ErrorNo = null;
        public String Message = null;
        public Integer Id = null;

        public Integer getErrorNo() { return ErrorNo; }
        public ErrorInfo setErrorNo(Integer value) { this.ErrorNo = value; return this; }
        public String getMessage() { return Message; }
        public ErrorInfo setMessage(String value) { this.Message = value; return this; }
        public Integer getId() { return Id; }
        public ErrorInfo setId(Integer value) { this.Id = value; return this; }
    }



    public static class LRole implements Serializable
    {
        public Integer UserRoleId = null;
        public Integer LRoleId = null;
        public String Role = null;
        public Integer LAccountTypeId = null;
        public Boolean IsSelected = null;
        public Boolean IsVisible = null;
        public Boolean IsAdmin = null;
        public LUserRole ELRole = null;
        public Integer Approval = null;
        public Date ApprovalDate = null;

        public Integer getUserRoleId() { return UserRoleId; }
        public LRole setUserRoleId(Integer value) { this.UserRoleId = value; return this; }
        public Integer getLRoleId() { return LRoleId; }
        public LRole setLRoleId(Integer value) { this.LRoleId = value; return this; }
        public String getRole() { return Role; }
        public LRole setRole(String value) { this.Role = value; return this; }
        public Integer getLAccountTypeId() { return LAccountTypeId; }
        public LRole setLAccountTypeId(Integer value) { this.LAccountTypeId = value; return this; }
        public Boolean getIsSelected() { return IsSelected; }
        public LRole setIsSelected(Boolean value) { this.IsSelected = value; return this; }
        public Boolean getIsVisible() { return IsVisible; }
        public LRole setIsVisible(Boolean value) { this.IsVisible = value; return this; }
        public Boolean getIsAdmin() { return IsAdmin; }
        public LRole setIsAdmin(Boolean value) { this.IsAdmin = value; return this; }
        public LUserRole getElRole() { return ELRole; }
        public LRole setElRole(LUserRole value) { this.ELRole = value; return this; }
        public Integer getApproval() { return Approval; }
        public LRole setApproval(Integer value) { this.Approval = value; return this; }
        public Date getApprovalDate() { return ApprovalDate; }
        public LRole setApprovalDate(Date value) { this.ApprovalDate = value; return this; }
    }

    public static class UserName implements Serializable
    {
        public String Email = null;
        public String MobileNo = null;
        public String Title = null;
        public String FirstName = null;
        public String Surname = null;
        public String UserNameDisplay = null;

        public String getEmail() { return Email; }
        public UserName setEmail(String value) { this.Email = value; return this; }
        public String getMobileNo() { return MobileNo; }
        public UserName setMobileNo(String value) { this.MobileNo = value; return this; }
        public String getTitle() { return Title; }
        public UserName setTitle(String value) { this.Title = value; return this; }
        public String getFirstName() { return FirstName; }
        public UserName setFirstName(String value) { this.FirstName = value; return this; }
        public String getSurname() { return Surname; }
        public UserName setSurname(String value) { this.Surname = value; return this; }
        public String getUserNameDisplay() { return UserNameDisplay; }
        public UserName setUserNameDisplay(String value) { this.UserNameDisplay = value; return this; }
    }




    public static enum LUserRole
    {
        @SerializedName("1") Trainer(1),
        @SerializedName("2") ServiceProvider(2),
        @SerializedName("3") Owner(3),
        @SerializedName("4") OwnerManager(4),
        @SerializedName("5") SystemAdmin(5);

        private final int value;
        LUserRole(final int intValue) { value = intValue; }
        public int getValue() { return value; }
    }

    public static class UserRole extends User
    {
        public Integer UserRoleId = null;
        public String Role = null;
        public Integer LRoleId = null;
        public LUserRole ELRole = null;
        public Integer Status = null;

        public Integer getUserRoleId() { return UserRoleId; }
        public UserRole setUserRoleId(Integer value) { this.UserRoleId = value; return this; }
        public String getRole() { return Role; }
        public UserRole setRole(String value) { this.Role = value; return this; }
        public Integer getLRoleId() { return LRoleId; }
        public UserRole setLRoleId(Integer value) { this.LRoleId = value; return this; }
        public LUserRole getElRole() { return ELRole; }
        public UserRole setElRole(LUserRole value) { this.ELRole = value; return this; }
        public Integer getStatus() { return Status; }
        public UserRole setStatus(Integer value) { this.Status = value; return this; }
    }


    public static class ResponseBase implements Serializable
    {
        public ErrorInfo ErrorInfo = null;
        public ResponseStatus ResponseStatus = null;

        public ErrorInfo getErrorInfo() { return ErrorInfo; }
        public ResponseBase setErrorInfo(ErrorInfo value) { this.ErrorInfo = value; return this; }
        public ResponseStatus getResponseStatus() { return ResponseStatus; }
        public ResponseBase setResponseStatus(ResponseStatus value) { this.ResponseStatus = value; return this; }
    }



    public static class User extends UserName
    {
        public Integer UserId = null;
        public Boolean IsTemporaryPassword = null;
        public Date AgreedTermsConditionDate = null;
        public Date SignupDate = null;
        public String Password = null;
        public String ConfirmPassword = null;
        public Integer MobilePIN = null;
        public Date DateOfBirth = null;
        public String ResidentialAddressNo = null;
        public String ResidentialAddress1 = null;
        public String ResidentialAddress2 = null;
        public String ResidentialSuburb = null;
        public String ResidentialStateCode = null;
        public String ResidentialPostCode = null;
        public String ResidentialCountryCode = null;
        public String PhoneNo = null;
        public Boolean AcceptsTerms = null;
        public Integer SecurityQuestionId = null;
        public String SecurityAnswer = null;
        public ArrayList<LRole> UserRole = null;
        public Boolean TrainingNotesEmail = null;
        public Boolean CalenderEventsEmail = null;
        public Boolean cbowner = null;
        public Boolean cbownermanager = null;
        public Boolean cbtrainer = null;
        public Boolean cbserviceprovider = null;
        public Integer AccountStatus = null;
        public Integer IsAdmin = null;
        public Integer RoleType = null;
        public String TrainerLicense = null;
        public Date TrainerLicenseDate = null;
        public String BusinessName = null;
        public String ABN = null;

        public Integer getUserId() { return UserId; }
        public User setUserId(Integer value) { this.UserId = value; return this; }
        public Boolean getIsTemporaryPassword() { return IsTemporaryPassword; }
        public User setIsTemporaryPassword(Boolean value) { this.IsTemporaryPassword = value; return this; }
        public Date getAgreedTermsConditionDate() { return AgreedTermsConditionDate; }
        public User setAgreedTermsConditionDate(Date value) { this.AgreedTermsConditionDate = value; return this; }
        public Date getSignupDate() { return SignupDate; }
        public User setSignupDate(Date value) { this.SignupDate = value; return this; }
        public String getPassword() { return Password; }
        public User setPassword(String value) { this.Password = value; return this; }
        public String getConfirmPassword() { return ConfirmPassword; }
        public User setConfirmPassword(String value) { this.ConfirmPassword = value; return this; }
        public Integer getMobilePIN() { return MobilePIN; }
        public User setMobilePIN(Integer value) { this.MobilePIN = value; return this; }
        public Date getDateOfBirth() { return DateOfBirth; }
        public User setDateOfBirth(Date value) { this.DateOfBirth = value; return this; }
        public String getResidentialAddressNo() { return ResidentialAddressNo; }
        public User setResidentialAddressNo(String value) { this.ResidentialAddressNo = value; return this; }
        public String getResidentialAddress1() { return ResidentialAddress1; }
        public User setResidentialAddress1(String value) { this.ResidentialAddress1 = value; return this; }
        public String getResidentialAddress2() { return ResidentialAddress2; }
        public User setResidentialAddress2(String value) { this.ResidentialAddress2 = value; return this; }
        public String getResidentialSuburb() { return ResidentialSuburb; }
        public User setResidentialSuburb(String value) { this.ResidentialSuburb = value; return this; }
        public String getResidentialStateCode() { return ResidentialStateCode; }
        public User setResidentialStateCode(String value) { this.ResidentialStateCode = value; return this; }
        public String getResidentialPostCode() { return ResidentialPostCode; }
        public User setResidentialPostCode(String value) { this.ResidentialPostCode = value; return this; }
        public String getResidentialCountryCode() { return ResidentialCountryCode; }
        public User setResidentialCountryCode(String value) { this.ResidentialCountryCode = value; return this; }
        public String getPhoneNo() { return PhoneNo; }
        public User setPhoneNo(String value) { this.PhoneNo = value; return this; }
        public Boolean isAcceptsTerms() { return AcceptsTerms; }
        public User setAcceptsTerms(Boolean value) { this.AcceptsTerms = value; return this; }
        public Integer getSecurityQuestionId() { return SecurityQuestionId; }
        public User setSecurityQuestionId(Integer value) { this.SecurityQuestionId = value; return this; }
        public String getSecurityAnswer() { return SecurityAnswer; }
        public User setSecurityAnswer(String value) { this.SecurityAnswer = value; return this; }
        public ArrayList<LRole> getUserRole() { return UserRole; }
        public User setUserRole(ArrayList<LRole> value) { this.UserRole = value; return this; }
        public Boolean isTrainingNotesEmail() { return TrainingNotesEmail; }
        public User setTrainingNotesEmail(Boolean value) { this.TrainingNotesEmail = value; return this; }
        public Boolean isCalenderEventsEmail() { return CalenderEventsEmail; }
        public User setCalenderEventsEmail(Boolean value) { this.CalenderEventsEmail = value; return this; }
        public Boolean isCbowner() { return cbowner; }
        public User setCbowner(Boolean value) { this.cbowner = value; return this; }
        public Boolean isCbownermanager() { return cbownermanager; }
        public User setCbownermanager(Boolean value) { this.cbownermanager = value; return this; }
        public Boolean isCbtrainer() { return cbtrainer; }
        public User setCbtrainer(Boolean value) { this.cbtrainer = value; return this; }
        public Boolean isCbserviceprovider() { return cbserviceprovider; }
        public User setCbserviceprovider(Boolean value) { this.cbserviceprovider = value; return this; }
        public Integer getAccountStatus() { return AccountStatus; }
        public User setAccountStatus(Integer value) { this.AccountStatus = value; return this; }
        public Integer getIsAdmin() { return IsAdmin; }
        public User setIsAdmin(Integer value) { this.IsAdmin = value; return this; }
        public Integer getRoleType() { return RoleType; }
        public User setRoleType(Integer value) { this.RoleType = value; return this; }
        public String getTrainerLicense() { return TrainerLicense; }
        public User setTrainerLicense(String value) { this.TrainerLicense = value; return this; }
        public Date getTrainerLicenseDate() { return TrainerLicenseDate; }
        public User setTrainerLicenseDate(Date value) { this.TrainerLicenseDate = value; return this; }
        public String getBusinessName() { return BusinessName; }
        public User setBusinessName(String value) { this.BusinessName = value; return this; }
        public String getAbn() { return ABN; }
        public User setAbn(String value) { this.ABN = value; return this; }
    }



    public static class SessionUserInfo extends UserName
    {
        public Integer UserId = null;
        public ArrayList<LRole> UserRole = null;
        public LRole SelectedRole = null;

        public Integer getUserId() { return UserId; }
        public SessionUserInfo setUserId(Integer value) { this.UserId = value; return this; }
        public ArrayList<LRole> getUserRole() { return UserRole; }
        public SessionUserInfo setUserRole(ArrayList<LRole> value) { this.UserRole = value; return this; }
        public LRole getSelectedRole() { return SelectedRole; }
        public SessionUserInfo setSelectedRole(LRole value) { this.SelectedRole = value; return this; }
    }





    @DataContract
    public static class AuthenticateResponse implements Serializable
    {
        @DataMember(Order=1)
        public String UserId = null;

        @DataMember(Order=2)
        public String SessionId = null;

        @DataMember(Order=3)
        public String UserName = null;

        @DataMember(Order=4)
        public String DisplayName = null;

        @DataMember(Order=5)
        public String ReferrerUrl = null;

        @DataMember(Order=6)
        public ResponseStatus ResponseStatus = null;

        @DataMember(Order=7)
        public HashMap<String,String> Meta = null;

        public String getUserId() { return UserId; }
        public AuthenticateResponse setUserId(String value) { this.UserId = value; return this; }
        public String getSessionId() { return SessionId; }
        public AuthenticateResponse setSessionId(String value) { this.SessionId = value; return this; }
        public String getUserName() { return UserName; }
        public AuthenticateResponse setUserName(String value) { this.UserName = value; return this; }
        public String getDisplayName() { return DisplayName; }
        public AuthenticateResponse setDisplayName(String value) { this.DisplayName = value; return this; }
        public String getReferrerUrl() { return ReferrerUrl; }
        public AuthenticateResponse setReferrerUrl(String value) { this.ReferrerUrl = value; return this; }
        public ResponseStatus getResponseStatus() { return ResponseStatus; }
        public AuthenticateResponse setResponseStatus(ResponseStatus value) { this.ResponseStatus = value; return this; }
        public HashMap<String,String> getMeta() { return Meta; }
        public AuthenticateResponse setMeta(HashMap<String,String> value) { this.Meta = value; return this; }
    }

    public static class AssignRolesResponse implements Serializable
    {
        public ArrayList<String> AllRoles = null;
        public ArrayList<String> AllPermissions = null;
        public ResponseStatus ResponseStatus = null;

        public ArrayList<String> getAllRoles() { return AllRoles; }
        public AssignRolesResponse setAllRoles(ArrayList<String> value) { this.AllRoles = value; return this; }
        public ArrayList<String> getAllPermissions() { return AllPermissions; }
        public AssignRolesResponse setAllPermissions(ArrayList<String> value) { this.AllPermissions = value; return this; }
        public ResponseStatus getResponseStatus() { return ResponseStatus; }
        public AssignRolesResponse setResponseStatus(ResponseStatus value) { this.ResponseStatus = value; return this; }
    }

    public static class UnAssignRolesResponse implements Serializable
    {
        public ArrayList<String> AllRoles = null;
        public ArrayList<String> AllPermissions = null;
        public ResponseStatus ResponseStatus = null;

        public ArrayList<String> getAllRoles() { return AllRoles; }
        public UnAssignRolesResponse setAllRoles(ArrayList<String> value) { this.AllRoles = value; return this; }
        public ArrayList<String> getAllPermissions() { return AllPermissions; }
        public UnAssignRolesResponse setAllPermissions(ArrayList<String> value) { this.AllPermissions = value; return this; }
        public ResponseStatus getResponseStatus() { return ResponseStatus; }
        public UnAssignRolesResponse setResponseStatus(ResponseStatus value) { this.ResponseStatus = value; return this; }
    }







    @Route("/account/signup")
    public static class Signup implements IReturn<ResponseBase>
    {
        public User User = null;

        public User getUser() { return User; }
        public Signup setUser(User value) { this.User = value; return this; }
        private static Object responseType = ResponseBase.class;
        public Object getResponseType() { return responseType; }
    }







    @Route("/auth")
    // @Route("/auth/{provider}")
    // @Route("/authenticate")
    // @Route("/authenticate/{provider}")
    @DataContract
    public static class Authenticate implements IReturn<AuthenticateResponse>
    {
        @DataMember(Order=1)
        public String provider = null;

        @DataMember(Order=2)
        public String State = null;

        @DataMember(Order=3)
        public String oauth_token = null;

        @DataMember(Order=4)
        public String oauth_verifier = null;

        @DataMember(Order=5)
        public String UserName = null;

        @DataMember(Order=6)
        public String Password = null;

        @DataMember(Order=7)
        public Boolean RememberMe = null;

        @DataMember(Order=8)
        public String Continue = null;

        @DataMember(Order=9)
        public String nonce = null;

        @DataMember(Order=10)
        public String uri = null;

        @DataMember(Order=11)
        public String response = null;

        @DataMember(Order=12)
        public String qop = null;

        @DataMember(Order=13)
        public String nc = null;

        @DataMember(Order=14)
        public String cnonce = null;

        @DataMember(Order=15)
        public HashMap<String,String> Meta = null;

        public String getProvider() { return provider; }
        public Authenticate setProvider(String value) { this.provider = value; return this; }
        public String getState() { return State; }
        public Authenticate setState(String value) { this.State = value; return this; }
        public String getOauthToken() { return oauth_token; }
        public Authenticate setOauthToken(String value) { this.oauth_token = value; return this; }
        public String getOauthVerifier() { return oauth_verifier; }
        public Authenticate setOauthVerifier(String value) { this.oauth_verifier = value; return this; }
        public String getUserName() { return UserName; }
        public Authenticate setUserName(String value) { this.UserName = value; return this; }
        public String getPassword() { return Password; }
        public Authenticate setPassword(String value) { this.Password = value; return this; }
        public Boolean isRememberMe() { return RememberMe; }
        public Authenticate setRememberMe(Boolean value) { this.RememberMe = value; return this; }
        public String getContinue() { return Continue; }
        public Authenticate setContinue(String value) { this.Continue = value; return this; }
        public String getNonce() { return nonce; }
        public Authenticate setNonce(String value) { this.nonce = value; return this; }
        public String getUri() { return uri; }
        public Authenticate setUri(String value) { this.uri = value; return this; }
        public String getResponse() { return response; }
        public Authenticate setResponse(String value) { this.response = value; return this; }
        public String getQop() { return qop; }
        public Authenticate setQop(String value) { this.qop = value; return this; }
        public String getNc() { return nc; }
        public Authenticate setNc(String value) { this.nc = value; return this; }
        public String getCnonce() { return cnonce; }
        public Authenticate setCnonce(String value) { this.cnonce = value; return this; }
        public HashMap<String,String> getMeta() { return Meta; }
        public Authenticate setMeta(HashMap<String,String> value) { this.Meta = value; return this; }
        private static Object responseType = AuthenticateResponse.class;
        public Object getResponseType() { return responseType; }
    }

    @Route("/assignroles")
    public static class AssignRoles implements IReturn<AssignRolesResponse>
    {
        public String UserName = null;
        public ArrayList<String> Permissions = null;
        public ArrayList<String> Roles = null;

        public String getUserName() { return UserName; }
        public AssignRoles setUserName(String value) { this.UserName = value; return this; }
        public ArrayList<String> getPermissions() { return Permissions; }
        public AssignRoles setPermissions(ArrayList<String> value) { this.Permissions = value; return this; }
        public ArrayList<String> getRoles() { return Roles; }
        public AssignRoles setRoles(ArrayList<String> value) { this.Roles = value; return this; }
        private static Object responseType = AssignRolesResponse.class;
        public Object getResponseType() { return responseType; }
    }

    @Route("/unassignroles")
    public static class UnAssignRoles implements IReturn<UnAssignRolesResponse>
    {
        public String UserName = null;
        public ArrayList<String> Permissions = null;
        public ArrayList<String> Roles = null;

        public String getUserName() { return UserName; }
        public UnAssignRoles setUserName(String value) { this.UserName = value; return this; }
        public ArrayList<String> getPermissions() { return Permissions; }
        public UnAssignRoles setPermissions(ArrayList<String> value) { this.Permissions = value; return this; }
        public ArrayList<String> getRoles() { return Roles; }
        public UnAssignRoles setRoles(ArrayList<String> value) { this.Roles = value; return this; }
        private static Object responseType = UnAssignRolesResponse.class;
        public Object getResponseType() { return responseType; }
    }

}
