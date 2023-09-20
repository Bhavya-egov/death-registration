package digit.Enrichment;

import digit.Service.UserService;
//import digit.models.coremodels.UserDetailResponse;
//import digit.service.UserService;
//import digit.utils.IdgenUtil;
//import digit.utils.UserUtil;
import digit.util.IdgenUtil;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
@Slf4j
public class DeathApplicationEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;
//
    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtils;

    public void enrichDeathApplication(DeathRegistrationRequest deathRegistrationRequest) {
        //Retrieve list of IDs from IDGen service
        List<String> deathRegistrationIdList = idgenUtil.getIdList(deathRegistrationRequest.getRequestInfo(), deathRegistrationRequest.getDeathRegistrationApplications().get(0).getTenantId(), "dtr.registrationid", "", deathRegistrationRequest.getDeathRegistrationApplications().size());
        Integer index = 0;
        for(DeathRegistrationApplication application : deathRegistrationRequest.getDeathRegistrationApplications()) {
            // Enrich audit details
            AuditDetails auditDetails = AuditDetails.builder().createdBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

            // Enrich UUID
            application.setId(UUID.randomUUID().toString());

            // Set application number from IdGen
            application.setApplicationNumber(deathRegistrationIdList.get(index++));

            // Enrich registration Id
            application.getAddressOfDeceased().setRegistrationId(application.getId());

            // Enrich address UUID
            application.getAddressOfDeceased().setId(UUID.randomUUID().toString());
            System.out.println(application.getAddressOfDeceased().getId());
        }
    }

    public void enrichDeathApplicationUponUpdate(DeathRegistrationRequest deathRegistrationRequest) {
        // Enrich lastModifiedTime and lastModifiedBy in case of update
        deathRegistrationRequest.getDeathRegistrationApplications().get(0).getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        deathRegistrationRequest.getDeathRegistrationApplications().get(0).getAuditDetails().setLastModifiedBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
    }

    public void enrichApplicantOnSearch(DeathRegistrationApplication application) {
        UserDetailResponse ApplicantUserResponse = userService.searchUser(userUtils.getStateLevelTenant(application.getTenantId()),application.getApplicant().getUuid(),null);
        User ApplicantUser = ApplicantUserResponse.getUser().get(0);
        log.info(ApplicantUser.toString());
        Applicant applicant = Applicant.builder().aadhaarNumber(ApplicantUser.getAadhaarNumber())
                .accountLocked(ApplicantUser.getAccountLocked())
                .active(ApplicantUser.getActive())
//                .altContactNumber(ApplicantUser.getAltContactNumber())
                .bloodGroup(ApplicantUser.getBloodGroup())
                .gender(ApplicantUser.getGender())
                .id(ApplicantUser.getId())
                .name(ApplicantUser.getName())
                .type(ApplicantUser.getType())
                .roles(ApplicantUser.getRoles()).build();
        application.setApplicant(applicant);
    }

//    public void enrichMotherApplicantOnSearch(DeathRegistrationApplication application) {
//        UserDetailResponse motherUserResponse = userService.searchUser(userUtils.getStateLevelTenant(application.getTenantId()),application.getFather().getId(),null);
//        User motherUser = motherUserResponse.getUser().get(0);
//        log.info(motherUser.toString());
//        MotherApplicant motherApplicant = MotherApplicant.builder().aadhaarNumber(motherUser.getAadhaarNumber())
//                .accountLocked(motherUser.getAccountLocked())
//                .active(motherUser.getActive())
//                .altContactNumber(motherUser.getAltContactNumber())
//                .bloodGroup(motherUser.getBloodGroup())
//                .correspondenceAddress(motherUser.getCorrespondenceAddress())
//                .correspondenceCity(motherUser.getCorrespondenceCity())
//                .correspondencePincode(motherUser.getCorrespondencePincode())
//                .gender(motherUser.getGender())
//                .id(motherUser.getUuid())
//                .name(motherUser.getName())
//                .type(motherUser.getType())
//                .roles(motherUser.getRoles()).build();
//        application.setMother(motherApplicant);
//    }
}